package mcjty.theoneprobe.apiimpl;

import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.elements.ElementVertical;
import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.items.ModItems;
import mcjty.theoneprobe.network.ThrowableIdentity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static mcjty.theoneprobe.api.TextStyleClass.*;
import static mcjty.theoneprobe.config.Config.PROBE_NEEDEDFOREXTENDED;
import static mcjty.theoneprobe.config.Config.PROBE_NEEDEDHARD;

public class ProbeInfo extends ElementVertical {

    public ProbeInfo() {
        super((Integer) null, 2, ElementAlignment.ALIGN_TOPLEFT);
    }

    public static List<IElement> createElements(ByteBuf buf) {
        int size = buf.readShort();
        List<IElement> elements = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int id = buf.readInt();
            IElementFactory factory = TheOneProbe.theOneProbeImp.getElementFactory(id);
            IElement element = factory.createElement(buf);
            elements.add(element);
        }
        return elements;
    }

    public static void writeElements(List<IElement> elements, ByteBuf buf) {
        buf.writeShort(elements.size());
        for (IElement element : elements) {
            buf.writeInt(element.getID());
            element.toBytes(buf);
        }
    }

    public List<IElement> getElements() {
        return children;
    }

    public void fromBytes(ByteBuf buf) {
        children = createElements(buf);
    }

    public void removeElement(IElement element) {
        this.getElements().remove(element);
    }
}
