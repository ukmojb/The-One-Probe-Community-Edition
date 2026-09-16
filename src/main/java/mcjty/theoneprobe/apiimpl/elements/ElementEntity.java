package mcjty.theoneprobe.apiimpl.elements;

import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.api.IEntityStyle;
import mcjty.theoneprobe.apiimpl.TheOneProbeImp;
import mcjty.theoneprobe.apiimpl.client.ElementEntityRender;
import mcjty.theoneprobe.apiimpl.styles.EntityStyle;
import mcjty.theoneprobe.network.NetworkTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class ElementEntity implements IElement {

    private final String entityName;
    private final Integer entityID;
    private final NBTTagCompound entityNBT;
    private final IEntityStyle style;
    private Entity previewEntity;

    public ElementEntity(String entityName, IEntityStyle style) {
        this.entityName = entityName;
        this.entityNBT = null;
        this.style = style;
        this.entityID = null;
    }

    public ElementEntity(Entity entity, IEntityStyle style) {
        if (entity instanceof EntityPlayer) {
            entityNBT = null;
        } else {
            entityNBT = entity.serializeNBT();
        }
        entityID = entity.getEntityId();
        this.entityName = EntityList.getEntityString(entity);
        this.style = style;
    }

    public ElementEntity(ByteBuf buf) {
        entityName = NetworkTools.readString(buf);
        style = new EntityStyle()
                .width(buf.readInt())
                .height(buf.readInt())
                .scale(buf.readFloat());
        if (buf.readBoolean()) {
            entityNBT = NetworkTools.readNBT(buf);
        } else {
            entityNBT = null;
        }
        if (buf.readBoolean()) {
            entityID = buf.readInt();
        } else {
            entityID = null;
        }
    }

    @Override
    public void render(int x, int y) {
        previewEntity = ElementEntityRender.render(entityName, entityNBT, entityID, previewEntity, style, x, y);
    }

    @Override
    public int getWidth() {
        return style.getWidth();
    }

    @Override
    public int getHeight() {
        return style.getHeight();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writeString(buf, entityName);
        buf.writeInt(style.getWidth());
        buf.writeInt(style.getHeight());
        buf.writeFloat(style.getScale());
        if (entityNBT != null) {
            buf.writeBoolean(true);
            NetworkTools.writeNBT(buf, entityNBT);
        } else {
            buf.writeBoolean(false);
        }
        if (entityID != null) {
            buf.writeBoolean(true);
            buf.writeInt(entityID);
        } else {
            buf.writeBoolean(false);
        }
    }

    @Override
    public int getID() {
        return TheOneProbeImp.ELEMENT_ENTITY;
    }
}
