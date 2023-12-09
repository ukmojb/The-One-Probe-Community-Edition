package mcjty.theoneprobe.mods.thaumcraft;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.items.IGogglesDisplayExtended;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.devices.TileArcaneEar;

public class ThaumHighlightInfoProvider implements IProbeInfoProvider {

    @Override
    public String getID() {
        return TheOneProbe.MODID + ":thaum_highlight";
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo,
                             EntityPlayer player, World world, IBlockState state, IProbeHitData data) {
        if (Config.requireGoggles && !EntityUtils.hasGoggles(player)) {
            return;
        }
        TileEntity tile = world.getTileEntity(data.getPos());
        boolean genericInfoAdded = false;

        if (tile instanceof IGogglesDisplayExtended) {
            for (String line : ((IGogglesDisplayExtended)tile).getIGogglesText()) {
                probeInfo.text(line);
            }
            genericInfoAdded = true;
        } else {
            Block block = state.getBlock();
            if (block instanceof IGogglesDisplayExtended) {
                for (String line : ((IGogglesDisplayExtended)block).getIGogglesText()) {
                    probeInfo.text(line);
                }
                genericInfoAdded = true;
            }
        }

        if (tile instanceof IAspectContainer) {
            AspectList aspects = ((IAspectContainer)tile).getAspects();
            if (aspects != null && aspects.size() > 0) {
                IProbeInfo aspectInfo = probeInfo.horizontal(probeInfo.defaultLayoutStyle()
                        .alignment(ElementAlignment.ALIGN_TOPLEFT)
                        .spacing(3));
                for (Aspect aspect : aspects.getAspectsSortedByName()) {
                    aspectInfo.element(new AspectElement(aspect, aspects.getAmount(aspect)));
                }
            }
            genericInfoAdded = true;
        }

        if (genericInfoAdded) { // tile-specific info below here
            return;
        }

        if (tile instanceof TileEntityNote) {
            probeInfo.text("Note: " + ((TileEntityNote)tile).note);
            return;
        }

        if (tile instanceof TileArcaneEar) {
            probeInfo.text("Note: " + ((TileArcaneEar)tile).note);
        }
    }

}
