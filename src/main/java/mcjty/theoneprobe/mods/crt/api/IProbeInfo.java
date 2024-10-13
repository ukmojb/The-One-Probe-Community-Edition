package mcjty.theoneprobe.mods.crt.api;

import crafttweaker.annotations.ZenRegister;
import mcjty.theoneprobe.api.IProgressStyle;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

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
