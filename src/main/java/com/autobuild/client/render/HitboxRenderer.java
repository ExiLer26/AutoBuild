package com.autobuild.client.render;

import com.autobuild.AutoBuild;
import com.autobuild.client.handler.SelectionManager;
import com.autobuild.common.config.AutoBuildConfig;
import com.autobuild.common.data.BuildStructure;
import com.autobuild.common.data.StructureManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = AutoBuild.MOD_ID, value = Dist.CLIENT)
public class HitboxRenderer {

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        if (!AutoBuildConfig.isEnabled() || !AutoBuildConfig.isHitboxVisible()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        Level level = mc.level;

        if (player == null || level == null) {
            return;
        }

        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        if (SelectionManager.hasPartialSelection()) {
            renderSelectionOutlines(buffer, poseStack.last().pose(), level);
        }

        String selectedStructure = AutoBuildConfig.getSelectedStructure();
        if (selectedStructure != null && !selectedStructure.isEmpty()) {
            BuildStructure structure = StructureManager.getStructure(selectedStructure);
            if (structure != null && structure.getBlockCount() > 0) {
                renderStructureOutlines(buffer, poseStack.last().pose(), level, player, mc, structure);
            }
        }

        tesselator.end();

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        poseStack.popPose();
    }

    private static void renderSelectionOutlines(BufferBuilder buffer, Matrix4f matrix, Level level) {
        BlockPos pos1 = SelectionManager.getPos1();
        if (pos1 != null) {
            renderBlockOutline(buffer, matrix, pos1, 1.0f, 1.0f, 0.0f, 1.0f);
        }

        BlockPos pos2 = SelectionManager.getPos2();
        if (pos2 != null) {
            renderBlockOutline(buffer, matrix, pos2, 0.0f, 1.0f, 1.0f, 1.0f);

            if (pos1 != null) {
                List<BlockPos> selectedBlocks = SelectionManager.getSelectedBlocksAbsolute();
                for (BlockPos pos : selectedBlocks) {
                    if (!pos.equals(pos1) && !pos.equals(pos2)) {
                        renderBlockOutline(buffer, matrix, pos, 0.5f, 0.5f, 1.0f, 0.5f);
                    }
                }
            }
        }
    }

    private static void renderStructureOutlines(BufferBuilder buffer, Matrix4f matrix, Level level, 
                                                 Player player, Minecraft mc, BuildStructure structure) {
        BlockPos origin;

        if (SelectionManager.hasFixedHitbox()) {
            origin = SelectionManager.getFixedHitboxOrigin().above();
        } else {
            HitResult hitResult = mc.hitResult;

            if (hitResult == null || hitResult.getType() == HitResult.Type.MISS) {
                BlockPos playerPos = player.blockPosition();
                origin = playerPos.relative(player.getDirection(), AutoBuildConfig.getProximityDistance());
            } else if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) hitResult;
                origin = blockHit.getBlockPos().relative(blockHit.getDirection());
            } else {
                return;
            }
        }

        Map<BlockPos, String> positions = structure.getRotatedBlocks(origin);

        for (BlockPos pos : positions.keySet()) {
            boolean isEmpty = level.getBlockState(pos).isAir();

            float r = isEmpty ? 0.0f : 1.0f;
            float g = isEmpty ? 1.0f : 0.0f;
            float b = 0.0f;
            float a = 0.8f;

            renderBlockOutline(buffer, matrix, pos, r, g, b, a);
        }
    }

    private static void renderBlockOutline(BufferBuilder buffer, Matrix4f matrix, BlockPos pos, 
                                           float r, float g, float b, float a) {
        float x1 = pos.getX();
        float y1 = pos.getY();
        float z1 = pos.getZ();
        float x2 = x1 + 1;
        float y2 = y1 + 1;
        float z2 = z1 + 1;

        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x2, y1, z1).color(r, g, b, a).endVertex();

        buffer.vertex(matrix, x2, y1, z1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x2, y1, z2).color(r, g, b, a).endVertex();

        buffer.vertex(matrix, x2, y1, z2).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x1, y1, z2).color(r, g, b, a).endVertex();

        buffer.vertex(matrix, x1, y1, z2).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a).endVertex();

        buffer.vertex(matrix, x1, y2, z1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x2, y2, z1).color(r, g, b, a).endVertex();

        buffer.vertex(matrix, x2, y2, z1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a).endVertex();

        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x1, y2, z2).color(r, g, b, a).endVertex();

        buffer.vertex(matrix, x1, y2, z2).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x1, y2, z1).color(r, g, b, a).endVertex();

        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x1, y2, z1).color(r, g, b, a).endVertex();

        buffer.vertex(matrix, x2, y1, z1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x2, y2, z1).color(r, g, b, a).endVertex();

        buffer.vertex(matrix, x2, y1, z2).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a).endVertex();

        buffer.vertex(matrix, x1, y1, z2).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x1, y2, z2).color(r, g, b, a).endVertex();
    }
}