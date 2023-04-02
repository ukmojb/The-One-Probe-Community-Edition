package mcjty.theoneprobe.mods.ember_top;

import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import teamroots.embers.tileentity.TileEntityMixerBottom;
import teamroots.embers.tileentity.TileEntityMixerTop;

import java.awt.*;
import java.util.Objects;

public class ember_mixer implements IProbeInfoProvider {
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        if (world.getTileEntity(data.getPos()) instanceof TileEntityMixerTop) {
            TileEntityMixerTop tileEntityMixerTop = (TileEntityMixerTop) world.getTileEntity(data.getPos());
            assert tileEntityMixerTop != null;
            final int max = (int) tileEntityMixerTop.capability.getEmberCapacity();
            final int ember = (int) tileEntityMixerTop.capability.getEmber();
            int orange = Color.ORANGE.getRGB();
            int yellow = Color.yellow.getRGB();
            int white = Color.white.getRGB();
            if(tileEntityMixerTop.capability.getEmber()>0){
                probeInfo.progress(ember, max + 5, new ProgressStyle().prefix(I18n.format("random.power") + ":" + ember).suffix("/" + max)
                        .width(110)
                        .numberFormat(NumberFormat.NONE)
                        .borderColor(yellow)
                        .backgroundColor(white)
                        .filledColor(orange));
            }
        }
        if (world.getTileEntity(data.getPos()) instanceof TileEntityMixerBottom) {
            TileEntityMixerBottom tileEntityMixerBottom = (TileEntityMixerBottom) world.getTileEntity(data.getPos());
            assert tileEntityMixerBottom != null;
            int cyan = Color.cyan.getRGB();
            int white = Color.white.getRGB();
            if (tileEntityMixerBottom.east.getFluidAmount() > 0) {
                probeInfo.progress(tileEntityMixerBottom.east.getFluidAmount(), 8000, new ProgressStyle().prefix("East" + ":" + Objects.requireNonNull(tileEntityMixerBottom.east.getFluid()).getLocalizedName()).suffix("mB")
                .width(130)
                .backgroundColor(white)
                .filledColor(cyan)
                .alternateFilledColor(cyan));
            }
            if (tileEntityMixerBottom.south.getFluidAmount()>0){
                probeInfo.progress(tileEntityMixerBottom.south.getFluidAmount(), 8000, new ProgressStyle().prefix("South" + ":" + Objects.requireNonNull(tileEntityMixerBottom.south.getFluid()).getLocalizedName()).suffix("mB")
                .width(130)
                .backgroundColor(white)
                .filledColor(cyan)
                .alternateFilledColor(cyan));
            }
            if (tileEntityMixerBottom.west.getFluidAmount()>0){
                probeInfo.progress(tileEntityMixerBottom.west.getFluidAmount(), 8000, new ProgressStyle().prefix("West" + ":" + Objects.requireNonNull(tileEntityMixerBottom.west.getFluid()).getLocalizedName()).suffix("mB")
                .width(130)
                .backgroundColor(white)
                .filledColor(cyan)
                .alternateFilledColor(cyan));
            }
            if (tileEntityMixerBottom.north.getFluidAmount()>0){
                probeInfo.progress(tileEntityMixerBottom.north.getFluidAmount(), 8000, new ProgressStyle().prefix("North" + ":" + Objects.requireNonNull(tileEntityMixerBottom.north.getFluid()).getLocalizedName()).suffix("mB")
                .width(130)
                .backgroundColor(white)
                .filledColor(cyan)
                .alternateFilledColor(cyan));
            }
        }
    }

    public String getID() {
        return "random.ember.mixer";
    }
}
