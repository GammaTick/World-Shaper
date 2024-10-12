package net.jurassicbeast.worldshaper.mixin;

import net.jurassicbeast.worldshaper.WorldShaper;
import net.minecraft.entity.vehicle.BoatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BoatEntity.class)
public class BoatEntityMixin {

    @Inject(method = "onBubbleColumnSurfaceCollision", at = @At("HEAD"), cancellable = true)
    public void onBubbleColumnSurfaceCollision(boolean drag, CallbackInfo info) {
        if (!WorldShaper.booleanValues[2]) {
            info.cancel();
        }
    }
}
