package dev.entropy159.cascadepvp.effects;

import dev.entropy159.cascadepvp.CascadePVP;
import dev.entropy159.entropylib.util.InvisEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class TrueInvisibilityEffect extends MobEffect implements InvisEffect {
    public TrueInvisibilityEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x8E8A9E);
    }

    @Override
    public boolean isFull() {
        return true;
    }

    @Override
    public ResourceLocation id() {
        return CascadePVP.id("true_invis");
    }
}
