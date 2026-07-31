package mcjty.theoneprobe.apiimpl.elements;

import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.apiimpl.TheOneProbeImp;
import mcjty.theoneprobe.apiimpl.client.ElementTextRender;
import mcjty.theoneprobe.network.NetworkTools;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class ElementText implements IElement {

    private final ITextComponent text;

    public ElementText(String text) {
        this(new TextComponentString(text));
    }

    public ElementText(ITextComponent text) {
        this.text = text;
    }

    public ElementText(ByteBuf buf) {
        text = ITextComponent.Serializer.jsonToComponent(NetworkTools.readStringUTF8(buf));
    }

    @Override
    public void render(int x, int y) {
        ElementTextRender.render(text, x, y);
    }

    @Override
    public int getWidth() {
        return ElementTextRender.getWidth(text);
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writeStringUTF8(buf, ITextComponent.Serializer.componentToJson(text));
    }

    @Override
    public int getID() {
        return TheOneProbeImp.ELEMENT_TEXT;
    }
}
