package net.jurassicbeast.worldshaper.mixin;

import net.jurassicbeast.worldshaper.WorldShaper;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WaterFluid.class)
public class WaterFluidMixin {

    @Inject(method = "getLevelDecreasePerBlock", at = @At("HEAD"), cancellable = true)
    public void getLevelDecreasePerBlock(WorldView world, CallbackInfoReturnable<Integer> info) {
        info.setReturnValue(WorldShaper.booleanValues[3] ? 0 : 1);
    }
}
