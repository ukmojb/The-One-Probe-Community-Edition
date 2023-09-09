package mcjty.theoneprobe.mods.crt.api;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nullable;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : wdcftgg
 * @create 2023/9/9 11:45
 */
@ZenRegister
@ZenClass("mods.topce.IProbeHitData")
public interface IProbeHitData {

    @ZenMethod
    BlockPos getPos();

    @ZenMethod
    Vec3d getHitVec();

    @ZenMethod
    EnumFacing getSideHit();

    /**
     * Access the client-side result of getPickBlock() for the given block. That way
     * you don't have to call this server side because that can sometimes be
     * problematic
     *
     * @return the picked block or null
     */
    @ZenMethod
    @Nullable
    ItemStack getPickBlock();
}
