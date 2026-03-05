package mcjty.theoneprobe.apiimpl.client;

import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.rendering.RenderHelper;
import net.minecraft.client.Minecraft;

public class ElementTextRender {

    public static void render(String text, int x, int y) {
        RenderHelper.renderText(Minecraft.getMinecraft(), x, y, Tools.stylifyString(text));
    }

    public static int getWidth(String text) {
        return Minecraft.getMinecraft().fontRenderer.getStringWidth(Tools.stylifyString(text));
    }
}
