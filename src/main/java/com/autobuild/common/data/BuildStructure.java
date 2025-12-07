package com.autobuild.common.data;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildStructure {
    private final String name;
    private final Map<BlockPos, String> blocks;
    private int rotation;

    public BuildStructure(String name) {
        this.name = name;
        this.blocks = new HashMap<>();
        this.rotation = 0;
    }

    public String getName() {
        return name;
    }

    public Map<BlockPos, String> getBlocks() {
        return blocks;
    }

    public List<BlockPos> getBlockPositions() {
        return new ArrayList<>(blocks.keySet());
    }

    public void addBlock(BlockPos pos, String blockId) {
        if (!blocks.containsKey(pos)) {
            blocks.put(pos, blockId);
        }
    }

    public void addBlock(BlockPos pos) {
        addBlock(pos, "minecraft:stone");
    }

    public String getBlockType(BlockPos pos) {
        return blocks.get(pos);
    }

    public void removeBlock(BlockPos pos) {
        blocks.remove(pos);
    }

    public void clearBlocks() {
        blocks.clear();
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation % 360;
    }

    public void rotateRight() {
        this.rotation = (rotation + 90) % 360;
    }

    public void rotateLeft() {
        this.rotation = (rotation - 90 + 360) % 360;
    }

    public Map<BlockPos, String> getRotatedBlocks(BlockPos origin) {
        Map<BlockPos, String> rotatedBlocks = new HashMap<>();
        
        for (Map.Entry<BlockPos, String> entry : blocks.entrySet()) {
            BlockPos rotated = rotateBlockPos(entry.getKey(), rotation);
            rotatedBlocks.put(origin.offset(rotated), entry.getValue());
        }
        
        return rotatedBlocks;
    }

    private BlockPos rotateBlockPos(BlockPos pos, int rotation) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        switch (rotation) {
            case 90:
                return new BlockPos(-z, y, x);
            case 180:
                return new BlockPos(-x, y, -z);
            case 270:
                return new BlockPos(z, y, -x);
            default:
                return pos;
        }
    }

    public int getBlockCount() {
        return blocks.size();
    }

    public BlockPos getMinBounds() {
        if (blocks.isEmpty()) {
            return BlockPos.ZERO;
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;

        for (BlockPos pos : blocks.keySet()) {
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
        }

        return new BlockPos(minX, minY, minZ);
    }

    public BlockPos getMaxBounds() {
        if (blocks.isEmpty()) {
            return BlockPos.ZERO;
        }

        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (BlockPos pos : blocks.keySet()) {
            maxX = Math.max(maxX, pos.getX());
            maxY = Math.max(maxY, pos.getY());
            maxZ = Math.max(maxZ, pos.getZ());
        }

        return new BlockPos(maxX, maxY, maxZ);
    }

    public Vec3 getCenter() {
        if (blocks.isEmpty()) {
            return Vec3.ZERO;
        }

        BlockPos min = getMinBounds();
        BlockPos max = getMaxBounds();

        return new Vec3(
                (min.getX() + max.getX()) / 2.0,
                (min.getY() + max.getY()) / 2.0,
                (min.getZ() + max.getZ()) / 2.0
        );
    }
}
