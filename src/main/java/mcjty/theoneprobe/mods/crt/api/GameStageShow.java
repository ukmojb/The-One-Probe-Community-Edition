package mcjty.theoneprobe.mods.crt.api;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import mcjty.theoneprobe.api.ProbeMode;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : wdcftgg
 * @create 2023/9/9 18:29
 */
@ZenRegister
@ZenClass("mods.topce.GameStageShow")
public class GameStageShow {
    public static HashMap<String, String> topstage = new HashMap<>();

    @ZenMethod
    public static void showAll(String string){
        topstage.put("all", string);
    }

    @ZenMethod
    public static void showExtended(String string){
        topstage.put("extended", string);
    }

    @ZenMethod
    public static void showBreakProgress(String string){
        topstage.put("breakProgress", string);
    }

    @ZenMethod
    public static void showLiquids(String string){
        topstage.put("liquids", string);
    }
}
