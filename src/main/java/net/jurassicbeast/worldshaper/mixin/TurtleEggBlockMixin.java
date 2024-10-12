package net.jurassicbeast.worldshaper.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(TurtleEggBlock.class)
public class TurtleEggBlockMixin {

    @Overwrite
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {

    }

    @Overwrite
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {

    }

    @Overwrite
    private void tryBreakEgg(World world, BlockState state, BlockPos pos, Entity entity, int inverseChance) {

    }
}
