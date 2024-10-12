package net.jurassicbeast.worldshaper.mixin;

import net.jurassicbeast.worldshaper.WorldShaper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(IronGolemEntity.class)
public class IronGolemEntityMixin {
    @Shadow
    private int attackTicksLeft;
    @Shadow
    private float getAttackDamage() {
        return 1.0f;
    }

    @Overwrite
    public boolean tryAttack(Entity target) {
        IronGolemEntity ironGolemEntity = (IronGolemEntity) (Object) this;

        this.attackTicksLeft = 10;
        ironGolemEntity.getWorld().sendEntityStatus(ironGolemEntity, EntityStatuses.PLAY_ATTACK_SOUND);
        float f = this.getAttackDamage();
        float g = (int)f > 0 ? f / 2.0F + (float)ironGolemEntity.getRandom().nextInt((int)f) : f;
        DamageSource damageSource = ironGolemEntity.getDamageSources().mobAttack(ironGolemEntity);
        boolean bl = target.damage(damageSource, g);
        if (bl) {
            double d = target instanceof LivingEntity livingEntity ? livingEntity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE) : 0.0;
            double e = Math.max(0.0, 1.0 - d);
            target.setVelocity(target.getVelocity().add(0.0, 0.4F * e, 0.0));
            if (ironGolemEntity.getWorld() instanceof ServerWorld serverWorld) {
                EnchantmentHelper.onTargetDamaged(serverWorld, target, damageSource);
            }
        }

        ironGolemEntity.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return bl;
    }
}
