package mcjty.theoneprobe.mods.crt.api;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : wdcftgg
 * @create 2023/9/8 22:06
 */

import crafttweaker.annotations.ZenRegister;
import mcjty.theoneprobe.api.NumberFormat;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.topce.IProgressStyle")
public interface IProgressStyle {

    @ZenMethod
    mcjty.theoneprobe.api.IProgressStyle borderColor(int c);

    @ZenMethod
    mcjty.theoneprobe.api.IProgressStyle backgroundColor(int c);

    @ZenMethod
    mcjty.theoneprobe.api.IProgressStyle filledColor(int c);

    @ZenMethod
    mcjty.theoneprobe.api.IProgressStyle alternateFilledColor(int c);

    @ZenMethod
    mcjty.theoneprobe.api.IProgressStyle showText(boolean b);

    @ZenMethod
    mcjty.theoneprobe.api.IProgressStyle numberFormat(NumberFormat f);

    @ZenMethod
    mcjty.theoneprobe.api.IProgressStyle prefix(String prefix);

    @ZenMethod
    mcjty.theoneprobe.api.IProgressStyle suffix(String suffix);

    @ZenMethod
    mcjty.theoneprobe.api.IProgressStyle width(int w);

    @ZenMethod
    mcjty.theoneprobe.api.IProgressStyle height(int h);

    @ZenMethod
    mcjty.theoneprobe.api.IProgressStyle lifeBar(boolean b);

    @ZenMethod
    mcjty.theoneprobe.api.IProgressStyle armorBar(boolean b);
}
