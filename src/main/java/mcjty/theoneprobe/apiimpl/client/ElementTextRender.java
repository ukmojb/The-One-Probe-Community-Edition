package mcjty.theoneprobe.apiimpl.client;

import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.TextStyleClass;
import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.rendering.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.translation.I18n;
import org.apache.commons.lang3.StringUtils;

import java.util.EnumSet;
import java.util.Set;

import static mcjty.theoneprobe.api.IProbeInfo.ENDLOC;
import static mcjty.theoneprobe.api.IProbeInfo.STARTLOC;

public class ElementTextRender {

    public static void render(String text, int x, int y) {
        RenderHelper.renderText(Minecraft.getMinecraft(), x, y, Tools.stylifyString(text));
    }

    public static int getWidth(String text) {
        return Minecraft.getMinecraft().fontRenderer.getStringWidth(Tools.stylifyString(text));
    }
}
