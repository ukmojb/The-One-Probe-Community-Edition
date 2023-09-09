package mcjty.theoneprobe.mods.crt.api;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : wdcftgg
 * @create 2023/9/9 11:48
 */
@ZenRegister
@ZenClass("mods.topce.ProbeMode")
public class ProbeMode {

    @ZenMethod
    @ZenGetter("NORMAL")
    public static mcjty.theoneprobe.api.ProbeMode getNORMAL(){
        return mcjty.theoneprobe.api.ProbeMode.NORMAL;
    }

    @ZenMethod
    @ZenGetter("DEBUG")
    public static mcjty.theoneprobe.api.ProbeMode getDEBUG(){
        return mcjty.theoneprobe.api.ProbeMode.DEBUG;
    }

    @ZenMethod
    @ZenGetter("EXTENDED")
    public static mcjty.theoneprobe.api.ProbeMode getEXTENDED(){
        return mcjty.theoneprobe.api.ProbeMode.EXTENDED;
    }
}
