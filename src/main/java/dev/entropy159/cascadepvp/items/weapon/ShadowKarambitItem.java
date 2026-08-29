package dev.entropy159.cascadepvp.items.weapon;

import dev.entropy159.cascadepvp.config.ServerConfig;
import dev.entropy159.cascadepvp.registry.CascadePotions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.UUID;

public class ShadowKarambitItem extends CascadeSword {
    public ShadowKarambitItem(Properties props) {
        super(props);
    }

    @Override
    public @Nullable UUID ownerUUID() {
        return UUID.fromString("2463d7b2-3220-452c-a65b-6fa26ab94d2a");
    }

    @Override
    public String description() {
        return "Left behind by an unknown criminal in the world's greatest heist.";
    }

    @Override
    public int activeAbility(ServerPlayer player, ItemStack stack, LivingEntity target) {
        player.addEffect(new MobEffectInstance(CascadePotions.TRUE_INVISIBILITY, ServerConfig.SHADOW_KARAMBIT_INVIS_DURATION.getAsInt(), 0, false, !ServerConfig.SHADOW_KARAMBIT_INVIS_PARTICLES.getAsBoolean(), true));
        return ServerConfig.SHADOW_KARAMBIT_INVIS_COOLDOWN.getAsInt();
    }

    @Override
    public void onHitEntity(ServerPlayer player, LivingEntity target, float damage, DamageSource source) {
        super.onHitEntity(player, target, damage, source);
        double chance = ServerConfig.SHADOW_KARAMBIT_WITHER_CHANCE.get();
        int duration = ServerConfig.SHADOW_KARAMBIT_WITHER_DURATION.get();
        if (new Random().nextDouble() < chance) {
            int amplifier = ServerConfig.SHADOW_KARAMBIT_WITHER_LEVEL.get() - 1;
            if (amplifier >= 0) {
                target.addEffect(new MobEffectInstance(MobEffects.WITHER, duration, amplifier));
            }
        }
    }

    @Override
    public void utilityServer(ServerPlayer player, ItemStack stack) {
        super.utilityServer(player, stack);
        if (player.hasEffect(CascadePotions.TRUE_INVISIBILITY)) {
            player.removeEffect(CascadePotions.TRUE_INVISIBILITY);
        }
    }
}
