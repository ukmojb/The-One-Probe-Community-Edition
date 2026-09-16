package mcjty.theoneprobe.apiimpl.client;

import mcjty.theoneprobe.api.IEntityStyle;
import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.rendering.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.fixes.EntityId;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.lang.reflect.Method;

public class ElementEntityRender {

    private static final EntityId FIXER = new EntityId();

    public static Entity render(String entityName, NBTTagCompound entityNBT, Integer entityID, Entity previewEntity,
                                IEntityStyle style, int x, int y) {
        World world = Minecraft.getMinecraft().world;
        if (world == null) {
            return null;
        }

        Entity entity = getWorldEntity(world, entityName, entityID);
        if (entity == null) {
            if (entityName == null || entityName.isEmpty()) {
                return previewEntity;
            }
            if (!isValidPreview(previewEntity, world, entityName)) {
                previewEntity = createPreviewEntity(world, entityName, entityNBT);
            }
            entity = previewEntity;
        }

        if (entity != null) {
            renderEntity(style, x, y, entity);
        }
        return previewEntity;
    }

    private static Entity getWorldEntity(World world, String entityName, Integer entityID) {
        if (entityID == null) {
            return null;
        }
        Entity entity = world.getEntityByID(entityID);
        return entityName == null || entityName.isEmpty() || isExpectedEntity(entity, entityName) ? entity : null;
    }

    private static boolean isValidPreview(Entity entity, World world, String entityName) {
        return entity != null && entity.world == world && isExpectedEntity(entity, entityName);
    }

    private static boolean isExpectedEntity(Entity entity, String entityName) {
        if (entity == null) {
            return false;
        }
        String actualName = EntityList.getEntityString(entity);
        return entityName.equals(actualName) || fixEntityId(entityName).equals(actualName);
    }

    private static Entity createPreviewEntity(World world, String entityName, NBTTagCompound entityNBT) {
        if (entityNBT != null) {
            return EntityList.createEntityFromNBT(entityNBT, world);
        }
        String fixed = fixEntityId(entityName);
        EntityEntry value = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(fixed));
        return value == null ? null : value.newInstance(world);
    }

    /**
     * This method attempts to fix an old-style (1.10.2) entity Id and convert it to the
     * string representation of the new ResourceLocation. The 1.10 version of this function will just return
     * the given id
     * This does not work for modded entities.
     *
     * @param id an old-style entity id as used in 1.10
     * @return
     */
    public static String fixEntityId(String id) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("id", id);
        nbt = FIXER.fixTagCompound(nbt);
        return nbt.getString("id");
    }


    private static void renderEntity(IEntityStyle style, int x, int y, Entity entity) {
        float height = entity.height;
        height = (float) ((height - 1) * .7 + 1);
        float s = style.getScale() * ((style.getHeight() * 14.0f / 25) / height);

        float renderedSize = Math.max(entity.width, height) * getRenderSize(entity) * s;
        if (renderedSize > Config.entityModelMaxSize) {
            s *= Config.entityModelMaxSize / renderedSize;
        }

        RenderHelper.renderEntity(entity, x, y, s);
    }

    private static float getRenderSize(Entity entity) {
        try {
            Method method = entity.getClass().getMethod("getRenderSize");
            Object value = method.invoke(entity);
            if (value instanceof Number) {
                return Math.max(1.0f, ((Number) value).floatValue());
            }
        } catch (ReflectiveOperationException ignored) {
        }
        return 1.0f;
    }

}
