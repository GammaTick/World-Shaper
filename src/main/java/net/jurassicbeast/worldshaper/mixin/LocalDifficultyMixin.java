package net.jurassicbeast.worldshaper.mixin;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalDifficulty.class)
public class LocalDifficultyMixin {
    private boolean isLocalDifficultySet = false;
    private float setValue;
    private float addValue;
    private float subtractValue;

    @Inject(method = "setLocalDifficulty", at = @At("HEAD"), cancellable = true)
    private void setLocalDifficulty(Difficulty difficulty, long timeOfDay, long inhabitedTime, float moonSize, CallbackInfoReturnable<Float> infoReturnable) {
        if (this.isLocalDifficultySet) {
            infoReturnable.setReturnValue(this.setValue);
            infoReturnable.cancel();
        }

        if (difficulty == Difficulty.PEACEFUL) {
            infoReturnable.setReturnValue(0.0F);
        } else {
            boolean bl = difficulty == Difficulty.HARD;
            float f = 0.75F;
            float g = MathHelper.clamp(((float)timeOfDay + -72000.0F) / 1440000.0F, 0.0F, 1.0F) * 0.25F;
            f += g;
            float h = 0.0F;
            h += MathHelper.clamp((float)inhabitedTime / 3600000.0F, 0.0F, 1.0F) * (bl ? 1.0F : 0.75F);
            h += MathHelper.clamp(moonSize * 0.25F, 0.0F, g);
            if (difficulty == Difficulty.EASY) {
                h *= 0.5F;
            }

            f += h;
            infoReturnable.setReturnValue(((float)difficulty.getId() * f) + addValue - subtractValue);
        }

        infoReturnable.cancel();
    }

    public void setLocalDifficulty(float amount) {
        this.isLocalDifficultySet = true;
        this.setValue = amount;
    }

    public void addLocalDifficulty(float amount) {
        this.addValue = amount;
    }

    public void subtractLocalDifficulty(float amount) {
        this.subtractValue = amount;
    }

    public void resetLocalDifficulty() {
        this.isLocalDifficultySet = false;
        this.setValue = 0;
        this.addValue = 0;
        this.subtractValue = 0;
    }
}
