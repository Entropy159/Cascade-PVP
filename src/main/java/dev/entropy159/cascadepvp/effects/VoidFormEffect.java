package dev.entropy159.cascadepvp.effects;

import dev.entropy159.cascadepvp.CascadePVP;
import dev.entropy159.cascadepvp.items.weapon.hexblade.aspects.VoidFormAspect;
import dev.entropy159.cascadepvp.registry.CascadePotions;
import dev.entropy159.entropylib.util.InvisEffect;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class VoidFormEffect extends MobEffect implements InvisEffect {
    public VoidFormEffect() {
        super(MobEffectCategory.BENEFICIAL, VoidFormAspect.COLOR);
        addAttributeModifier(NeoForgeMod.CREATIVE_FLIGHT, CascadePVP.id("flight"), 1, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        Vec3 pos = entity.getEyePosition();
        Vec3 dir = entity.getDeltaMovement().scale(5).add(randomVec().scale(5));
        for (int i = 0; i < 10; i++) {
            double rand = new Random().nextDouble();
            double y = entity.getY() + (rand * entity.getBbHeight());
            entity.level().addParticle(new DustParticleOptions(Vec3.fromRGB24(getColor()).toVector3f(), new Random().nextFloat(0.5f, 2.5f)), pos.x, y, pos.z, dir.x, dir.y, dir.z);
        }
        return super.applyEffectTick(entity, amplifier);
    }

    private Vec3 randomVec() {
        Random rand = new Random();
        double x = (rand.nextDouble() * 2) - 1;
        double y = (rand.nextDouble() * 2) - 1;
        double z = (rand.nextDouble() * 2) - 1;
        return new Vec3(x, y, z);
    }

    @Override
    public void onMobHurt(@NotNull LivingEntity entity, int amplifier, @NotNull DamageSource source, float amount) {
        super.onMobHurt(entity, amplifier, source, amount);
        entity.removeEffect(CascadePotions.VOID_FORM);
        entity.hurt(entity.damageSources().outOfBorder(), amount * 2);
    }

    @Override
    public boolean isFull() {
        return true;
    }

    @Override
    public ResourceLocation id() {
        return CascadePVP.id("void_form");
    }
}
