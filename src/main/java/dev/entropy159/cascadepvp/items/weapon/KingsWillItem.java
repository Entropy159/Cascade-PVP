package dev.entropy159.cascadepvp.items.weapon;

import dev.entropy159.cascadepvp.config.ServerConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

public class KingsWillItem extends CascadeSword {
    public KingsWillItem(Properties props) {
        super(props, 5, -2.4F);
    }

    @Override
    public int activeAbility(ServerPlayer player, ItemStack stack, LivingEntity target) {
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200));
        player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 20));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 1200));
        return ServerConfig.KINGS_WILL_COOLDOWN.get();
    }

    @Override
    public @Nullable UUID ownerUUID() {
        return UUID.fromString("8d8c0b02-2551-4887-bf60-04a7690703da");
    }

    @Override
    public String description() {
        return "The king's word is law.";
    }
}
