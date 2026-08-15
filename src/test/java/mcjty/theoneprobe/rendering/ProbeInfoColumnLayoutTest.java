package mcjty.theoneprobe.rendering;

import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.IElement;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProbeInfoColumnLayoutTest {

    @Test
    void keepsTheOriginalSingleColumnLayoutWhenDisabled() {
        List<TestElement> elements = elements(3, 10, 8);

        ProbeInfoColumnLayout layout = ProbeInfoColumnLayout.create(asElements(elements), 20, false, 0.6f);

        assertEquals(1, layout.getColumnCount());
        assertEquals(10, layout.getWidth());
        assertEquals(28, layout.getHeight());
    }

    @Test
    void derivesAndBalancesColumnsFromTheConfiguredScreenFraction() {
        List<TestElement> elements = elements(10, 10, 10);

        // Original height: 10 * 10 + 9 * 2 = 118. ceil(118 / (100 * .6)) = 2.
        ProbeInfoColumnLayout layout = ProbeInfoColumnLayout.create(asElements(elements), 100, true, 0.6f);

        assertEquals(2, layout.getColumnCount());
        assertEquals(26, layout.getWidth());
        assertEquals(58, layout.getHeight());
    }

    @Test
    void rendersColumnsWithSixPixelsOfHorizontalSpacingAndPreservesElementOrder() {
        TestElement first = new TestElement(8, 10);
        TestElement second = new TestElement(12, 10);
        TestElement third = new TestElement(7, 10);
        TestElement fourth = new TestElement(9, 10);
        List<TestElement> elements = Arrays.asList(first, second, third, fourth);
        ProbeInfoColumnLayout layout = ProbeInfoColumnLayout.create(asElements(elements), 40, true, 0.6f);

        layout.render(100, 5);

        assertEquals(2, layout.getColumnCount());
        assertEquals(27, layout.getWidth());
        assertEquals(Arrays.asList(100, 5), first.renderPosition);
        assertEquals(Arrays.asList(100, 17), second.renderPosition);
        // The second column starts after the first column's maximum width plus a 6px gap.
        assertEquals(Arrays.asList(118, 5), third.renderPosition);
        assertEquals(Arrays.asList(118, 17), fourth.renderPosition);
    }

    @Test
    void doesNotCreateEmptyColumnsForAnIndivisibleElement() {
        List<TestElement> elements = elements(1, 10, 200);

        ProbeInfoColumnLayout layout = ProbeInfoColumnLayout.create(asElements(elements), 100, true, 0.1f);

        assertEquals(1, layout.getColumnCount());
        assertEquals(10, layout.getWidth());
        assertEquals(200, layout.getHeight());
    }

    private static List<TestElement> elements(int count, int width, int height) {
        List<TestElement> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(new TestElement(width, height));
        }
        return result;
    }

    private static List<IElement> asElements(List<TestElement> elements) {
        return new ArrayList<IElement>(elements);
    }

    private static final class TestElement implements IElement {
        private final int width;
        private final int height;
        private List<Integer> renderPosition;

        private TestElement(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public void render(int x, int y) {
            renderPosition = Arrays.asList(x, y);
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public void toBytes(ByteBuf buf) {
        }

        @Override
        public int getID() {
            return 0;
        }
    }
}
