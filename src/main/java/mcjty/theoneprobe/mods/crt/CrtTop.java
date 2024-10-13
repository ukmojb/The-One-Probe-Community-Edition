package mcjty.theoneprobe.mods.crt;

import crafttweaker.api.minecraft.CraftTweakerMC;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.mods.crt.api.AddProbeInfo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.Objects;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : wdcftgg
 * @create 2023/9/9 15:44
 */
public class CrtTop implements IProbeInfoProvider {
    @Override
    public String getID() {
        return "topce.crt";
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        for (int i = 0; i < AddProbeInfo.probeInfos.size(); i++) {
            if(mode.equals(AddProbeInfo.modes.get(i))){
                if(AddProbeInfo.blockStates.get(i) == null){
                    showWorldText(world.provider.getDimension() , AddProbeInfo.worlds.get(i), AddProbeInfo.texts.get(i), probeInfo);
                }
                if(blockState.equals(CraftTweakerMC.getBlockState(AddProbeInfo.blockStates.get(i)))){
                    showWorldText(world.provider.getDimension() , AddProbeInfo.worlds.get(i), AddProbeInfo.texts.get(i), probeInfo);
                }
            }
            if(AddProbeInfo.modes.get(i) == null) {
                if(AddProbeInfo.blockStates.get(i) == null){
                    showWorldText(world.provider.getDimension() , AddProbeInfo.worlds.get(i), AddProbeInfo.texts.get(i), probeInfo);
                }
                if(blockState.equals(CraftTweakerMC.getBlockState(AddProbeInfo.blockStates.get(i)))){
                    showWorldText(world.provider.getDimension() , AddProbeInfo.worlds.get(i), AddProbeInfo.texts.get(i), probeInfo);
                }
            }
        }
    }

    private void showText(String string, IProbeInfo probeInfo){
        if(string != null){
            probeInfo.text(string);
        }
    }

    private void showWorldText(Integer world , Integer iworld, String string, IProbeInfo probeInfo){
        if (iworld == null){
            showText(string, probeInfo);
        }
        if(Objects.equals(world, iworld)){
            if(string != null){
                probeInfo.text(string);
            }
        }
    }

}
