package mcjty.theoneprobe.apiimpl.providers;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IIconStyle;
import mcjty.theoneprobe.api.ILayoutStyle;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.items.ModItems;
import net.darkhax.bookshelf.util.PlayerUtils;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.orestages.api.OreTiersAPI;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;
import static mcjty.theoneprobe.api.TextStyleClass.*;

public class HarvestInfoTools {

    private static final ResourceLocation ICONS = new ResourceLocation(TheOneProbe.MODID, "textures/gui/icons.png");
    private static final HashMap<String, ItemStack> testTools = new HashMap<>();
    private static String[] harvestLevels = new String[]{
            "stone",
            "iron",
            "diamond",
            "obsidian",
            "cobalt"
    };

    static {
        testTools.put("{*top.toolclass.Shovel*}", new ItemStack(Items.WOODEN_SHOVEL));
        testTools.put("{*top.toolclass.Axe*}", new ItemStack(Items.WOODEN_AXE));
        testTools.put("{*top.toolclass.Pickaxe*}", new ItemStack(Items.WOODEN_PICKAXE));
    }

    static void showHarvestLevel(IProbeInfo probeInfo, IBlockState blockState, Block block) {
        String harvestTool = block.getHarvestTool(blockState);
        if (harvestTool != null) {
            int harvestLevel = block.getHarvestLevel(blockState);
            String harvestName;
            if (harvestLevel >= harvestLevels.length) {
                harvestName = Integer.toString(harvestLevel);
            } else if (harvestLevel < 0) {
                harvestName = Integer.toString(harvestLevel);
            } else {
                harvestName = harvestLevels[harvestLevel];
            }
            probeInfo.text(LABEL + "Tool: " + INFO + harvestTool + " (level " + harvestName + ")");
        }
    }

    static void showCanBeHarvested(IProbeInfo probeInfo, World world, BlockPos pos, Block block, EntityPlayer player) {
        if (ModItems.isProbeInHand(player.getHeldItemMainhand())) {
            // If the player holds the probe there is no need to show harvestability information as the
            // probe cannot harvest anything. This is only supposed to work in off hand.
            return;
        }

        boolean harvestable = block.canHarvestBlock(world, pos, player) && world.getBlockState(pos).getBlockHardness(world, pos) >= 0;
        if (harvestable) {
            probeInfo.text(OK + "Harvestable");
        } else {
            probeInfo.text(WARNING + "Not harvestable");
        }
    }

