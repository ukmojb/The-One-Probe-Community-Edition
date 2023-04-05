package mcjty.theoneprobe.mods.botania;


import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import mcjty.theoneprobe.config.ConfigSetup;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import vazkii.botania.common.block.tile.mana.*;

import java.awt.Color;

public class ManaPool implements IProbeInfoProvider{

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data){
        if(world.getTileEntity(data.getPos()) instanceof TilePool){
            TilePool tile = (TilePool) world.getTileEntity(data.getPos());

            final int mana = (int) tile.getCurrentMana();
            final int manamax = (int) tile.manaCap;
            int light_bule = new Color(39,255,247).getRGB();
            int gray = Color.gray.getRGB();
            int white = Color.white.getRGB();
            if (ConfigSetup.showBotaniaprogress) {
                if (ConfigSetup.testinprogress) {
                    probeInfo.progress(mana, manamax, new ProgressStyle()
                            .prefix("Mana" + ":" + mana).suffix("/" + manamax)
                            .width(110)
                            .numberFormat(NumberFormat.NONE)
                            .borderColor(white)
                            .backgroundColor(gray)
                            .filledColor(light_bule)
                            .alternateFilledColor(light_bule));
                }else {
                    probeInfo.progress(mana, manamax, new ProgressStyle()
                            .width(110)
                            .numberFormat(NumberFormat.NONE)
                            .borderColor(white)
                            .backgroundColor(gray)
                            .filledColor(light_bule)
                            .alternateFilledColor(light_bule));
                    probeInfo.text("Mana:" + mana + "/" + manamax);
                }

            } else {
                probeInfo.text("Mana:" + mana + "/" + manamax);
            }
        }
    }

    @Override
    public String getID() {
        return "random.botania.pool";
    }
}