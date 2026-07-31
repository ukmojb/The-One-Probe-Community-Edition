package mcjty.theoneprobe.apiimpl.client;

import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.rendering.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class ElementTextRender {

    public static void render(String text, int x, int y) {
        render(new TextComponentString(text), x, y);
    }

    public static void render(ITextComponent text, int x, int y) {
        RenderHelper.renderText(Minecraft.getMinecraft(), x, y, Tools.applyTextStyles(text.getFormattedText()));
    }

    public static int getWidth(ITextComponent text) {
        return Minecraft.getMinecraft().fontRenderer.getStringWidth(Tools.applyTextStyles(text.getFormattedText()));
    }

    public static int getWidth(String text) {
        return getWidth(new TextComponentString(text));
    }
}
