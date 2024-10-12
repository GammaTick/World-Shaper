package net.jurassicbeast.worldshaper.mixin;

import net.jurassicbeast.worldshaper.WorldShaper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends MobEntity {

    protected ZombieEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    private boolean canBreakDoors;
    @Shadow protected abstract void setCanBreakDoors(boolean canBreakDoors);
    @Shadow public abstract boolean canBreakDoors();

//    @Inject(method = "initGoals", at = @At("TAIL"))
//    private void adjustGoals(CallbackInfo ci) {
//        boolean canBreakTurtleEggs = this.getWorld().getGameRules().getBoolean(WorldShaper.CAN_ZOMBIE_BREAK_TURTLE_EGGS);
//        boolean canBreakDoorsRule = this.getWorld().getGameRules().getBoolean(WorldShaper.CAN_ZOMBIE_BREAK_DOORS);
//
//        if (!canBreakTurtleEggs) {
//            this.goalSelector.getGoals().removeIf(prioritizedGoal ->
//                    prioritizedGoal.getGoal().getClass().getSimpleName().equals("DestroyEggGoal"));
//        }
//
//        // Store the zombie's original door-breaking capability
//        boolean originalCanBreakDoors = this.canBreakDoors();
//
//        // Set the capability based on both the gamerule and the zombie's original capability
//        this.setCanBreakDoors(canBreakDoorsRule && originalCanBreakDoors);
//    }
}
