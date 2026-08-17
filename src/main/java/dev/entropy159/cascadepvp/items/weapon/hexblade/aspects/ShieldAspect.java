package dev.entropy159.cascadepvp.items.weapon.hexblade.aspects;

import dev.entropy159.cascadepvp.items.weapon.hexblade.HexbladeAspect;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class ShieldAspect extends HexbladeAspect {
    private final Config<Integer> duration;
    private final Config<Integer> resistance;
    private final Config<Integer> slowness;

    public ShieldAspect() {
        super("Shield", 0x00BBFF, 45 * 20);
        duration = addConfig("Duration", 15 * 20);
        resistance = addConfig("Resistance", 2);
        slowness = addConfig("Slowness", 4);
    }

    @Override
    public ItemLike getItem() {
        return Items.SHIELD;
    }

    @Override
    public boolean onUse(ServerPlayer player, ItemStack stack, LivingEntity target) {
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration.get(), resistance.get()));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration.get(), slowness.get()));
        return true;
    }
}
