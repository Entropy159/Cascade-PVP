package dev.entropy159.cascadepvp.effects;

import dev.entropy159.cascadepvp.config.ServerConfig;
import dev.entropy159.entropylib.util.InvisEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class TrueInvisibilityEffect extends MobEffect implements InvisEffect {
    public TrueInvisibilityEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x8E8A9E);
    }

    @Override
    public boolean isFull() {
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (!ServerConfig.SHADOW_KARAMBIT_INVIS_PARTICLES.get()) {
            for (var effect : entity.getActiveEffects()) {
                if (effect.isVisible()) {
                    entity.removeEffect(effect.getEffect());
                    entity.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), false, effect.showIcon()));
                }
            }
        }
        return super.applyEffectTick(entity, amplifier);
    }
}
