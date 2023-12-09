package mcjty.theoneprobe.mods.thaumcraft;

import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.api.IElementFactory;
import thaumcraft.api.aspects.Aspect;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;

public class AspectElement implements IElement {

    public static final int WIDTH = 16, HEIGHT = 16;

    @Nullable
    private final Aspect aspect; // null represents error state
    private final int amount;

    public AspectElement(@Nullable Aspect aspect, int amount) {
        this.aspect = aspect;
        this.amount = amount;
    }

    @Override
    public void render(int x, int y) {
        if (aspect != null) {
            AspectElementRenderer.render(x, y, aspect, amount);
        }
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (aspect != null) {
            String id = aspect.getTag();
            buf.writeByte(id.length());
            buf.writeBytes(id.getBytes(StandardCharsets.US_ASCII));
        } else {
            buf.writeByte(0);
        }
        buf.writeInt(amount);
    }

    @Override
    public int getID() {
        return TheOneProbe.getAspectElementId();
    }

    public static class Factory implements IElementFactory {

        @Override
        public IElement createElement(ByteBuf buf) {
            byte[] idBuf = new byte[buf.readByte()];
            buf.readBytes(idBuf);
            return new AspectElement(Aspect.getAspect(new String(idBuf, StandardCharsets.US_ASCII)), buf.readInt());
        }

    }

}
