package net.jurassicbeast.worldshaper.arguments.entityarguments;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class PathfindingData {
    public final BlockPos targetPos;
    public final Entity targetEntity;
    public final float proximityDistance;
    public final float mobSpeed;

    PathfindingData(BlockPos targetPos, float proximityDistance, float mobSpeed, Entity targetEntity) {
        this.targetPos = targetPos;
        this.targetEntity = targetEntity;
        this.proximityDistance = proximityDistance;
        this.mobSpeed = mobSpeed;
    }
}
