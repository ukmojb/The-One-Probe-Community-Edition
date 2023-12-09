package mcjty.theoneprobe.mods.botania;


import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import mcjty.theoneprobe.config.Config;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import vazkii.botania.common.block.tile.TileRuneAltar;

import java.awt.*;

public class RuneAltar implements IProbeInfoProvider {

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {

        if (world.getTileEntity(data.getPos()) instanceof TileRuneAltar) {
            TileRuneAltar tile = (TileRuneAltar) world.getTileEntity(data.getPos());


            final int mana = tile.getCurrentMana();
            final int manamax = tile.getTargetMana();
            int light_bule = new Color(39, 255, 247).getRGB();
            int gray = Color.gray.getRGB();
            int white = Color.white.getRGB();
            if (Config.showBotaniaprogress) {
                if (Config.textinprogress) {
                    probeInfo.progress(mana, manamax, new ProgressStyle()
                            .prefix("Needed Mana" + ":" + mana).suffix("/" + manamax)
                            .width(110)
                            .numberFormat(NumberFormat.NONE)
                            .borderColor(white)
                            .backgroundColor(gray)
                            .filledColor(light_bule)
                            .alternateFilledColor(light_bule));
                } else {
                    probeInfo.progress(mana, manamax, new ProgressStyle()
                            .width(110)
                            .numberFormat(NumberFormat.NONE)
                            .borderColor(white)
                            .backgroundColor(gray)
                            .filledColor(light_bule)
                            .alternateFilledColor(light_bule));
                    probeInfo.text("Needed Mana:" + mana + "/" + manamax);
                }

            } else {
                probeInfo.text("Needed Mana:" + mana + "/" + manamax);
            }
        }
    }

    @Override
    public String getID() {
        return "random.botania.RuneAltar";
    }
}