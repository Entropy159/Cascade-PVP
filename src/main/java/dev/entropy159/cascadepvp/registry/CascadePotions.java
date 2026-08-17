package dev.entropy159.cascadepvp.registry;

import dev.entropy159.cascadepvp.effects.InverseInvisiblityEffect;
import dev.entropy159.cascadepvp.effects.TrueInvisibilityEffect;
import dev.entropy159.cascadepvp.effects.VoidFormEffect;
import dev.entropy159.entropylib.registrate.mobeffect.MobEffectEntry;
import dev.entropy159.entropylib.registrate.potion.PotionEntry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;

import static dev.entropy159.cascadepvp.CascadePVP.REGISTRATE;

public class CascadePotions {
    public static final MobEffectEntry<TrueInvisibilityEffect> TRUE_INVISIBILITY = REGISTRATE.mobEffect("true_invisibility", TrueInvisibilityEffect::new).register();
    public static final MobEffectEntry<InverseInvisiblityEffect> INVERSE_INVISIBILITY = REGISTRATE.mobEffect("inverse_invisibility", InverseInvisiblityEffect::new).register();
    public static final MobEffectEntry<VoidFormEffect> VOID_FORM = REGISTRATE.mobEffect("void_form", VoidFormEffect::new).register();
    public static final PotionEntry<Potion> INVERSE_INVISIBILITY_POTION = REGISTRATE.potion("inverse_invisibility", () -> new MobEffectInstance[]{new MobEffectInstance(INVERSE_INVISIBILITY, 90 * 20)}).recipe(Potions.INVISIBILITY, Items.FERMENTED_SPIDER_EYE).register();
    public static final PotionEntry<Potion> PVP_POTION = REGISTRATE.potion("pvp", () -> new MobEffectInstance[]{new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 9600, 1), new MobEffectInstance(MobEffects.DAMAGE_BOOST, 9600, 1), new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 9600), new MobEffectInstance(MobEffects.REGENERATION, 1800), new MobEffectInstance(MobEffects.ABSORPTION, 600, 4)}).lang("PVP").color(0xE060D7).recipe(Items.GOLDEN_APPLE).register();

    public static void init() {
    }
}