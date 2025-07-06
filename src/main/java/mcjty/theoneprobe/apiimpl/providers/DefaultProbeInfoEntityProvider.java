package mcjty.theoneprobe.apiimpl.providers;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.styles.ItemStyle;
import mcjty.theoneprobe.apiimpl.styles.LayoutStyle;
import mcjty.theoneprobe.compat.event.SpecialNameEvent;
import mcjty.theoneprobe.config.Config;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.text.DecimalFormat;
import java.util.*;

import static mcjty.theoneprobe.api.IProbeInfo.ENDLOC;
import static mcjty.theoneprobe.api.IProbeInfo.STARTLOC;
import static mcjty.theoneprobe.api.TextStyleClass.*;

public class DefaultProbeInfoEntityProvider implements IProbeInfoEntityProvider {

    private static DecimalFormat dfCommas = new DecimalFormat("##.#");
    private final List<ItemStack> ChestHorsestacks = new ArrayList<>();
    private final Set<Item> ChestHorsefoundItems = new HashSet<>();
    private final List<ItemStack> MinecartContainerstacks = new ArrayList<>();
    private final Set<Item> MinecartContainerfoundItems = new HashSet<>();

    public static String getPotionDurationString(PotionEffect effect, float factor) {
        if (effect.getDuration() == 32767) {
            return "**:**";
        } else {
            int i = MathHelper.floor(effect.getDuration() * factor);
            return ticksToElapsedTime(i);
        }
    }

    public static String ticksToElapsedTime(int ticks) {
        int i = ticks / 20;
        int j = i / 60;
        i = i % 60;
        return i < 10 ? j + ":0" + i : j + ":" + i;
    }

