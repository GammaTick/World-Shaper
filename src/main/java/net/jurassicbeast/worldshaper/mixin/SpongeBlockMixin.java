package net.jurassicbeast.worldshaper.mixin;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static net.minecraft.block.Block.dropStacks;
import static net.minecraft.world.RedstoneView.DIRECTIONS;

@Mixin(SpongeBlock.class)
public class SpongeBlockMixin {
    
    @Overwrite
    private boolean absorbWater(World world, BlockPos pos) {
        return BlockPos.iterateRecursively(pos, 64, 10_000, (currentPos, queuer) -> {
            for (Direction direction : DIRECTIONS) {
                queuer.accept(currentPos.offset(direction));
            }
        }, currentPos -> {
            if (currentPos.equals(pos)) {
                return true;
            } else {
                BlockState blockState = world.getBlockState(currentPos);
                FluidState fluidState = world.getFluidState(currentPos);
                if (!fluidState.isIn(FluidTags.WATER)) {
                    return false;
                } else {
                    if (blockState.getBlock() instanceof FluidDrainable fluidDrainable && !fluidDrainable.tryDrainFluid(null, world, currentPos, blockState).isEmpty()) {
                        return true;
                    }

                    if (blockState.getBlock() instanceof FluidBlock) {
                        world.setBlockState(currentPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                    } else {
                        if (!blockState.isOf(Blocks.KELP) && !blockState.isOf(Blocks.KELP_PLANT) && !blockState.isOf(Blocks.SEAGRASS) && !blockState.isOf(Blocks.TALL_SEAGRASS)) {
                            return false;
                        }

                        BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(currentPos) : null;
                        dropStacks(blockState, world, currentPos, blockEntity);
                        world.setBlockState(currentPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                    }

                    return true;
                }
            }
        }) > 1;
    }
}
