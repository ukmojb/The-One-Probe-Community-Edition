package mcjty.theoneprobe.rendering;

import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.apiimpl.elements.ElementVertical;
import mcjty.theoneprobe.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Arranges the top-level probe elements in contiguous, height-balanced columns.
 */
final class ProbeInfoColumnLayout {

    private static final int COLUMN_SPACING = 6;
    private static final double MARQUEE_PIXELS_PER_SECOND = 12.0;
    private static final double MARQUEE_EDGE_PAUSE_SECONDS = 1.0;

    private final List<IElement> headerElements;
    private final int headerHeight;
    private final List<Column> columns;
    private final int columnsWidth;
    private final int columnsHeight;
    private final int columnsContentHeight;
    private final boolean scrolling;
    private final boolean centerFirstHeader;
    private final int screenWidth;
    private final int screenHeight;
    private final long scrollElapsedMillis;
    private final int width;
    private final int height;

    private ProbeInfoColumnLayout(List<IElement> headerElements, List<Column> columns) {
        this(headerElements, columns, false, true, Integer.MAX_VALUE, 1, 1, 0L);
    }

    private ProbeInfoColumnLayout(List<IElement> headerElements, List<Column> columns, boolean scrolling,
                                  boolean centerFirstHeader, int maxVisibleHeight, int screenWidth, int screenHeight,
                                  long scrollElapsedMillis) {
        this.headerElements = headerElements;
        this.columns = columns;
        this.scrolling = scrolling;
        this.centerFirstHeader = centerFirstHeader;
        this.screenWidth = Math.max(1, screenWidth);
        this.screenHeight = Math.max(1, screenHeight);
        this.scrollElapsedMillis = Math.max(0L, scrollElapsedMillis);

        int totalWidth = 0;
        int maxHeight = 0;
        for (Column column : columns) {
            totalWidth += column.width;
            maxHeight = Math.max(maxHeight, column.height);
        }
        columnsWidth = totalWidth + COLUMN_SPACING * Math.max(0, columns.size() - 1);
        columnsContentHeight = maxHeight;
        columnsHeight = scrolling ? Math.min(maxHeight, Math.max(0, maxVisibleHeight)) : maxHeight;

        int maxHeaderWidth = 0;
        for (IElement headerElement : headerElements) {
            maxHeaderWidth = Math.max(maxHeaderWidth, headerElement.getWidth());
        }
        headerHeight = getElementsHeight(headerElements);

        if (headerElements.isEmpty()) {
            width = columnsWidth;
            height = columnsHeight;
        } else {
            width = Math.max(maxHeaderWidth, columnsWidth);
            int totalHeight = headerHeight;
            if (columnsHeight > 0) {
                totalHeight += ElementVertical.SPACING + columnsHeight;
            }
            height = totalHeight;
        }
    }

    static ProbeInfoColumnLayout create(List<IElement> elements, int elementChangeHeaderCount, int screenWidth,
                                        int screenHeight, String autoWrapMode, float maxColumnHeightFraction,
                                        int maxScrollElements) {
        return create(elements, elementChangeHeaderCount, screenWidth, screenHeight, autoWrapMode,
                maxColumnHeightFraction, maxScrollElements, 0L);
    }

