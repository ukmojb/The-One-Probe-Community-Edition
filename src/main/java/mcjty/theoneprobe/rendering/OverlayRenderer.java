package mcjty.theoneprobe.rendering;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.ProbeHitData;
import mcjty.theoneprobe.apiimpl.ProbeHitEntityData;
import mcjty.theoneprobe.apiimpl.ProbeInfo;
import mcjty.theoneprobe.apiimpl.elements.ElementProgress;
import mcjty.theoneprobe.apiimpl.elements.ElementText;
import mcjty.theoneprobe.apiimpl.providers.DefaultProbeInfoEntityProvider;
import mcjty.theoneprobe.apiimpl.providers.DefaultProbeInfoProvider;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.mods.crt.api.GameStageShow;
import mcjty.theoneprobe.network.PacketGetEntityInfo;
import mcjty.theoneprobe.network.PacketGetInfo;
import mcjty.theoneprobe.network.PacketHandler;
import mcjty.theoneprobe.network.ThrowableIdentity;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static mcjty.theoneprobe.api.TextStyleClass.ERROR;

public class OverlayRenderer {

    private static Map<Pair<Integer,BlockPos>, Pair<Long, ProbeInfo>> cachedInfo = new HashMap<>();
    private static Map<UUID, Pair<Long, ProbeInfo>> cachedEntityInfo = new HashMap<>();
    private static long lastCleanupTime = 0;

    // For a short while we keep displaying the last pair if we have no new information
    // to prevent flickering
    private static Pair<Long, ProbeInfo> lastPair;
    private static long lastPairTime = 0;

    // When the server delays too long we also show some preliminary information already
    private static long lastRenderedTime = -1;
    private static float animatedBoxX = Float.NaN;
    private static float animatedBoxY = Float.NaN;
    private static float animatedBoxW = Float.NaN;
    private static float animatedBoxH = Float.NaN;
    private static long animatedBoxLastUpdateTime = 0L;
    private static long animatedBoxLastRenderTime = 0L;
    private static final long BOX_ANIMATION_RESET_TIMEOUT_MS = 600L;
    private static final float BOX_ANIMATION_SPEED = 42.0f;
    private static final float BOX_ANIMATION_STEP_SECONDS = 1.0f / 200.0f;

    public static void registerProbeInfo(int dim, BlockPos pos, ProbeInfo probeInfo) {
        if (probeInfo == null) {
            return;
        }
        long time = System.currentTimeMillis();
        cachedInfo.put(Pair.of(dim, pos), Pair.of(time, probeInfo));
    }

    public static void registerProbeInfo(UUID uuid, ProbeInfo probeInfo) {
        if (probeInfo == null) {
            return;
        }
        long time = System.currentTimeMillis();
        cachedEntityInfo.put(uuid, Pair.of(time, probeInfo));
    }

    public static void renderHUD(ProbeMode mode, float partialTicks) {
        float dist = Config.probeDistance;

        RayTraceResult mouseOver = Minecraft.getMinecraft().objectMouseOver;

        if (mouseOver != null) {
            if (mouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
                GlStateManager.pushMatrix();

                double scale = Config.tooltipScale;

                ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
                double sw = scaledresolution.getScaledWidth_double();
                double sh = scaledresolution.getScaledHeight_double();

                setupOverlayRendering(sw * scale, sh * scale);
                renderHUDEntity(mode, mouseOver, sw * scale, sh * scale);
                setupOverlayRendering(sw, sh);
                GlStateManager.popMatrix();

                checkCleanup();
                return;
            }
        }

        EntityPlayerSP entity = Minecraft.getMinecraft().player;
        Vec3d start  = entity.getPositionEyes(partialTicks);
        Vec3d vec31 = entity.getLook(partialTicks);
        Vec3d end = start.add(vec31.x * dist, vec31.y * dist, vec31.z * dist);

        mouseOver = entity.getEntityWorld().rayTraceBlocks(start, end, Config.showLiquids);
        if (mouseOver == null) {
            return;
        }

        if (mouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
            GlStateManager.pushMatrix();

            double scale = Config.tooltipScale;

            ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
            double sw = scaledresolution.getScaledWidth_double();
            double sh = scaledresolution.getScaledHeight_double();

            setupOverlayRendering(sw * scale, sh * scale);
            renderHUDBlock(mode, mouseOver, sw * scale, sh * scale);
            setupOverlayRendering(sw, sh);

            GlStateManager.popMatrix();
        }

        checkCleanup();
    }

