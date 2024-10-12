package net.jurassicbeast.worldshaper.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void baseTick(CallbackInfo info) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;

        if (!(livingEntity instanceof PlayerEntity)) {
            info.cancel();
        }
    }

    //@Inject(method = "getMaxRelativeHeadRotation", at = @At("HEAD"), cancellable = true)
    private void getMaxRelativeHeadRotation(CallbackInfoReturnable<Float> info) {
        info.setReturnValue(250.0F);
    }

    @Inject(method = "isInCreativeMode", at = @At("HEAD"), cancellable = true)
    public void isInCreativeMode(CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(true);
    }
}