    static ProbeInfoColumnLayout create(List<IElement> elements, int elementChangeHeaderCount, int screenWidth,
                                        int screenHeight, String autoWrapMode, float maxColumnHeightFraction,
                                        int maxScrollElements, long scrollElapsedMillis) {
        if (elements.isEmpty()) {
            return new ProbeInfoColumnLayout(Collections.emptyList(), emptyColumns());
        }

        if (Config.AUTO_WRAP_ELEMENT_CHANGE.equals(autoWrapMode)
                || Config.AUTO_WRAP_ELEMENT_SCROLL.equals(autoWrapMode)) {
            int headerCount = Math.max(0, Math.min(elementChangeHeaderCount, elements.size()));
            List<IElement> header = new ArrayList<>(elements.subList(0, headerCount));
            List<IElement> bodyElements = new ArrayList<>(elements.subList(headerCount, elements.size()));
            if (Config.AUTO_WRAP_ELEMENT_SCROLL.equals(autoWrapMode)) {
                List<Column> columns = Collections.singletonList(new Column(bodyElements));
                int visibleHeight = getElementsHeight(bodyElements, Math.max(1, maxScrollElements));
                return new ProbeInfoColumnLayout(header, columns, true, false, visibleHeight, screenWidth, screenHeight,
                        scrollElapsedMillis);
            }
            return new ProbeInfoColumnLayout(header,
                    createColumns(bodyElements, screenHeight, true, maxColumnHeightFraction));
        }

        boolean autoWrap = Config.AUTO_WRAP_ALL_CHANGE.equals(autoWrapMode);
        return new ProbeInfoColumnLayout(Collections.emptyList(),
                createColumns(elements, screenHeight, autoWrap, maxColumnHeightFraction));
    }

    private static List<Column> createColumns(List<IElement> elements, int screenHeight,
                                              boolean autoWrap, float maxColumnHeightFraction) {
        if (elements.isEmpty()) {
            return emptyColumns();
        }

        int columnCount = 1;
        if (autoWrap) {
            int totalHeight = getElementsHeight(elements);
            double targetHeight = Math.max(1.0, screenHeight * (double) maxColumnHeightFraction);
            columnCount = Math.max(1, (int) Math.ceil(totalHeight / targetHeight));
            // Elements are the smallest units that can be moved without changing an add-on's layout.
            columnCount = Math.min(columnCount, elements.size());
        }

        return partition(elements, columnCount);
    }

    private static List<Column> emptyColumns() {
        return Collections.singletonList(new Column(Collections.emptyList()));
    }

    private static List<Column> partition(List<IElement> elements, int columnCount) {
        List<Column> result = new ArrayList<>(columnCount);
        int nextElement = 0;

        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            int remainingColumns = columnCount - columnIndex;
            if (remainingColumns == 1) {
                result.add(new Column(new ArrayList<>(elements.subList(nextElement, elements.size()))));
                break;
            }

            int remainingHeight = getElementsHeight(elements.subList(nextElement, elements.size()));
            double targetHeight = remainingHeight / (double) remainingColumns;
            int end = nextElement;
            int columnHeight = 0;

            while (end < elements.size() - (remainingColumns - 1)) {
                int elementHeight = elements.get(end).getHeight();
                int candidateHeight = columnHeight == 0
                        ? elementHeight
                        : columnHeight + ElementVertical.SPACING + elementHeight;

                if (columnHeight > 0
                        && Math.abs(targetHeight - columnHeight) <= Math.abs(targetHeight - candidateHeight)) {
                    break;
                }

                columnHeight = candidateHeight;
                end++;
            }

            // Always put at least one element in every column.
            if (end == nextElement) {
                end++;
            }
            result.add(new Column(new ArrayList<>(elements.subList(nextElement, end))));
            nextElement = end;
        }