    public static void setupOverlayRendering(double sw, double sh) {
        GlStateManager.clear(256);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, sw, sh, 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
    }

    private static void checkCleanup() {
        long time = System.currentTimeMillis();
        if (time > lastCleanupTime + 5000) {
            cleanupCachedBlocks(time);
            cleanupCachedEntities(time);
            lastCleanupTime = time;
        }
    }

    private static void renderHUDEntity(ProbeMode mode, RayTraceResult mouseOver, double sw, double sh) {
        Entity entity = mouseOver.entityHit;
        if (entity == null) return;
        if (!Config.showEntityInfo) return;

//@todo
//        if (entity instanceof EntityDragonPart) {
//            EntityDragonPart part = (EntityDragonPart) entity;
//            if (part.entityDragonObj instanceof Entity) {
//                entity = (Entity) part.entityDragonObj;
//            }
//        }

        String entityString = EntityList.getEntityString(entity);
        if (entityString == null && !(entity instanceof EntityPlayer)) {
            // We can't show info for this entity
            return;
        }

        UUID uuid = entity.getPersistentID();

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        long time = System.currentTimeMillis();

        IAttributeInstance reachAttribute = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE);

        double currentReach = reachAttribute.getAttributeValue();

//         Enables probeDistance to control the display as well
        if (player.getDistance(entity) > (Config.probeAccommodateReach ? currentReach : Config.probeDistance)
                && Config.probeEntityDistance) return;

