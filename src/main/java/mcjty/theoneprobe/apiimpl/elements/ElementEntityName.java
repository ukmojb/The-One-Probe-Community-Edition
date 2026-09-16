package mcjty.theoneprobe.apiimpl.elements;

import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.apiimpl.TheOneProbeImp;
import mcjty.theoneprobe.apiimpl.client.ElementEntityNameRender;
import mcjty.theoneprobe.network.NetworkTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.text.ITextComponent;

public class ElementEntityName implements IElement {

    private final int entityID;
    private final String entityName;
    private final String fallbackName;
    private final ITextComponent fixedName;

    public ElementEntityName(Entity entity, ITextComponent fixedName) {
        entityID = entity.getEntityId();
        entityName = EntityList.getEntityString(entity);
        fallbackName = fixedName == null ? entity.getName() : "";
        this.fixedName = fixedName;
    }

    public ElementEntityName(ByteBuf buf) {
        entityID = buf.readInt();
        entityName = buf.readBoolean() ? NetworkTools.readString(buf) : null;
        fallbackName = NetworkTools.readStringUTF8(buf);
        fixedName = buf.readBoolean()
                ? ITextComponent.Serializer.jsonToComponent(NetworkTools.readStringUTF8(buf))
                : null;
    }

    @Override
    public void render(int x, int y) {
        ElementEntityNameRender.render(entityID, entityName, fallbackName, fixedName, x, y);
    }

    @Override
    public int getWidth() {
        return ElementEntityNameRender.getWidth(entityID, entityName, fallbackName, fixedName);
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityID);
        if (entityName != null) {
            buf.writeBoolean(true);
            NetworkTools.writeString(buf, entityName);
        } else {
            buf.writeBoolean(false);
        }
        NetworkTools.writeStringUTF8(buf, fallbackName);
        if (fixedName != null) {
            buf.writeBoolean(true);
            NetworkTools.writeStringUTF8(buf, ITextComponent.Serializer.componentToJson(fixedName));
        } else {
            buf.writeBoolean(false);
        }
    }

    @Override
    public int getID() {
        return TheOneProbeImp.ELEMENT_ENTITY_NAME;
    }
}
