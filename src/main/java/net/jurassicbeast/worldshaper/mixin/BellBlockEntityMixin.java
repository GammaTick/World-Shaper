package net.jurassicbeast.worldshaper.mixin;

import net.jurassicbeast.worldshaper.WorldShaper;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BellBlockEntity.class)
public class BellBlockEntityMixin extends BlockEntity {
    @Shadow
    private long lastRingTime;
    @Shadow
    private List<LivingEntity> hearingEntities;

    public BellBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "notifyMemoriesOfBell", at = @At("TAIL"), cancellable = true)
    private void notifyMemoriesOfBell(CallbackInfo info) {
        BlockPos blockPos = this.getPos();
        if (this.world.getTime() > this.lastRingTime + 60L || this.hearingEntities == null) {
            this.lastRingTime = this.world.getTime();
            Box box = new Box(blockPos).expand(48.0);
            this.hearingEntities = this.world.getNonSpectatingEntities(LivingEntity.class, box);
        }

        if (!this.world.isClient) {
            for (LivingEntity livingEntity : this.hearingEntities) {
                if (livingEntity.isAlive() && !livingEntity.isRemoved() && blockPos.isWithinDistance(livingEntity.getPos(), 32.0)) {
                    livingEntity.getBrain().remember(MemoryModuleType.HEARD_BELL_TIME, this.world.getTime());
                }
            }
        }

        info.cancel();
    }

    @Inject(method = "raidersHearBell", at = @At("TAIL"), cancellable = true)
    private static boolean raidersHearBell(BlockPos pos, List<LivingEntity> hearingEntities, CallbackInfoReturnable<Boolean> info) {
        for (LivingEntity livingEntity : hearingEntities) {
            if (livingEntity.isAlive()
                    && !livingEntity.isRemoved()
                    && pos.isWithinDistance(livingEntity.getPos(), 32.0)
                    && livingEntity.getType().isIn(EntityTypeTags.RAIDERS)) {
                return true;
            }
        }

        return false;
    }
}
