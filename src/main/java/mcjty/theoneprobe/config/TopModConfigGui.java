package mcjty.theoneprobe.config;

import com.google.common.collect.Lists;
import mcjty.theoneprobe.TheOneProbe;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.List;

public class TopModConfigGui extends GuiConfig {

    public TopModConfigGui(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(), TheOneProbe.MODID, false, false, "The One Probe Config");
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = Lists.newArrayList();

        list.add(new ConfigElement(Config.mainConfig.getCategory(Config.CATEGORY_CLIENT)));
        list.add(new ConfigElement(Config.mainConfig.getCategory(Config.CATEGORY_PROVIDERS)));
        list.add(new ConfigElement(Config.mainConfig.getCategory(Config.CATEGORY_THEONEPROBE)));

        return list;
    }
}
