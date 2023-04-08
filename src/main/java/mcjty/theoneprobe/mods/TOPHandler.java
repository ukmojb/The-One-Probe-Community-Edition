package mcjty.theoneprobe.mods;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.apiimpl.TheOneProbeImp;
import mcjty.theoneprobe.mods.BotanicAdditions.*;
import mcjty.theoneprobe.mods.botania.*;
import mcjty.theoneprobe.mods.ember_top.*;
import mcjty.theoneprobe.mods.ExtraBotany.*;
import net.minecraftforge.fml.common.Loader;

public class TOPHandler {
    public static void registerTips(){
        TheOneProbeImp theOneProbeImp = TheOneProbe.theOneProbeImp;
        if (Loader.isModLoaded("botania")) {
            theOneProbeImp.registerProvider(new ManaPool());
            theOneProbeImp.registerProvider(new Spreader());
            theOneProbeImp.registerProvider(new TerraPlate());
            theOneProbeImp.registerProvider(new RuneAltar());
        }
        if (Loader.isModLoaded("extrabotany")) {
            theOneProbeImp.registerProvider(new ManaBuffer());
            theOneProbeImp.registerProvider(new QuantumManaBuffer());
        }
        if (Loader.isModLoaded("botanicadds")) {
            theOneProbeImp.registerProvider(new DreamingManaPool());
            theOneProbeImp.registerProvider(new ElvenAltar());
        }
        if (Loader.isModLoaded("embers")) {
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

