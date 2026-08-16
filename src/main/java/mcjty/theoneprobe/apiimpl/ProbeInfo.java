package mcjty.theoneprobe.apiimpl;

import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.api.IElementFactory;
import mcjty.theoneprobe.apiimpl.elements.ElementVertical;

import java.util.ArrayList;
import java.util.List;

public class ProbeInfo extends ElementVertical {

    private int elementChangeHeaderCount = 1;
    private long scrollStartTime = -1L;

    public ProbeInfo() {
        super((Integer) null, 2, ElementAlignment.ALIGN_TOPLEFT);
    }

    public static List<IElement> createElements(ByteBuf buf) {
        int size = buf.readShort();
        List<IElement> elements = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int id = buf.readInt();
            IElementFactory factory = TheOneProbe.theOneProbeImp.getElementFactory(id);
            IElement element = factory.createElement(buf);
            elements.add(element);
        }
        return elements;
    }

    public static void writeElements(List<IElement> elements, ByteBuf buf) {
        buf.writeShort(elements.size());
        for (IElement element : elements) {
            buf.writeInt(element.getID());
            element.toBytes(buf);
        }
    }

    public List<IElement> getElements() {
        return children;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeShort(elementChangeHeaderCount);
    }

    public void fromBytes(ByteBuf buf) {
        children = createElements(buf);
        if (buf.readBoolean()) {
            borderColor = buf.readInt();
        } else {
            borderColor = null;
        }
        spacing = buf.readShort();
        alignment = ElementAlignment.values()[buf.readShort()];
        // Older packets end after the root panel layout fields.
        elementChangeHeaderCount = buf.readableBytes() >= 2 ? buf.readUnsignedShort() : 1;
        scrollStartTime = -1L;
    }

    public int getElementChangeHeaderCount() {
        return Math.min(elementChangeHeaderCount, children.size());
    }

    public long getScrollElapsedMillis() {
        long now = System.currentTimeMillis();
        if (scrollStartTime < 0L) {
            scrollStartTime = now;
        }
        return Math.max(0L, now - scrollStartTime);
    }

    public void markElementChangeHeader() {
        elementChangeHeaderCount = children.size();
    }

    public void removeElement(IElement element) {
        this.getElements().remove(element);
    }
}