    public static String getName(Entity entity) {
        if (entity.hasCustomName()) {
            return entity.getCustomNameTag();
        } else {
            String s;

            SpecialNameEvent event = new SpecialNameEvent(entity);
            MinecraftForge.EVENT_BUS.post(event);
            s = event.getSpacialName();

            if (s == null) {
                s = EntityList.getEntityString(entity) == null ? "generic" : EntityList.getEntityString(entity);
            }

            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                return player.getName();
            }

            return "{*entity." + s + ".name*}";
        }
    }

    public static void showStandardInfo(ProbeMode mode, IProbeInfo probeInfo, Entity entity, IProbeConfig config) {
        String modid = Tools.getModName(entity);
        if (Tools.show(mode, config.getShowModName())) {
            if (Config.showEntityModel) {
                probeInfo.horizontal()
                        .entity(entity)
                        .vertical()
                        .text(NAME + getName(entity))
                        .text(MODNAME + modid);
            } else {
                probeInfo.horizontal()
                        .vertical()
                        .text(NAME + getName(entity))
                        .text(MODNAME + modid);
            }
        } else {
            if (Config.showEntityModel) {
                probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                        .entity(entity)
                        .text(NAME + getName(entity));
            } else {
                probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                        .text(NAME + getName(entity));
            }
        }
    }

    @Override
    public String getID() {
        return TheOneProbe.MODID + ":entity.default";
    }

    @Override
    public void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) {
        IProbeConfig config = Config.getRealConfig();

        if (!Config.showEntityInfo) return;

        boolean handled = false;
        for (IEntityDisplayOverride override : TheOneProbe.theOneProbeImp.getEntityOverrides()) {
            if (override.overrideStandardInfo(mode, probeInfo, player, world, entity, data)) {
                handled = true;
                break;
            }
        }
        if (!handled) {
            showStandardInfo(mode, probeInfo, entity, config);
        }

        if (entity instanceof EntityLivingBase) {
            EntityLivingBase livingBase = (EntityLivingBase) entity;

            if (Tools.show(mode, config.getShowMobHealth())) {
                int health = (int) livingBase.getHealth();
                int maxHealth = (int) livingBase.getMaxHealth();
                int armor = livingBase.getTotalArmorValue();

                if (Config.showEntityHealth) {
                    probeInfo.progress(health, maxHealth, probeInfo.defaultProgressStyle().lifeBar(true).showText(false).width(150).height(10));
                }

                if (mode == ProbeMode.EXTENDED && Config.showEntityHealth) {
                    probeInfo.text(LABEL + "{*top.Health*}" + ": " + INFOIMP + health + " / " + maxHealth);
                }

                if (armor > 0 && Config.showEntityArmor) {
                    probeInfo.progress(armor, armor, probeInfo.defaultProgressStyle().armorBar(true).showText(false).width(80).height(10));
                }
            }

            if (Tools.show(mode, config.getShowMobGrowth()) && entity instanceof EntityAgeable) {
                int age = ((EntityAgeable) entity).getGrowingAge();
                if (age < 0) {
                    probeInfo.text(LABEL + "{*top.Growing_time*}" + ": " + ((age * -1) / 20) + "{*top.Second*}");
                }
            }

            if (Tools.show(mode, config.getShowMobPotionEffects())) {
                Collection<PotionEffect> effects = livingBase.getActivePotionEffects();
                if (!effects.isEmpty()) {
                    IProbeInfo vertical = probeInfo.vertical(probeInfo.defaultLayoutStyle().borderColor(0xffffffff));
                    float durationFactor = 1.0f;
                    for (PotionEffect effect : effects) {
                        String s1 = STARTLOC + effect.getEffectName() + ENDLOC;
                        Potion potion = effect.getPotion();
                        if (effect.getAmplifier() > 0) {
                            s1 = s1 + " " + STARTLOC + "potion.potency." + effect.getAmplifier() + ENDLOC;
                        }

                        if (effect.getDuration() > 20) {
                            s1 = s1 + " (" + getPotionDurationString(effect, durationFactor) + ")";
                        }

                        if (potion.isBadEffect()) {
                            vertical.text(ERROR + s1);
                        } else {
                            vertical.text(OK + s1);
                        }
                    }
                }
            }
        } else if (entity instanceof EntityItemFrame) {
            EntityItemFrame itemFrame = (EntityItemFrame) entity;
            ItemStack stack = itemFrame.getDisplayedItem();
            if (!stack.isEmpty()) {
                probeInfo.horizontal(new LayoutStyle().spacing(10).alignment(ElementAlignment.ALIGN_CENTER))
                        .item(stack, new ItemStyle().width(16).height(16))
                        .text(INFO + stack.getDisplayName());
                if (mode == ProbeMode.EXTENDED) {
                    probeInfo.text(LABEL + "{*top.Rotation*}" + ": " + INFO + itemFrame.getRotation());
                }
            } else {
                probeInfo.text(LABEL + "{*top.Empty*}");
            }
        }

        if (Tools.show(mode, config.getAnimalOwnerSetting())) {
            UUID ownerId = null;
            if (entity instanceof IEntityOwnable) {
                ownerId = ((IEntityOwnable) entity).getOwnerId();
            } else if (entity instanceof EntityHorse) {
                ownerId = ((EntityHorse) entity).getOwnerUniqueId();
            }

            if (ownerId != null) {
                String username = UsernameCache.getLastKnownUsername(ownerId);
                if (username == null) {
                    probeInfo.text(WARNING + "{*top.Unknown_owner*}");
                } else {
                    probeInfo.text(LABEL + "{*top.Owned_by*}" + ": " + INFO + username);
                }
            } else if (entity instanceof EntityTameable) {
                probeInfo.text(LABEL + "{*top.Tameable*}");
            }
        }

        if (Tools.show(mode, config.getHorseStatSetting())) {
            if (entity instanceof EntityHorse) {
                double jumpStrength = ((EntityHorse) entity).getHorseJumpStrength();
                double jumpHeight = -0.1817584952 * jumpStrength * jumpStrength * jumpStrength + 3.689713992 * jumpStrength * jumpStrength + 2.128599134 * jumpStrength - 0.343930367;
                probeInfo.text(LABEL + "{*top.Jump_height*}" + ": " + INFO + dfCommas.format(jumpHeight));
                IAttributeInstance iattributeinstance = ((EntityHorse) entity).getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
                probeInfo.text(LABEL + "{*top.Speed*}" + ": " + INFO + dfCommas.format(iattributeinstance.getAttributeValue()));
            }
        }

        if (entity instanceof EntityWolf && Config.showCollarColor) {
            if (((EntityWolf) entity).isTamed()) {
                EnumDyeColor collarColor = ((EntityWolf) entity).getCollarColor();
                probeInfo.text(LABEL + "{*top.Collar*}" + ": " + INFO + collarColor.getName());
            }
        }

        if (entity instanceof EntityVillager) {
            EntityVillager villager = (EntityVillager) entity;
            int careerId = villager.serializeNBT().getInteger("Career");
            VillagerRegistry.VillagerCareer career = villager.getProfessionForge().getCareer(careerId);

            int careerLevel = villager.serializeNBT().getInteger("CareerLevel");

            if (Config.showVillagerCareer) probeInfo.text("{*top.Career*}" + ": " + "{*top.Career." + career.getName() + "*}");

            if (Config.showVillagerCareerLevel) probeInfo.text("{*top.CareerLevel*}" + ": " + "{*top.CareerLevel." + careerLevel + "*}");
        }

        if (entity instanceof EntityTameable) {
            EntityTameable tameable = (EntityTameable) entity;

            if (tameable.isTamed()) {
                probeInfo.text(TextStyleClass.OK + "{*top.tamed*}");
                probeInfo.text(TextStyleClass.LABEL + (tameable.isSitting() ? "{*top.sitting*}" : "{*top.standing*}"));
            } else {
                probeInfo.text(TextStyleClass.LABEL + "{*top.not_tamed*}");
            }
        }

        if (entity instanceof EntityTNTPrimed) {
            probeInfo.text(TextStyleClass.LABEL + "{*top.tnt_fuse*} " + TextStyleClass.WARNING + Tools.ticksToElapsedTime(((EntityTNTPrimed) entity).getFuse()));
        }

        if (entity instanceof EntityChicken) {
            EntityChicken chicken = (EntityChicken) entity;
            probeInfo.text(TextStyleClass.LABEL + "{*top.chicken_egg*} " + TextStyleClass.INFOIMP + Tools.ticksToElapsedTime(chicken.timeUntilNextEgg));
        }

        if (entity instanceof AbstractChestHorse && ((AbstractChestHorse) entity).hasChest()) {
            int maxSlots;
            if (entity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
                IItemHandler capability = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (capability == null) {
                    ChestHorsestacks.clear();
                    ChestHorsefoundItems.clear();
                    return;
                }

                maxSlots = capability.getSlots();
                // start at 3 to ignore armor/saddle
                for (int i = 3; i < maxSlots; i++) {
                    Tools.addItemStack(ChestHorsestacks, ChestHorsefoundItems, capability.getStackInSlot(i));
                }
            } else {
                NBTTagCompound compound = entity.writeToNBT(new NBTTagCompound());
                if (!compound.hasKey("Items")) {
                    ChestHorsestacks.clear();
                    ChestHorsefoundItems.clear();
                    return;
                }

                NBTTagList list = compound.getTagList("Items", 10);
                for (int i = 0; i < list.tagCount(); i++) {
                    NBTTagCompound tagCompound = list.getCompoundTagAt(i);
                    int slot = tagCompound.getByte("Slot") & 255;

                    // start at 3 to ignore armor/saddle
                    if (slot > 2) Tools.addItemStack(ChestHorsestacks, ChestHorsefoundItems, new ItemStack(tagCompound));
                }
            }
            if (!ChestHorsestacks.isEmpty()) Tools.showChestContents(probeInfo, ChestHorsestacks, mode);
            ChestHorsestacks.clear();
            ChestHorsefoundItems.clear();
        }

        if (entity instanceof EntityAnimal) {
            EntityAnimal animal = (EntityAnimal) entity;
            int age = animal.getGrowingAge();

            // adult
            if (age > 0) {
                probeInfo.text(TextStyleClass.LABEL + "{*top.breeding_cooldown*} " + TextStyleClass.INFOIMP + Tools.ticksToElapsedTime(age));
            }
        }

        if (entity instanceof EntityPainting) {
            NBTTagCompound compound = entity.writeToNBT(new NBTTagCompound());
            if (compound.hasKey("Motive")) {
                probeInfo.text(TextStyleClass.INFO + TextFormatting.ITALIC.toString() +
                        org.apache.commons.lang3.StringUtils.join(org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase(compound.getString("Motive")), ' '));
            }
        }

        if (entity instanceof EntityMinecartContainer) {
            int maxSlots;
            if (entity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
                IItemHandler capability = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (capability == null) {
                    MinecartContainerstacks.clear();
                    MinecartContainerfoundItems.clear();
                    return;
                }

                maxSlots = capability.getSlots();
                for (int i = 0; i < maxSlots; i++) {
                    Tools.addItemStack(MinecartContainerstacks, MinecartContainerfoundItems, capability.getStackInSlot(i));
                }
            } else {
                IInventory inventory = (IInventory) entity;
                maxSlots = inventory.getSizeInventory();
                for (int i = 0; i < maxSlots; i++) {
                    Tools.addItemStack(MinecartContainerstacks, MinecartContainerfoundItems, inventory.getStackInSlot(i));
                }
            }
            if (!MinecartContainerstacks.isEmpty()) Tools.showChestContents(probeInfo, MinecartContainerstacks, mode);
            MinecartContainerstacks.clear();
            MinecartContainerfoundItems.clear();
        }
    }
}
