package mcjty.theoneprobe.mods;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.apiimpl.TheOneProbeImp;
import mcjty.theoneprobe.mods.botania.*;
import mcjty.theoneprobe.mods.ember_top.*;
import net.minecraftforge.fml.common.Loader;

public class TOPHandler {
    public static void registerTips(){
        if (Loader.isModLoaded("botania")) {
            TheOneProbeImp theOneProbeImp = TheOneProbe.theOneProbeImp;
            theOneProbeImp.registerProvider(new ManaPool());
            theOneProbeImp.registerProvider(new Spreader());
        }
        if (Loader.isModLoaded("embers")) {
            TheOneProbeImp theOneProbeImp = TheOneProbe.theOneProbeImp;
            theOneProbeImp.registerProvider(new ember_coppercell());
            theOneProbeImp.registerProvider(new ember_auto_hummer());
            theOneProbeImp.registerProvider(new ember_beam_cannon());
            theOneProbeImp.registerProvider(new ember_cinder_plinth());
            theOneProbeImp.registerProvider(new ember_furnace());
            theOneProbeImp.registerProvider(new ember_mixer());
            theOneProbeImp.registerProvider(new ember_stamper());
            theOneProbeImp.registerProvider(new ember_crystal_cell());
            theOneProbeImp.registerProvider(new ember_emitter());
            theOneProbeImp.registerProvider(new ember_receiver());
            theOneProbeImp.registerProvider(new ember_activator());
            theOneProbeImp.registerProvider(new ember_BeamSplitter());
            theOneProbeImp.registerProvider(new ember_alchemy_pedestal());
            theOneProbeImp.registerProvider(new ember_emberinjector());
            theOneProbeImp.registerProvider(new ember_reactor());
            theOneProbeImp.registerProvider(new ember_pulser());
            theOneProbeImp.registerProvider(new ember_funnel());
            theOneProbeImp.registerProvider(new ember_boiler());
        }
    }
}

