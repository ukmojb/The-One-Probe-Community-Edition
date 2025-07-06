package mcjty.theoneprobe.apiimpl.providers;

import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IProbeConfig;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ItemStyle;
import mcjty.theoneprobe.apiimpl.styles.LayoutStyle;
import mcjty.theoneprobe.config.Config;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChestInfoTools {

    static void showChestInfo(ProbeMode mode, IProbeInfo probeInfo, World world, BlockPos pos, IProbeConfig config) {
        List<ItemStack> stacks = null;
        IProbeConfig.ConfigMode chestMode = config.getShowChestContents();
        if (chestMode == IProbeConfig.ConfigMode.EXTENDED && (Config.showSmallChestContentsWithoutSneaking > 0 || !Config.getInventoriesToShow().isEmpty())) {
            if (Config.getInventoriesToShow().contains(world.getBlockState(pos).getBlock().getRegistryName())) {
                chestMode = IProbeConfig.ConfigMode.NORMAL;
            } else if (Config.showSmallChestContentsWithoutSneaking > 0) {
                stacks = new ArrayList<>();
                int slots = getChestContents(world, pos, stacks);
                if (slots <= Config.showSmallChestContentsWithoutSneaking) {
                    chestMode = IProbeConfig.ConfigMode.NORMAL;
                }
            }
        } else if (chestMode == IProbeConfig.ConfigMode.NORMAL && !Config.getInventoriesToNotShow().isEmpty()) {
            if (Config.getInventoriesToNotShow().contains(world.getBlockState(pos).getBlock().getRegistryName())) {
                chestMode = IProbeConfig.ConfigMode.EXTENDED;
            }
        }

        if (Tools.show(mode, chestMode)) {
            if (stacks == null) {
                stacks = new ArrayList<>();
                getChestContents(world, pos, stacks);
            }

            if (!stacks.isEmpty()) {
                boolean showDetailed = Tools.show(mode, config.getShowChestContentsDetailed()) && stacks.size() <= Config.showItemDetailThresshold;
                showChestContents(probeInfo, world, pos, stacks, showDetailed);
            }
        }
    }

    private static void addItemStack(List<ItemStack> stacks, Set<Item> foundItems, @Nonnull ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (foundItems != null && foundItems.contains(stack.getItem())) {
            for (ItemStack s : stacks) {
                if (ItemHandlerHelper.canItemStacksStack(s, stack)) {
                    s.grow(stack.getCount());
                    return;
                }
            }
        }
        // If we come here we need to append a new stack
        stacks.add(stack.copy());
        if (foundItems != null) {
            foundItems.add(stack.getItem());
        }
    }

    private static void showChestContents(IProbeInfo probeInfo, World world, BlockPos pos, List<ItemStack> stacks, boolean detailed) {
        IProbeInfo vertical = null;
        IProbeInfo horizontal = null;

        int rows = 0;
        int idx = 0;

        vertical = probeInfo.vertical(probeInfo.defaultLayoutStyle().borderColor(Config.chestContentsBorderColor).spacing(0));

        if (detailed) {
            for (ItemStack stackInSlot : stacks) {
                horizontal = vertical.horizontal(new LayoutStyle().spacing(10).alignment(ElementAlignment.ALIGN_CENTER));
                horizontal.item(stackInSlot, new ItemStyle().width(16).height(16))
                        .itemLabel(stackInSlot);
            }
        } else {
            for (ItemStack stackInSlot : stacks) {
                if (idx % 10 == 0) {
                    horizontal = vertical.horizontal(new LayoutStyle().spacing(0));
                    rows++;
                    if (rows > 4) {
                        break;
                    }
                }
                horizontal.item(stackInSlot);
                idx++;
            }
        }
    }

    private static int getChestContents(World world, BlockPos pos, List<ItemStack> stacks) {
        TileEntity te = world.getTileEntity(pos);

        Set<Item> foundItems = Config.compactEqualStacks ? new HashSet<>() : null;
        int maxSlots = 0;
        try {
            if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
                IItemHandler capability = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (capability != null) {
                    maxSlots = capability.getSlots();
                } else {
                    for (EnumFacing facing : EnumFacing.VALUES) {
                        capability = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
                        if (capability != null) {
                            maxSlots = capability.getSlots();
                            break;
                        }
                    }
                }
                for (int i = 0; i < maxSlots; i++) {
                    addItemStack(stacks, foundItems, capability.getStackInSlot(i));
                }
            } else if (te instanceof IInventory) {
                IInventory inventory = (IInventory) te;
                maxSlots = inventory.getSizeInventory();
                for (int i = 0; i < maxSlots; i++) {
                    addItemStack(stacks, foundItems, inventory.getStackInSlot(i));
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Getting the contents of a " + world.getBlockState(pos).getBlock().getRegistryName() + " (" + te.getClass().getName() + ")", e);
        }
        return maxSlots;
    }
}
