package com.autobuild.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectionManager {
    private static BlockPos pos1 = null;
    private static BlockPos pos2 = null;
    private static BlockPos fixedHitboxOrigin = null;
    
    public static void setPos1(BlockPos pos) {
        pos1 = pos;
    }
    
    public static void setPos2(BlockPos pos) {
        pos2 = pos;
    }
    
    public static BlockPos getPos1() {
        return pos1;
    }
    
    public static BlockPos getPos2() {
        return pos2;
    }
    
    public static boolean hasSelection() {
        return pos1 != null && pos2 != null;
    }
    
    public static boolean hasPartialSelection() {
        return pos1 != null;
    }
    
    public static void clearSelection() {
        pos1 = null;
        pos2 = null;
    }
    
    public static void setFixedHitboxOrigin(BlockPos pos) {
        fixedHitboxOrigin = pos;
    }
    
    public static BlockPos getFixedHitboxOrigin() {
        return fixedHitboxOrigin;
    }
    
    public static void clearFixedHitboxOrigin() {
        fixedHitboxOrigin = null;
    }
    
    public static boolean hasFixedHitbox() {
        return fixedHitboxOrigin != null;
    }
    
    public static List<BlockPos> getSelectedBlocks() {
        List<BlockPos> blocks = new ArrayList<>();
        
        if (!hasSelection()) {
            return blocks;
        }
        
        int minX = Math.min(pos1.getX(), pos2.getX());
        int minY = Math.min(pos1.getY(), pos2.getY());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int maxY = Math.max(pos1.getY(), pos2.getY());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    blocks.add(new BlockPos(x - minX, y - minY, z - minZ));
                }
            }
        }
        
        return blocks;
    }
    
    public static Map<BlockPos, String> getSelectedBlocksWithTypes() {
        Map<BlockPos, String> blocks = new HashMap<>();
        
        if (!hasSelection()) {
            return blocks;
        }
        
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null) {
            return blocks;
        }
        
        int minX = Math.min(pos1.getX(), pos2.getX());
        int minY = Math.min(pos1.getY(), pos2.getY());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int maxY = Math.max(pos1.getY(), pos2.getY());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos worldPos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(worldPos);
                    
                    if (!state.isAir()) {
                        BlockPos relativePos = new BlockPos(x - minX, y - minY, z - minZ);
                        String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
                        blocks.put(relativePos, blockId);
                    }
                }
            }
        }
        
        return blocks;
    }
    
    public static List<BlockPos> getSelectedBlocksAbsolute() {
        List<BlockPos> blocks = new ArrayList<>();
        
        if (!hasSelection()) {
            return blocks;
        }
        
        int minX = Math.min(pos1.getX(), pos2.getX());
        int minY = Math.min(pos1.getY(), pos2.getY());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int maxY = Math.max(pos1.getY(), pos2.getY());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    blocks.add(new BlockPos(x, y, z));
                }
            }
        }
        
        return blocks;
    }
}
