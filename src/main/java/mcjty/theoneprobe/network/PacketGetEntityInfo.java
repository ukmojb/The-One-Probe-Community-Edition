package mcjty.theoneprobe.network;

import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.ProbeHitEntityData;
import mcjty.theoneprobe.apiimpl.ProbeInfo;
import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.items.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;
import java.util.UUID;

import static mcjty.theoneprobe.api.TextStyleClass.ERROR;
import static mcjty.theoneprobe.api.TextStyleClass.LABEL;
import static mcjty.theoneprobe.config.Config.PROBE_NEEDEDFOREXTENDED;
import static mcjty.theoneprobe.config.Config.PROBE_NEEDEDHARD;

public class PacketGetEntityInfo implements IMessage {

    private static int dim;
    private static UUID uuid;
    private static ProbeMode mode;
    private static Vec3d hitVec;
    private static ProbeInfo probeInfo;

    public PacketGetEntityInfo() {
    }

    public PacketGetEntityInfo(int dim, ProbeMode mode, RayTraceResult mouseOver, Entity entity, ProbeInfo probeInfo) {
        PacketGetEntityInfo.dim = dim;
        PacketGetEntityInfo.uuid = entity.getPersistentID();
        PacketGetEntityInfo.mode = mode;
        PacketGetEntityInfo.hitVec = mouseOver.hitVec;
        PacketGetEntityInfo.probeInfo = probeInfo;
    }


    @Override
    public void fromBytes(ByteBuf buf) {
        dim = buf.readInt();
        uuid = new UUID(buf.readLong(), buf.readLong());
        mode = ProbeMode.values()[buf.readByte()];
        if (buf.readBoolean()) {
            hitVec = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        }
        probeInfo = new ProbeInfo();
        probeInfo.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dim);
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
        buf.writeByte(mode.ordinal());
        if (hitVec == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeDouble(hitVec.x);
            buf.writeDouble(hitVec.y);
            buf.writeDouble(hitVec.z);
        }
        probeInfo.toBytes(buf);
    }

    public static class Handler implements IMessageHandler<PacketGetEntityInfo, IMessage> {
        @Override
        public IMessage onMessage(PacketGetEntityInfo message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketGetEntityInfo message, MessageContext ctx) {
            WorldServer world = DimensionManager.getWorld(message.dim);
            if (world != null) {
                Entity entity = world.getEntityFromUuid(message.uuid);
                if (entity != null) {
                    PacketHandler.INSTANCE.sendTo(new PacketReturnEntityInfo(message.uuid, message.probeInfo), ctx.getServerHandler().player);
                }
            }
        }
    }

}
