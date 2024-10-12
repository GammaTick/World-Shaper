package net.jurassicbeast.worldshaper.mixin;

import net.jurassicbeast.worldshaper.WorldShaper;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.village.raid.Raid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Raid.class)
public class RaidMixin {
    @Shadow
    private static Text EVENT_TEXT;
    @Shadow
    private ServerBossBar bar;
    @Shadow
    private int wavesSpawned;
    @Shadow
    private int waveCount;
    @Shadow
    private ServerWorld world;
    @Shadow
    private int badOmenLevel;

    @Inject(method = "tick",
            at = {
                    @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/ServerBossBar;setName(Lnet/minecraft/text/Text;)V", shift = At.Shift.AFTER, ordinal = 1),
                    @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/ServerBossBar;setName(Lnet/minecraft/text/Text;)V", shift = At.Shift.AFTER, ordinal = 2)
            },
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void injectCustomCode(CallbackInfo ci, int i) {
        MutableText raidBarText = EVENT_TEXT.copy();

        if (WorldShaper.booleanValues[0]) {
            raidBarText.append(" - ").append(Text.translatable("event.minecraft.raid.raiders_remaining", i));
        }

        if (WorldShaper.booleanValues[1]) {
            raidBarText.append(" - ").append(Text.literal(String.format("Wave: %d / %d", this.wavesSpawned, (this.waveCount
                    + (this.badOmenLevel > 1 ? this.badOmenLevel - 1 : 0)))));
        }

        this.bar.setName(raidBarText);
    }
}
