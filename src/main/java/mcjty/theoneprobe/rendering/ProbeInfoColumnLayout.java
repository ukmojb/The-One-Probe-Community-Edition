package mcjty.theoneprobe.rendering;

import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.apiimpl.elements.ElementVertical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Arranges the top-level probe elements in contiguous, height-balanced columns.
 */
final class ProbeInfoColumnLayout {

    private static final int COLUMN_SPACING = 6;

    private final List<Column> columns;
    private final int width;
    private final int height;

    private ProbeInfoColumnLayout(List<Column> columns) {
        this.columns = columns;

        int totalWidth = 0;
        int maxHeight = 0;
        for (Column column : columns) {
            totalWidth += column.width;
            maxHeight = Math.max(maxHeight, column.height);
        }
        width = totalWidth + COLUMN_SPACING * Math.max(0, columns.size() - 1);
        height = maxHeight;
    }

    static ProbeInfoColumnLayout create(List<IElement> elements, int screenHeight,
                                        boolean autoWrap, float maxColumnHeightFraction) {
        if (elements.isEmpty()) {
            return new ProbeInfoColumnLayout(Collections.singletonList(new Column(Collections.emptyList())));
        }

        int totalHeight = getElementsHeight(elements);
        int columnCount = 1;
        if (autoWrap) {
            double targetHeight = Math.max(1.0, screenHeight * (double) maxColumnHeightFraction);
            columnCount = Math.max(1, (int) Math.ceil(totalHeight / targetHeight));
            // Elements are the smallest units that can be moved without changing an add-on's layout.
            columnCount = Math.min(columnCount, elements.size());
        }

        return new ProbeInfoColumnLayout(partition(elements, columnCount));
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
        if (elements.isEmpty()) {
            return 0;
        }

        int result = 0;
        for (IElement element : elements) {
            result += element.getHeight();
        }
        return result + ElementVertical.SPACING * (elements.size() - 1);
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
        for (Column column : columns) {
            int elementY = y;
            for (IElement element : column.elements) {
                element.render(x, elementY);
                elementY += element.getHeight() + ElementVertical.SPACING;
            }
            x += column.width + COLUMN_SPACING;
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
