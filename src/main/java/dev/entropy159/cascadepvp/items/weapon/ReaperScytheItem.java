package dev.entropy159.cascadepvp.items.weapon;

import dev.entropy159.cascadepvp.config.ServerConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;

public class ReaperScytheItem extends CascadeSword {
    public static HashMap<UUID, OldData> OLD_DATA = new HashMap<>();

    public ReaperScytheItem(Properties props) {
        super(props);
    }

    @Override
    public @Nullable UUID ownerUUID() {
        return UUID.fromString("4ca83233-3215-424e-81e1-ec280af064af");
    }

    @Override
    public String description() {
        return "The grim reaper reaps the dead, this weapon reaps the living.";
    }

    @Override
    public int activeAbility(ServerPlayer player, ItemStack stack, LivingEntity target) {
        var old = player.getEffect(MobEffects.DAMAGE_BOOST);
        if (old != null) {
            OLD_DATA.put(player.getUUID(), new OldData(old.getDuration(), old.getAmplifier()));
        }
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, ServerConfig.REAPER_SCYTHE_STRENGTH_DURATION.getAsInt(), 2));
        return ServerConfig.REAPER_SCYTHE_STRENGTH_COOLDOWN.get();
    }

    @Override
    public void onHitEntity(ServerPlayer player, LivingEntity target, float damage, DamageSource source) {
        super.onHitEntity(player, target, damage, source);
        float multiplied = damage * (float) ServerConfig.REAPER_SCYTHE_LIFESTEAL.getAsDouble();
        player.heal(multiplied);
    }

    public record OldData(int duration, int amplifier) {
    }
}
