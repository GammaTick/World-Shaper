package net.jurassicbeast.worldshaper.mixin;

import net.jurassicbeast.worldshaper.WorldShaper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void baseTick(CallbackInfo info) {
        Entity livingEntity = (Entity) (Object) this;

        if (!(livingEntity instanceof PlayerEntity)) {
            info.cancel();
        }
    }
}
