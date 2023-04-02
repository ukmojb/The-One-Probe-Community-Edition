package mcjty.theoneprobe.mods.ember_top;

import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import teamroots.embers.tileentity.TileEntityReactor;

import java.awt.*;

public class ember_reactor implements IProbeInfoProvider {
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        if (world.getTileEntity(data.getPos()) instanceof TileEntityReactor) {
            TileEntityReactor tileEntityReactor = (TileEntityReactor) world.getTileEntity(data.getPos());
            assert tileEntityReactor != null;
            final int max = (int) tileEntityReactor.capability.getEmberCapacity();
            final int ember = (int) tileEntityReactor.capability.getEmber();
            int orange = Color.ORANGE.getRGB();
            int yellow = Color.yellow.getRGB();
            int white = Color.white.getRGB();
            if(tileEntityReactor.capability.getEmber()>0){
                probeInfo.progress(ember, max + 5, new ProgressStyle().prefix("Ember" + ":" + ember).suffix("/" + max)
                        .width(110)
                        .numberFormat(NumberFormat.NONE)
                        .borderColor(yellow)
                        .backgroundColor(white)
                        .filledColor(orange));
            }
        }
    }

    public String getID() {
        return "random.ember.reactor";
    }
}