        return result;
    }

    private static int getElementsHeight(List<IElement> elements) {
        return getElementsHeight(elements, elements.size());
    }

    private static int getElementsHeight(List<IElement> elements, int maxElements) {
        int count = Math.min(elements.size(), maxElements);
        if (count == 0) {
            return 0;
        }

        int result = 0;
        for (int index = 0; index < count; index++) {
            result += elements.get(index).getHeight();
        }
        return result + ElementVertical.SPACING * (count - 1);
    }

    static float marqueeOffset(double contentHeight, double visibleHeight, long elapsedMillis) {
        double overflow = contentHeight - visibleHeight;
        if (overflow <= 0) {
            return 0.0f;
        }

        double seconds = Math.max(0L, elapsedMillis) / 1000.0;
        double travelSeconds = overflow / MARQUEE_PIXELS_PER_SECOND;
        double cycleSeconds = MARQUEE_EDGE_PAUSE_SECONDS * 2.0 + travelSeconds * 2.0;
        double position = seconds % cycleSeconds;
        if (position < MARQUEE_EDGE_PAUSE_SECONDS) {
            return 0.0f;
        }

        position -= MARQUEE_EDGE_PAUSE_SECONDS;
        if (position < travelSeconds) {
            return (float) (position * MARQUEE_PIXELS_PER_SECOND);
        }
        if (position < travelSeconds + MARQUEE_EDGE_PAUSE_SECONDS) {
            return (float) overflow;
        }

        position -= travelSeconds + MARQUEE_EDGE_PAUSE_SECONDS;
        return (float) (overflow - position * MARQUEE_PIXELS_PER_SECOND);
    }

    private static IntBuffer getScissorBox() {
        IntBuffer scissor = BufferUtils.createIntBuffer(4);
        GL11.glGetInteger(GL11.GL_SCISSOR_BOX, scissor);
        return scissor;
    }

    private void setScissor(int x, int y, int width, int height, IntBuffer previousScissor) {
        Minecraft minecraft = Minecraft.getMinecraft();
        double scaleX = minecraft.displayWidth / (double) screenWidth;
        double scaleY = minecraft.displayHeight / (double) screenHeight;
        int scissorX = (int) Math.floor(x * scaleX);
        int scissorY = (int) Math.floor(minecraft.displayHeight - (y + height) * scaleY);
        int scissorWidth = Math.max(0, (int) Math.ceil(width * scaleX));
        int scissorHeight = Math.max(0, (int) Math.ceil(height * scaleY));
        if (previousScissor != null) {
            int left = Math.max(scissorX, previousScissor.get(0));
            int bottom = Math.max(scissorY, previousScissor.get(1));
            int right = Math.min(scissorX + scissorWidth, previousScissor.get(0) + previousScissor.get(2));
            int top = Math.min(scissorY + scissorHeight, previousScissor.get(1) + previousScissor.get(3));
            scissorX = left;
            scissorY = bottom;
            scissorWidth = Math.max(0, right - left);
            scissorHeight = Math.max(0, top - bottom);
        }
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(scissorX, scissorY, scissorWidth, scissorHeight);
    }

    private void renderColumns(int x, int y) {
        for (Column column : columns) {
            int elementY = y;
            for (IElement element : column.elements) {
                element.render(x, elementY);
                elementY += element.getHeight() + ElementVertical.SPACING;
            }
            x += column.width + COLUMN_SPACING;
        }
    }

    private void renderScrollingColumns(int x, int y) {
        if (columnsContentHeight <= columnsHeight) {
            renderColumns(x, y);
            return;
        }

        float offset = marqueeOffset(columnsContentHeight, columnsHeight, scrollElapsedMillis);
        boolean restoreScissor = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
        IntBuffer previousScissor = restoreScissor ? getScissorBox() : null;
        setScissor(x, y, columnsWidth, columnsHeight, previousScissor);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, -offset, 0.0f);
        try {
            renderColumns(x, y);
        } finally {
            GlStateManager.popMatrix();
            if (previousScissor == null) {
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            } else {
                GL11.glScissor(previousScissor.get(0), previousScissor.get(1),
                        previousScissor.get(2), previousScissor.get(3));
            }
        }
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }

    int getColumnCount() {
        return columns.size();
    }

    void render(int x, int y) {
        if (!headerElements.isEmpty()) {
            for (IElement headerElement : headerElements) {
                int headerX = centerFirstHeader && headerElement == headerElements.get(0)
                        ? x + (width - headerElement.getWidth()) / 2
                        : x;
                headerElement.render(headerX, y);
                y += headerElement.getHeight() + ElementVertical.SPACING;
            }
            if (columnsHeight == 0) {
                return;
            }
        }

        if (scrolling) {
            renderScrollingColumns(x, y);
        } else {
            renderColumns(x, y);
        }
    }

    private static final class Column {
        private final List<IElement> elements;
        private final int width;
        private final int height;

        private Column(List<IElement> elements) {
            this.elements = elements;

            int maxWidth = 0;
            for (IElement element : elements) {
                maxWidth = Math.max(maxWidth, element.getWidth());
            }
            width = maxWidth;
            height = getElementsHeight(elements);
        }
    }
}
