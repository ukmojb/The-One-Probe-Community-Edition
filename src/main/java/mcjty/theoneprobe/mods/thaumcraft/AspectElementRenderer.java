package mcjty.theoneprobe.mods.thaumcraft;

import mcjty.theoneprobe.rendering.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import thaumcraft.api.aspects.Aspect;

public class AspectElementRenderer {

    public static void render(int x, int y, Aspect aspect, int amount) {
        Minecraft mc = Minecraft.getMinecraft();
        int colour = aspect.getColor();
        GlStateManager.color(
                ((colour >> 16) & 0xFF) / 255F,
                ((colour >> 8) & 0xFF) / 255F,
                (colour & 0xFF) / 255F,
                1F);
        mc.getTextureManager().bindTexture(aspect.getImage());
        RenderHelper.drawTexturedModalRect(
                x, y, 0, 0, AspectElement.WIDTH, AspectElement.HEIGHT, AspectElement.WIDTH, AspectElement.HEIGHT);
        if (amount >= 0) {
            String amountStr = Integer.toString(amount);
            GlStateManager.pushMatrix();
            GlStateManager.translate(
                    x + AspectElement.WIDTH + 1 - mc.fontRenderer.getStringWidth(amountStr) * 0.75F,
                    y + AspectElement.HEIGHT + 1 - mc.fontRenderer.FONT_HEIGHT * 0.75F,
                    0F);
            GlStateManager.scale(0.75F, 0.75F, 1F);
            RenderHelper.renderText(mc, 0, 0, amountStr);
            GlStateManager.popMatrix();
        }
    }

}
