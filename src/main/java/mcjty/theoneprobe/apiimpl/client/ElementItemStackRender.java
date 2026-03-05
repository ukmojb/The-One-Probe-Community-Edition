package mcjty.theoneprobe.apiimpl.client;

import mcjty.theoneprobe.api.IItemStyle;
import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.rendering.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.text.DecimalFormat;

public class ElementItemStackRender {

    private static final String[] JADE_NUM_SUFFIXES = new String[]{"", "k", "m", "b", "t"};
    private static final int JADE_MAX_LENGTH = 4;
    private static final DecimalFormat JADE_SHORT_FORMAT = new DecimalFormat("##0E0");

    public static void render(ItemStack itemStack, IItemStyle style, int x, int y) {
        RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
        if (!itemStack.isEmpty()) {
            String amount = formatAmount(itemStack.getCount());

            if (!RenderHelper.renderItemStack(Minecraft.getMinecraft(), itemRender, itemStack, x + (style.getWidth() - 18) / 2, y + (style.getHeight() - 18) / 2, amount)) {
                // There was a crash rendering this item
                RenderHelper.renderText(Minecraft.getMinecraft(), x, y, TextFormatting.RED + "ERROR: " + itemStack.getDisplayName());
            }
        }
    }

    private static String formatAmount(int size) {
        if (size <= 1) {
            return "";
        }

        if (Config.isJadeTheme()) {
            return toJadeShortNumber(size);
        }

        if (size < 100000) {
            return String.valueOf(size);
        } else if (size < 1000000) {
            return String.valueOf(size / 1000) + "k";
        } else if (size < 1000000000) {
            return String.valueOf(size / 1000000) + "m";
        }
        return String.valueOf(size / 1000000000) + "g";
    }

    private static String toJadeShortNumber(int number) {
        String shorthand = JADE_SHORT_FORMAT.format(number);
        int suffixIndex = Character.getNumericValue(shorthand.charAt(shorthand.length() - 1)) / 3;
        if (suffixIndex >= JADE_NUM_SUFFIXES.length) {
            suffixIndex = JADE_NUM_SUFFIXES.length - 1;
        }
        shorthand = shorthand.replaceAll("E[0-9]", JADE_NUM_SUFFIXES[suffixIndex]);

        while (shorthand.length() > JADE_MAX_LENGTH || shorthand.matches("[0-9]+\\.[a-z]")) {
            shorthand = shorthand.substring(0, shorthand.length() - 2) + shorthand.substring(shorthand.length() - 1);
        }
        return shorthand;
    }
}