        Pair<Long, ProbeInfo> cacheEntry = cachedEntityInfo.get(uuid);
        if (cacheEntry == null || cacheEntry.getValue() == null) {

            // To make sure we don't ask it too many times before the server got a chance to send the answer
            // we insert a dummy entry here for a while
            if (cacheEntry == null || time >= cacheEntry.getLeft()) {
                cachedEntityInfo.put(uuid, Pair.of(time + 500, null));
                requestEntityInfo(mode, mouseOver, entity, player);
            }

            if (lastPair != null && time < lastPairTime + Config.timeout) {
                renderElements(lastPair.getRight(), Config.getDefaultOverlayStyle(), sw, sh, null);
//                lastRenderedTime = time;
            } else if (Config.waitingForServerTimeout > 0 && lastRenderedTime != -1 && time > lastRenderedTime + Config.waitingForServerTimeout) {
                // It has been a while. Show some info on client that we are
                // waiting for server information
                ProbeInfo info = getWaitingEntityInfo(mode, mouseOver, entity, player);
//                registerProbeInfo(uuid, info);
//                lastPair = Pair.of(time, info);
//                lastPairTime = time;
                renderElements(lastPair.getRight(), Config.getDefaultOverlayStyle(), sw, sh, null);
//                lastRenderedTime = time;
            }
        } else {
            if (time > cacheEntry.getLeft() + Config.timeout) {
                // This info is slightly old. Update it

                // To make sure we don't ask it too many times before the server got a chance to send the answer
                // we increase the time a bit here
                cachedEntityInfo.put(uuid, Pair.of(time + 500, cacheEntry.getRight()));
                requestEntityInfo(mode, mouseOver, entity, player);
            }
            renderElements(cacheEntry.getRight(), Config.getDefaultOverlayStyle(), sw, sh, null);
            lastRenderedTime = time;
            lastPair = cacheEntry;
            lastPairTime = time;
        }
    }

    private static void requestEntityInfo(ProbeMode mode, RayTraceResult mouseOver, Entity entity, EntityPlayerSP player) {
        PacketHandler.INSTANCE.sendToServer(new PacketGetEntityInfo(player.getEntityWorld().provider.getDimension(), mode, mouseOver, entity));
    }

    private static void renderHUDBlock(ProbeMode mode, RayTraceResult mouseOver, double sw, double sh) {
        BlockPos blockPos = mouseOver.getBlockPos();
        if (blockPos == null) {
            return;
        }
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player.getEntityWorld().isAirBlock(blockPos)) {
            return;
        }

        long time = System.currentTimeMillis();

        IElement damageElement = null;
        boolean noHasStage = true;

        if (Loader.isModLoaded("gamestages") && GameStageShow.topstage.containsKey("breakProgress")) {
            if (!GameStageHelper.hasStage(Minecraft.getMinecraft().player, GameStageShow.topstage.get("breakProgress"))) {
                noHasStage = false;
            }
        }

        if (noHasStage) {
            if (Config.showBreakProgress > 0 && !Config.isJadeTheme()) {
                float damage = Minecraft.getMinecraft().playerController.curBlockDamageMP;
                if (damage > 0) {
                    if (Config.showBreakProgress == 2) {
                        damageElement = new ElementText(TextFormatting.RED + "{*top.Progress*}" + " " + (int) (damage * 100) + "%");
                    } else {
                        damageElement = new ElementProgress((long) (damage * 100), 100, new ProgressStyle()
                                .prefix("{*top.Progress*}" + " ")
                                .suffix("%")
                                .width(85)
                                .borderColor(0)
                                .filledColor(0)
                                .filledColor(0xff990000)
                                .alternateFilledColor(0xff550000));
                    }
                }
            }
        }

        int dimension = player.getEntityWorld().provider.getDimension();
        Pair<Integer, BlockPos> key = Pair.of(dimension, blockPos);
        Pair<Long, ProbeInfo> cacheEntry = cachedInfo.get(key);
        if (cacheEntry == null || cacheEntry.getValue() == null) {

            // To make sure we don't ask it too many times before the server got a chance to send the answer
            // we insert a dummy entry here for a while
            if (cacheEntry == null || time >= cacheEntry.getLeft()) {
                cachedInfo.put(key, Pair.of(time + 500, null));
                requestBlockInfo(mode, mouseOver, blockPos, player);
            }

            if (lastPair != null && time < lastPairTime + Config.timeout) {
                renderElements(lastPair.getRight(), Config.getDefaultOverlayStyle(), sw, sh, damageElement);
                lastRenderedTime = time;
            } else if (Config.waitingForServerTimeout > 0 && lastRenderedTime != -1 && time > lastRenderedTime + Config.waitingForServerTimeout) {
                // It has been a while. Show some info on client that we are
                // waiting for server information
                ProbeInfo info = getWaitingInfo(mode, mouseOver, blockPos, player);
                registerProbeInfo(dimension, blockPos, info);
                lastPair = Pair.of(time, info);
                lastPairTime = time;
                renderElements(lastPair.getRight(), Config.getDefaultOverlayStyle(), sw, sh, damageElement);
                lastRenderedTime = time;
            }
        } else {
            if (time > cacheEntry.getLeft() + Config.timeout) {
                // This info is slightly old. Update it

                // To make sure we don't ask it too many times before the server got a chance to send the answer
                // we increase the time a bit here
                cachedInfo.put(key, Pair.of(time + 500, cacheEntry.getRight()));
                requestBlockInfo(mode, mouseOver, blockPos, player);
            }
            renderElements(cacheEntry.getRight(), Config.getDefaultOverlayStyle(), sw, sh, damageElement);
            lastRenderedTime = time;
            lastPair = cacheEntry;
            lastPairTime = time;
        }
    }

    // Information for when the server is laggy
    private static ProbeInfo getWaitingInfo(ProbeMode mode, RayTraceResult mouseOver, BlockPos blockPos, EntityPlayerSP player) {
        ProbeInfo probeInfo = TheOneProbe.theOneProbeImp.create();

        World world = player.getEntityWorld();
        IBlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        ItemStack pickBlock = block.getPickBlock(blockState, mouseOver, world, blockPos, player);
        IProbeHitData data = new ProbeHitData(blockPos, mouseOver.hitVec, mouseOver.sideHit, pickBlock);

        IProbeConfig probeConfig = TheOneProbe.theOneProbeImp.createProbeConfig();
        try {
            DefaultProbeInfoProvider.showStandardBlockInfo(probeConfig, mode, probeInfo, blockState, block, world, blockPos, player, data);
        } catch (Exception e) {
            ThrowableIdentity.registerThrowable(e);
            probeInfo.text(ERROR + "Error (see log for details)!");
        }

        probeInfo.text(ERROR + "Waiting for server...");
        return probeInfo;
    }

    private static ProbeInfo getWaitingEntityInfo(ProbeMode mode, RayTraceResult mouseOver, Entity entity, EntityPlayerSP player) {
        ProbeInfo probeInfo = TheOneProbe.theOneProbeImp.create();
        IProbeHitEntityData data = new ProbeHitEntityData(mouseOver.hitVec);

        IProbeConfig probeConfig = TheOneProbe.theOneProbeImp.createProbeConfig();
        try {
            DefaultProbeInfoEntityProvider.showStandardInfo(mode, probeInfo, entity, probeConfig);
        } catch (Exception e) {
            ThrowableIdentity.registerThrowable(e);
            probeInfo.text(ERROR + "Error (see log for details)!");
        }

        probeInfo.text(ERROR + "Waiting for server...");
        return probeInfo;
    }

    private static void requestBlockInfo(ProbeMode mode, RayTraceResult mouseOver, BlockPos blockPos, EntityPlayerSP player) {
        World world = player.getEntityWorld();
        IBlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        ItemStack pickBlock = block.getPickBlock(blockState, mouseOver, world, blockPos, player);
        if (pickBlock == null || (!pickBlock.isEmpty() && pickBlock.getItem() == null)) {
            // Protection for some invalid items.
            pickBlock = ItemStack.EMPTY;
        }
        if (pickBlock != null && (!pickBlock.isEmpty()) && Config.getDontSendNBTSet().contains(pickBlock.getItem().getRegistryName())) {
            pickBlock = pickBlock.copy();
            pickBlock.setTagCompound(null);
        }
        PacketHandler.INSTANCE.sendToServer(new PacketGetInfo(world.provider.getDimension(), blockPos, mode, mouseOver, pickBlock));
    }

    public static void renderOverlay(IOverlayStyle style, IProbeInfo probeInfo) {
        GlStateManager.pushMatrix();

        double scale = Config.tooltipScale;

        Minecraft minecraft = Minecraft.getMinecraft();
        ScaledResolution scaledresolution = new ScaledResolution(minecraft);
        double sw = scaledresolution.getScaledWidth_double();
        double sh = scaledresolution.getScaledHeight_double();

        setupOverlayRendering(sw * scale, sh * scale);
        renderElements((ProbeInfo) probeInfo, style, sw * scale, sh * scale, null);
        setupOverlayRendering(sw, sh);
        GlStateManager.popMatrix();
    }

    private static void cleanupCachedBlocks(long time) {
        // It has been a while. Time to clean up unused cached pairs.
        Map<Pair<Integer,BlockPos>, Pair<Long, ProbeInfo>> newCachedInfo = new HashMap<>();
        for (Map.Entry<Pair<Integer, BlockPos>, Pair<Long, ProbeInfo>> entry : cachedInfo.entrySet()) {
            long t = entry.getValue().getLeft();
            if (time < t + Config.timeout + 1000) {
                newCachedInfo.put(entry.getKey(), entry.getValue());
            }
        }
        cachedInfo = newCachedInfo;
    }

    private static void cleanupCachedEntities(long time) {
        // It has been a while. Time to clean up unused cached pairs.
        Map<UUID, Pair<Long, ProbeInfo>> newCachedInfo = new HashMap<>();
        for (Map.Entry<UUID, Pair<Long, ProbeInfo>> entry : cachedEntityInfo.entrySet()) {
            long t = entry.getValue().getLeft();
            if (time < t + Config.timeout + 1000) {
                newCachedInfo.put(entry.getKey(), entry.getValue());
            }
        }
        cachedEntityInfo = newCachedInfo;
    }

    public static void renderElements(ProbeInfo probeInfo, IOverlayStyle style, double sw, double sh,
                                      @Nullable IElement extra) {
        if (extra != null) {
            probeInfo.element(extra);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();

        int scaledWidth = (int) sw;
        int scaledHeight = (int) sh;

        int w = probeInfo.getWidth();
        int h = probeInfo.getHeight();

        int offset = style.getBorderOffset();
        int thick = style.getBorderThickness();
        int margin = 0;
        if (thick > 0) {
            w += (offset + thick + 3) * 2;
            h += (offset + thick + 3) * 2;
            margin = offset + thick + 3;
        }

        int x;
        int y;
        if (style.getLeftX() != -1) {
            x = style.getLeftX();
        } else if (style.getRightX() != -1) {
            x = scaledWidth - w - style.getRightX();
        } else {
            x = (scaledWidth - w) / 2;
        }
        if (style.getTopY() != -1) {
            y = style.getTopY();
        } else if (style.getBottomY() != -1) {
            y = scaledHeight - h - style.getBottomY();
        } else {
            y = (scaledHeight - h) / 2;
        }

        int drawX = x;
        int drawY = y;
        int drawW = w;
        int drawH = h;
        if (thick > 0 && Config.boxResizeAnimation) {
            long now = System.currentTimeMillis();
            boolean resetAnimation = Float.isNaN(animatedBoxW) || now - animatedBoxLastRenderTime > BOX_ANIMATION_RESET_TIMEOUT_MS;
            if (resetAnimation) {
                animatedBoxX = x;
                animatedBoxY = y;
                animatedBoxW = w;
                animatedBoxH = h;
                animatedBoxLastUpdateTime = now;
            } else {
                float dt = Math.min((now - animatedBoxLastUpdateTime) / 1000.0f, 0.2f);
                while (dt > 0.0f) {
                    float step = Math.min(dt, BOX_ANIMATION_STEP_SECONDS);
                    animatedBoxX = animateBoxValue(animatedBoxX, x, step);
                    animatedBoxY = animateBoxValue(animatedBoxY, y, step);
                    animatedBoxW = animateBoxValue(animatedBoxW, w, step);
                    animatedBoxH = animateBoxValue(animatedBoxH, h, step);
                    dt -= step;
                }

                // Pixel-aware snap: avoid a late 1px jump at the end of easing.
                if (Math.abs(animatedBoxX - x) < 0.2f) {
                    animatedBoxX = x;
                }
                if (Math.abs(animatedBoxY - y) < 0.2f) {
                    animatedBoxY = y;
                }
                if (Math.abs(animatedBoxW - w) < 0.2f) {
                    animatedBoxW = w;
                }
                if (Math.abs(animatedBoxH - h) < 0.2f) {
                    animatedBoxH = h;
                }
                animatedBoxLastUpdateTime = now;
            }
            animatedBoxLastRenderTime = now;
            float animatedRight = animatedBoxX + animatedBoxW;
            float animatedBottom = animatedBoxY + animatedBoxH;
            int drawLeft = quantizeLowerEdge(animatedBoxX, x);
            int drawTop = quantizeLowerEdge(animatedBoxY, y);
            int drawRight = quantizeUpperEdge(animatedRight, x + w);
            int drawBottom = quantizeUpperEdge(animatedBottom, y + h);
            if (drawRight <= drawLeft) {
                drawRight = drawLeft + 1;
            }
            if (drawBottom <= drawTop) {
                drawBottom = drawTop + 1;
            }
            drawX = drawLeft;
            drawY = drawTop;
            drawW = drawRight - drawLeft;
            drawH = drawBottom - drawTop;
        } else if (thick > 0) {
            animatedBoxX = Float.NaN;
            animatedBoxY = Float.NaN;
            animatedBoxW = Float.NaN;
            animatedBoxH = Float.NaN;
            animatedBoxLastUpdateTime = 0L;
            animatedBoxLastRenderTime = 0L;
        }

        if (thick > 0) {
            if (offset > 0) {
                RenderHelper.drawThickBeveledBox(drawX, drawY, drawX + drawW - 1, drawY + drawH - 1, thick, style.getBoxColor(), style.getBoxColor(), style.getBoxColor());
            }

            RenderHelper.drawThickBeveledBox(drawX + offset, drawY + offset, drawX + drawW - 1 - offset, drawY + drawH - 1 - offset, thick, style.getBorderColor(), style.getBorderColor(), style.getBoxColor());

            if (Config.isJadeTheme()) {
                RenderHelper.drawExtraBorder(drawX, drawY, drawX + drawW - 1, drawY + drawH - 1, thick, 0x88121212);

                float damage = Minecraft.getMinecraft().playerController.curBlockDamageMP;
                RenderHelper.drawJadeBreakProgress(drawX, drawY, drawX + drawW - 1, drawY + drawH - 1, thick, 0xFFFFFFFF, damage);
            }

        }

        if (!Minecraft.getMinecraft().isGamePaused()) {
            RenderHelper.rot += .5f;
        }

        probeInfo.render(drawX + margin, drawY + margin);
        if (extra != null) {
            probeInfo.removeElement(extra);
        }
    }

    private static float animateBoxValue(float current, float target, float dt) {
        float lerp = 1.0f - (float) Math.exp(-BOX_ANIMATION_SPEED * dt);
        return current + (target - current) * lerp;
    }

    private static int quantizeLowerEdge(float current, float target) {
        return target >= current ? (int) Math.floor(current) : (int) Math.ceil(current);
    }

    private static int quantizeUpperEdge(float current, float target) {
        return target >= current ? (int) Math.ceil(current) : (int) Math.floor(current);
    }
}
