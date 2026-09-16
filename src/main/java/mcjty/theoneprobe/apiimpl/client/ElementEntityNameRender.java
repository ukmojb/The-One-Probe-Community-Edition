package mcjty.theoneprobe.apiimpl.client;

import mcjty.theoneprobe.Tools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import static mcjty.theoneprobe.api.TextStyleClass.NAME;

public class ElementEntityNameRender {

    public static void render(int entityID, String entityName, String fallbackName, ITextComponent fixedName, int x, int y) {
        ElementTextRender.render(resolveName(entityID, entityName, fallbackName, fixedName), x, y);
    }

    public static int getWidth(int entityID, String entityName, String fallbackName, ITextComponent fixedName) {
        return ElementTextRender.getWidth(resolveName(entityID, entityName, fallbackName, fixedName));
    }

    private static ITextComponent resolveName(int entityID, String entityName, String fallbackName, ITextComponent fixedName) {
        ITextComponent name = fixedName;
        if (name == null) {
            Entity entity = getWorldEntity(entityID, entityName);
            if (entity != null) {
                name = new TextComponentString(entity.getName());
            } else {
                String translationKey = "entity." + entityName + ".name";
                name = entityName != null && !entityName.isEmpty() && I18n.hasKey(translationKey)
                        ? new TextComponentTranslation(translationKey)
                        : new TextComponentString(fallbackName);
            }
        }
        return Tools.text(NAME, name);
    }

    private static Entity getWorldEntity(int entityID, String entityName) {
        World world = Minecraft.getMinecraft().world;
        if (world == null) {
            return null;
        }
        Entity entity = world.getEntityByID(entityID);
        if (entity == null || entityName == null || entityName.isEmpty()) {
            return entity;
        }
        String actualName = EntityList.getEntityString(entity);
        return entityName.equals(actualName) || ElementEntityRender.fixEntityId(entityName).equals(actualName) ? entity : null;
    }
}
