package mcjty.theoneprobe.mods.crt.api;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.world.IWorld;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.world.World;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : wdcftgg
 * @create 2023/9/9 15:46
 */
@ZenRegister
@ZenClass("mods.topce.AddProbeInfo")
public class AddProbeInfo {

    public static final List<ProbeMode> modes = new ArrayList<>();
    public static final List<IBlockState> blockStates = new ArrayList<>();
    public static final List<String> texts = new ArrayList<>();
    public static final List<Integer> worlds = new ArrayList<>();
    public static final List<IProbeInfo> probeInfos = new ArrayList<>();
    public static final List<IProbeHitData> probeHitData = new ArrayList<>();

    @ZenMethod
    public static void addProbeText(IBlockState blockState, String text){
        modes.add(null);
        blockStates.add(blockState);
        texts.add(text);
        worlds.add(null);
        probeInfos.add(null);
        probeHitData.add(null);
    }

    @ZenMethod
    public static void addProbeModeText(ProbeMode mode, IBlockState blockState, String text){
        modes.add(mode);
        blockStates.add(blockState);
        texts.add(text);
        worlds.add(null);
        probeInfos.add(null);
        probeHitData.add(null);
    }

    @ZenMethod
    public static void addProbeDimensionModeText(ProbeMode mode, IBlockState blockState, String text, int world){
        modes.add(mode);
        blockStates.add(blockState);
        texts.add(text);
        worlds.add(world);
        probeInfos.add(null);
        probeHitData.add(null);
    }

    @ZenMethod
    public static void addDimensionModeText(IBlockState blockState, String text, int world){
        modes.add(null);
        blockStates.add(blockState);
        texts.add(text);
        worlds.add(world);
        probeInfos.add(null);
        probeHitData.add(null);
    }
}
