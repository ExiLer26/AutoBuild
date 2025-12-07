package com.autobuild.common.data;

import net.minecraft.core.BlockPos;
import java.util.ArrayList;
import java.util.List;

public class StructureData {
    private final String name;
    private final List<BlockPos> blocks;
    private BlockPos fixedHitbox;
    private int rotation = 0;
    
    public StructureData(String name, List<BlockPos> blocks) {
        this.name = name;
        this.blocks = blocks != null ? blocks : new ArrayList<>();
        this.fixedHitbox = null;
    }
    
    public String getName() {
        return name;
    }
    
    public List<BlockPos> getBlocks() {
        return blocks;
    }
    
    public BlockPos getFixedHitbox() {
        return fixedHitbox;
    }
    
    public void setFixedHitbox(BlockPos pos) {
        this.fixedHitbox = pos;
    }
    
    public void clearFixedHitbox() {
        this.fixedHitbox = null;
    }
    
    public int getRotation() {
        return rotation;
    }
    
    public void setRotation(int rotation) {
        this.rotation = rotation % 4;
    }
    
    public void rotateClockwise() {
        this.rotation = (this.rotation + 1) % 4;
    }
    
    public void rotateCounterClockwise() {
        this.rotation = (this.rotation - 1 + 4) % 4;
    }
}
