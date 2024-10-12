package net.jurassicbeast.worldshaper.mixin;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(PistonHandler.class)
public abstract class PistonHandlerMixin {
    @Final
    @Shadow
    private World world;
    @Shadow
    @Final
    private BlockPos posFrom;
    @Shadow
    private List<BlockPos> movedBlocks = Lists.newArrayList();
    @Shadow
    @Final
    private Direction motionDirection;
    @Shadow
    private List<BlockPos> brokenBlocks = Lists.newArrayList();


    /**
     * Attempts to move a block at the given position in the specified direction.
     *
     * @param pos the position of the block to move
     * @param dir the direction in which to move the block
     * @return true if the block can be moved, false otherwise
     * @author JurassicBeast
     * @reason to modify the max limit of pushable blocks by piston
     */
    @Overwrite
    private boolean tryMove(BlockPos pos, Direction dir) {
        int k;

        BlockState blockState = this.world.getBlockState(pos);
        if (blockState.isAir()) {
            return true;
        }

        if (!PistonBlock.isMovable(blockState, this.world, pos, this.motionDirection, false, dir)) {
            return true;
        }

        if (pos.equals(this.posFrom)) {
            return true;
        }

        if (this.movedBlocks.contains(pos)) {
            return true;
        }

        int i = 1;

        if (i + this.movedBlocks.size() > 12) {
            return false;
        }

        while (isBlockSticky(blockState)) {
            BlockPos blockPos = pos.offset(this.motionDirection.getOpposite(), i);
            BlockState blockState2 = blockState;
            blockState = this.world.getBlockState(blockPos);
            if (blockState.isAir() || !isAdjacentBlockStuck(blockState2, blockState) || !PistonBlock.isMovable(blockState, this.world, blockPos, this.motionDirection, false, this.motionDirection.getOpposite()) || blockPos.equals(this.posFrom)) break;
            if (++i + this.movedBlocks.size() <= 12) continue;
            return false;
        }

        int j = 0;

        for (k = i - 1; k >= 0; --k) {
            this.movedBlocks.add(pos.offset(this.motionDirection.getOpposite(), k));
            ++j;
        }

        k = 1;

        while (true) {
            BlockPos blockPos2;
            int l;

            if ((l = this.movedBlocks.indexOf(blockPos2 = pos.offset(this.motionDirection, k))) > -1) {
                this.setMovedBlocks(j, l);

                for (int m = 0; m <= l + j; ++m) {
                    BlockPos blockPos3 = this.movedBlocks.get(m);
                    if (!isBlockSticky(this.world.getBlockState(blockPos3)) || this.tryMoveAdjacentBlock(blockPos3)) continue;
                    return false;
                }

                return true;
            }

            blockState = this.world.getBlockState(blockPos2);

            if (blockState.isAir()) {
                return true;
            }

            if (!PistonBlock.isMovable(blockState, this.world, blockPos2, this.motionDirection, true, this.motionDirection) || blockPos2.equals(this.posFrom)) {
                return false;
            }

            if (blockState.getPistonBehavior() == PistonBehavior.DESTROY) {
                this.brokenBlocks.add(blockPos2);
                return true;
            }

            if (this.movedBlocks.size() >= 12) {
                return false;
            }

            this.movedBlocks.add(blockPos2);

            ++j;
            ++k;
        }
    }

    @Shadow
    private static boolean isBlockSticky(BlockState state) {
        return state.isOf(Blocks.SLIME_BLOCK) || state.isOf(Blocks.HONEY_BLOCK);
    }

    @Shadow
    private static boolean isAdjacentBlockStuck(BlockState state, BlockState adjacentState) {
        if (state.isOf(Blocks.HONEY_BLOCK) && adjacentState.isOf(Blocks.SLIME_BLOCK)) {
            return false;
        }
        if (state.isOf(Blocks.SLIME_BLOCK) && adjacentState.isOf(Blocks.HONEY_BLOCK)) {
            return false;
        }
        return isBlockSticky(state) || isBlockSticky(adjacentState);
    }

    @Shadow
    private void setMovedBlocks(int from, int to) {
        ArrayList<BlockPos> list = Lists.newArrayList();
        ArrayList<BlockPos> list2 = Lists.newArrayList();
        ArrayList<BlockPos> list3 = Lists.newArrayList();
        list.addAll(this.movedBlocks.subList(0, to));
        list2.addAll(this.movedBlocks.subList(this.movedBlocks.size() - from, this.movedBlocks.size()));
        list3.addAll(this.movedBlocks.subList(to, this.movedBlocks.size() - from));
        this.movedBlocks.clear();
        this.movedBlocks.addAll(list);
        this.movedBlocks.addAll(list2);
        this.movedBlocks.addAll(list3);
    }

    @Shadow
    private boolean tryMoveAdjacentBlock(BlockPos pos) {
        BlockState blockState = this.world.getBlockState(pos);
        for (Direction direction : Direction.values()) {
            BlockPos blockPos;
            if (direction.getAxis() == this.motionDirection.getAxis() || !isAdjacentBlockStuck(this.world.getBlockState(blockPos = pos.offset(direction)), blockState) || this.tryMove(blockPos, direction)) continue;
            return false;
        }
        return true;
    }
}