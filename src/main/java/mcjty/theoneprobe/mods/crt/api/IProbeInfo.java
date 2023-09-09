package mcjty.theoneprobe.mods.crt.api;

import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.oredict.IOreDictEntry;
import mcjty.theoneprobe.api.IProgressStyle;
import mcjty.theoneprobe.api.NumberFormat;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Objects;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : wdcftgg
 * @create 2023/9/8 21:53
 */
@ZenRegister
@ZenClass("mods.topce.IProbeInfo")
public interface IProbeInfo {
    @ZenMethod
    mcjty.theoneprobe.api.IProbeInfo progress(mcjty.theoneprobe.api.IProbeInfo probeInfo, int current, int max, IProgressStyle style);

    @ZenMethod
    mcjty.theoneprobe.api.IProbeInfo progress(mcjty.theoneprobe.api.IProbeInfo probeInfo, int current, int max);
}
