package com.autobuild.client.handler;

import com.autobuild.AutoBuild;
import com.autobuild.common.config.AutoBuildConfig;
import com.autobuild.common.data.BuildStructure;
import com.autobuild.common.data.StructureManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = AutoBuild.MOD_ID, value = Dist.CLIENT)
public class AutoBuildHandler {
    
    private static int tickCounter = 0;
    private static int currentBlockIndex = 0;
    private static BlockPos buildOrigin = null;
    private static Map<BlockPos, String> targetPositions = null;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (!AutoBuildConfig.isEnabled()) {
            resetBuild();
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        Level level = mc.level;

        if (player == null || level == null || mc.screen != null) {
            return;
        }

        String selectedStructure = AutoBuildConfig.getSelectedStructure();
        if (selectedStructure == null || selectedStructure.isEmpty()) {
            return;
        }

        BuildStructure structure = StructureManager.getStructure(selectedStructure);
        if (structure == null || structure.getBlockCount() == 0) {
            return;
        }

        int buildDelay = 11 - AutoBuildConfig.getBuildSpeed();
        tickCounter++;
        
        if (tickCounter < buildDelay) {
            return;
        }
        tickCounter = 0;

        if (SelectionManager.hasFixedHitbox()) {
            buildOrigin = SelectionManager.getFixedHitboxOrigin().above();
        } else {
            HitResult hitResult = mc.hitResult;
            if (hitResult == null || hitResult.getType() == HitResult.Type.MISS) {
                BlockPos playerPos = player.blockPosition();
                buildOrigin = playerPos.relative(player.getDirection(), AutoBuildConfig.getProximityDistance());
            } else if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) hitResult;
                buildOrigin = blockHit.getBlockPos().relative(blockHit.getDirection());
            } else {
                return;
            }
        }

        targetPositions = structure.getRotatedBlocks(buildOrigin);

        if (targetPositions.isEmpty()) {
            return;
        }

        Map.Entry<BlockPos, String> nextBlock = findNextBuildablePosition(player, level, targetPositions);
        if (nextBlock != null) {
            int slot = findBlockInInventory(player, nextBlock.getValue());
            if (slot != -1) {
                if (slot != player.getInventory().selected) {
                    player.getInventory().selected = slot;
                }
                ItemStack heldItem = player.getMainHandItem();
                if (heldItem.getItem() instanceof BlockItem) {
                    placeBlockAt(mc, player, level, nextBlock.getKey(), heldItem);
                }
            }
        }
    }

    private static Map.Entry<BlockPos, String> findNextBuildablePosition(LocalPlayer player, Level level, Map<BlockPos, String> positions) {
        int proximity = AutoBuildConfig.getProximityDistance();
        double maxDistanceSq = proximity * proximity;
        
        for (Map.Entry<BlockPos, String> entry : positions.entrySet()) {
            BlockPos pos = entry.getKey();
            
            if (!level.getBlockState(pos).isAir()) {
                continue;
            }

            double distanceSq = player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            if (distanceSq <= maxDistanceSq * 4) {
                return entry;
            }
        }
        return null;
    }

    private static int findBlockInInventory(LocalPlayer player, String blockId) {
        Inventory inventory = player.getInventory();
        
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                String itemBlockId = BuiltInRegistries.BLOCK.getKey(block).toString();
                if (itemBlockId.equals(blockId)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static void placeBlockAt(Minecraft mc, LocalPlayer player, Level level, BlockPos pos, ItemStack heldItem) {
        if (!(heldItem.getItem() instanceof BlockItem blockItem)) {
            return;
        }

        BlockPos below = pos.below();
        Direction placeDirection = Direction.UP;
        
        if (!level.getBlockState(below).isAir()) {
            placeDirection = Direction.UP;
        } else {
            for (Direction dir : Direction.values()) {
                BlockPos adjacent = pos.relative(dir);
                if (!level.getBlockState(adjacent).isAir()) {
                    placeDirection = dir.getOpposite();
                    break;
                }
            }
        }

        BlockHitResult hitResult = new BlockHitResult(
                Vec3.atCenterOf(pos),
                placeDirection,
                pos.relative(placeDirection.getOpposite()),
                false
        );

        mc.gameMode.useItemOn(player, InteractionHand.MAIN_HAND, hitResult);
    }

    private static void resetBuild() {
        currentBlockIndex = 0;
        buildOrigin = null;
        targetPositions = null;
    }
}
