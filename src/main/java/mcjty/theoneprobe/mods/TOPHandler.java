package mcjty.theoneprobe.mods;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.apiimpl.TheOneProbeImp;
import mcjty.theoneprobe.mods.crt.CrtTop;
import net.minecraftforge.fml.common.Loader;

public class TOPHandler {
    public static void registerTips() {
        TheOneProbeImp theOneProbeImp = TheOneProbe.theOneProbeImp;
        if (Loader.isModLoaded("crafttweaker")) {
            theOneProbeImp.registerProvider(new CrtTop());
        }
    }
}