    static void showHarvestInfo(IProbeInfo probeInfo, World world, BlockPos pos, Block block, IBlockState blockState, EntityPlayer player) {
        boolean harvestable = block.canHarvestBlock(world, pos, player) && world.getBlockState(pos).getBlockHardness(world, pos) >= 0;

        if (Loader.isModLoaded("orestages")) {
            Tuple<String, IBlockState> stageInfo = OreTiersAPI.getStageInfo(blockState);
            if (stageInfo != null && !GameStageHelper.clientHasStage(PlayerUtils.getClientPlayer(), stageInfo.getFirst())) {
                Block stageBlock = stageInfo.getSecond().getBlock();
                if (stageBlock.canHarvestBlock(world, pos, player) && world.getBlockState(pos).getBlockHardness(world, pos) >= 0) {
                    harvestable = true;
                } else {
                    harvestable = false;
                }
            }
        }

        String harvestTool = block.getHarvestTool(blockState);
        String harvestName = null;



        if (harvestTool == null) {
            // The block doesn't have an explicitly-set harvest tool, so we're going to test our wooden tools against the block.
            float blockHardness = blockState.getBlockHardness(world, pos);
            if (blockHardness > 0f) {
                for (Map.Entry<String, ItemStack> testToolEntry : testTools.entrySet()) {
                    // loop through our test tools until we find a winner.
                    ItemStack testTool = testToolEntry.getValue();

                    if (testTool != null && testTool.getItem() instanceof ItemTool) {
                        ItemTool toolItem = (ItemTool) testTool.getItem();
                        // @todo
                        if (testTool.getDestroySpeed(blockState) >= toolItem.toolMaterial.getEfficiency()) {
                            // BINGO!
                            harvestTool = testToolEntry.getKey();
                            break;
                        }
                    }
                }
            }
        }

        int harvestLevel = block.getHarvestLevel(blockState);

        if (Loader.isModLoaded("orestages")) {
            Tuple<String, IBlockState> stageInfo = OreTiersAPI.getStageInfo(blockState);
            if (harvestTool != null) {
                if (stageInfo != null && !GameStageHelper.clientHasStage(PlayerUtils.getClientPlayer(), stageInfo.getFirst())) {
                    IBlockState stageBlockState = stageInfo.getSecond();
                    Block stageBlock = stageInfo.getSecond().getBlock();
                    harvestTool = stageBlock.getHarvestTool(stageBlockState);
                }
            }
        }

        if (harvestLevel < 0) {
            // NOTE: When a block doesn't have an explicitly-set harvest tool, getHarvestLevel will return -1 for ANY tool. (Expected behavior)
//                TheOneProbe.logger.info("HarvestLevel out of bounds (less than 0). Found " + harvestLevel);
            if (Config.showCustomharvestLevelName) {
                if (I18n.canTranslate("top.harvestLevel.null")) {
                    harvestName = "{*top.harvestLevel.null*}";
                }
            }
        } else if (harvestLevel >= harvestLevels.length) {
//                TheOneProbe.logger.info("HarvestLevel out of bounds (Max value " + harvestLevels.length + "). Found " + harvestLevel);
        } else {
            harvestName = harvestLevels[harvestLevel];

            if (Loader.isModLoaded("orestages")) {
                Tuple<String, IBlockState> stageInfo = OreTiersAPI.getStageInfo(blockState);
                if (stageInfo != null && !GameStageHelper.clientHasStage(PlayerUtils.getClientPlayer(), stageInfo.getFirst())) {
                    IBlockState stageBlockState = stageInfo.getSecond();
                    Block stageBlock = stageInfo.getSecond().getBlock();
                    int stageharvestLevel = stageBlock.getHarvestLevel(stageBlockState);
                    harvestName = harvestLevels[stageharvestLevel];
                }
            }
            if (Config.showCustomharvestLevelName)
                    harvestName = "{*top.harvestLevel." + harvestLevel + "*}";

        }
        harvestTool = StringUtils.capitalize(harvestTool);


        boolean v = Config.harvestStyleVanilla;
        int offs = v ? 16 : 0;
        int dim = v ? 13 : 16;

        ILayoutStyle alignment = probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER);
        IIconStyle iconStyle = probeInfo.defaultIconStyle().width(v ? 18 : 20).height(v ? 14 : 16).textureWidth(32).textureHeight(32);
        IProbeInfo horizontal = probeInfo.horizontal(alignment);
        if (harvestable) {
            if (harvestTool != null) {
                String ToolClassString;

                if (I18n.canTranslate("top.toolclass." + harvestTool))
                    ToolClassString = "{*top.toolclass." + harvestTool + "*}";
                else
                    ToolClassString = harvestTool.substring(0, 1).toUpperCase() + harvestTool.substring(1);

                horizontal.icon(ICONS, 0, offs, dim, dim, iconStyle)
                        .text(OK + ToolClassString + " (" + "{*top.level*}" + " " + harvestName + ")");
            } else {
                if (isNull(harvestName)){
                    horizontal.icon(ICONS, 0, offs, dim, dim, iconStyle)
                            .text(OK + "{*top.NoTool*}");
                } else {
                    horizontal.icon(ICONS, 0, offs, dim, dim, iconStyle)
                            .text(OK + "{*top.NoTool*}" + " (" + "{*top.level*}" + " " + harvestName + ")");
                }

            }

        } else {
            if (harvestName == null || harvestName.isEmpty()) {
                horizontal.icon(ICONS, 16, offs, dim, dim, iconStyle)
                        .text(WARNING + ((harvestTool != null) ? harvestTool : "{*top.NoTool*}"));
            } else {
                if (harvestTool != null) {
                    String ToolClassString;
                    if (I18n.canTranslate("top.toolclass." + harvestTool))
                        ToolClassString = "{*top.toolclass." + harvestTool + "*}";
                    else
                        ToolClassString = harvestTool.substring(0, 1).toUpperCase() + harvestTool.substring(1);

                    horizontal.icon(ICONS, 16, offs, dim, dim, iconStyle)
                            .text(WARNING + ToolClassString + " (" + "{*top.level*}" + " " + harvestName + ")");
                } else {
                    horizontal.icon(ICONS, 16, offs, dim, dim, iconStyle)
                            .text(WARNING + "{*top.NoTool*}" + " (" + "{*top.level*}" + " " + harvestName + ")");
                }

            }
        }
    }

}
