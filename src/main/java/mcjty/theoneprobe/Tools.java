package mcjty.theoneprobe;

import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.styles.ItemStyle;
import mcjty.theoneprobe.apiimpl.styles.LayoutStyle;
import mcjty.theoneprobe.config.Config;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.*;

import static mcjty.theoneprobe.api.IProbeConfig.ConfigMode.EXTENDED;
import static mcjty.theoneprobe.api.IProbeConfig.ConfigMode.NORMAL;

public class Tools {

    private final static Map<String, String> modNamesForIds = new HashMap<>();

    public static final DecimalFormat FORMAT = new DecimalFormat("#,###.#");

    private static void init() {
        Map<String, ModContainer> modMap = Loader.instance().getIndexedModList();
        for (Map.Entry<String, ModContainer> modEntry : modMap.entrySet()) {
            String lowercaseId = modEntry.getKey().toLowerCase(Locale.ENGLISH);
            String modName = modEntry.getValue().getName();
            modNamesForIds.put(lowercaseId, modName);
        }
    }

    public static String getModName(Block block) {
        if (modNamesForIds.isEmpty()) {
            init();
        }
        ResourceLocation itemResourceLocation = block.getRegistryName();
        String modId = itemResourceLocation.getNamespace();
        String lowercaseModId = modId.toLowerCase(Locale.ENGLISH);
        String modName = modNamesForIds.get(lowercaseModId);
        if (modName == null) {
            modName = WordUtils.capitalize(modId);
            modNamesForIds.put(lowercaseModId, modName);
        }
        return modName;
    }

    public static String getModName(Entity entity) {
        if (modNamesForIds.isEmpty()) {
            init();
        }
        EntityRegistry.EntityRegistration modSpawn = EntityRegistry.instance().lookupModSpawn(entity.getClass(), true);
        if (modSpawn == null) {
            return "Minecraft";
        }
        ModContainer container = modSpawn.getContainer();
        if (container == null) {
            return "Minecraft";
        }
        String modId = container.getModId();
        String lowercaseModId = modId.toLowerCase(Locale.ENGLISH);
        String modName = modNamesForIds.get(lowercaseModId);
        if (modName == null) {
            modName = WordUtils.capitalize(modId);
            modNamesForIds.put(lowercaseModId, modName);
        }
        return modName;
    }

    public static boolean show(ProbeMode mode, IProbeConfig.ConfigMode cfg) {
        return cfg == NORMAL || (cfg == EXTENDED && mode == ProbeMode.EXTENDED);
    }

    public static void addItemStack(@Nonnull List<ItemStack> stacks, @Nonnull Set<Item> foundItems, @Nonnull ItemStack stack) {
        if (stack.isEmpty()) return;
        if (foundItems.contains(stack.getItem())) {
            for (ItemStack s : stacks) {
                if (ItemHandlerHelper.canItemStacksStack(s, stack)) {
                    s.grow(stack.getCount());
                    return;
                }
            }
        }
        // If we come here we need to append a new stack
        stacks.add(stack.copy());
        foundItems.add(stack.getItem());
    }

    public static void showChestContents(@Nonnull IProbeInfo probeInfo, @Nonnull List<ItemStack> stacks, @Nonnull ProbeMode mode) {
        IProbeInfo vertical = probeInfo.vertical(probeInfo.defaultLayoutStyle().borderColor(Config.chestContentsBorderColor).spacing(0));
        int rows = 0;
        int idx = 0;

        if (Tools.show(mode, Config.getRealConfig().getShowChestContentsDetailed()) && stacks.size() <= Config.showItemDetailThresshold) {
            for (ItemStack stackInSlot : stacks) {
                vertical.horizontal(new LayoutStyle().spacing(10).alignment(ElementAlignment.ALIGN_CENTER))
                        .item(stackInSlot, new ItemStyle().width(16).height(16))
                        .text(TextStyleClass.INFO + stackInSlot.getDisplayName());
            }
        } else {
            IProbeInfo horizontal = null;
            for (ItemStack stackInSlot : stacks) {
                if (idx % 10 == 0) {
                    horizontal = vertical.horizontal(new LayoutStyle().spacing(0));
                    rows++;
                    if (rows > 4) break;
                }
                horizontal.item(stackInSlot);
                idx++;
            }
        }
    }

    public static String ticksToElapsedTime(int ticks) {
        int i = ticks / 20;
        int j = i / 60;
        i %= 60;
        return i < 10 ? j + ":0" + i : j + ":" + i;
    }

    public static Entity getCachedEntity(MobSpawnerBaseLogic logic) {
        Entity cachedEntity = null;
        cachedEntity = AnvilChunkLoader.readWorldEntity(logic.spawnData.getNbt(), logic.getSpawnerWorld(), false);
        if (logic.spawnData.getNbt().getSize() == 1 && logic.spawnData.getNbt().hasKey("id", 8) && cachedEntity instanceof EntityLiving) {
            ((EntityLiving) cachedEntity).onInitialSpawn(logic.getSpawnerWorld().getDifficultyForLocation(new BlockPos(cachedEntity)), (IEntityLivingData)null);
        }

        return cachedEntity;
    }

    public static String getName(Entity entity) {
        if (entity.hasCustomName()) {
            return entity.getCustomNameTag();
        } else {
            String s = EntityList.getEntityString(entity);
            if (s == null) {
                s = "generic";
            }

            return "{*entity." + s + ".name*}";
        }
    }
}
