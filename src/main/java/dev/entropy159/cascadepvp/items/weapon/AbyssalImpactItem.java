package dev.entropy159.cascadepvp.items.weapon;

import dev.entropy159.cascadepvp.config.ServerConfig;
import dev.entropy159.cascadepvp.items.CascadeItem;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static net.minecraft.world.item.MaceItem.canSmashAttack;
import static net.minecraft.world.item.MaceItem.knockback;

public class AbyssalImpactItem extends AxeItem implements CascadeItem {
    public AbyssalImpactItem(Properties props) {
        super(Tiers.NETHERITE, CascadeItem.defaultProps(props).stacksTo(1).attributes(AxeItem.createAttributes(Tiers.NETHERITE, 5, -3)));
    }

    public int activeAbility(ServerPlayer player, ItemStack stack, LivingEntity target) {
        if (player instanceof ServerPlayer) {
            double power = ServerConfig.ABYSSAL_IMPACT_VELOCITY.get();
            var look = player.getLookAngle().normalize();
            double angle = ServerConfig.ABYSSAL_IMPACT_ANGLE.get();
            double forward = Math.cos(Math.toRadians(angle));
            double up = Math.sin(Math.toRadians(angle));
            var flatLook = new Vec3(look.x, 0, look.z).normalize().scale(forward * power);
            var velocity = new Vec3(flatLook.x, up * power, flatLook.z);
            player.push(velocity);
            player.connection.send(new ClientboundSetEntityMotionPacket(player));
            player.setIgnoreFallDamageFromCurrentImpulse(true);
            return ServerConfig.ABYSSAL_IMPACT_COOLDOWN.get();
        }
        return 0;
    }

    @Override
    public @Nullable UUID ownerUUID() {
        return UUID.fromString("4c24ae61-8516-456c-bf5d-9d3383e62d38");
    }

    @Override
    public String description() {
        return "Maxe? Ace? What do we call this again?";
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        if (attacker instanceof ServerPlayer serverplayer && canSmashAttack(serverplayer)) {
            ServerLevel serverlevel = (ServerLevel) attacker.level();
            if (serverplayer.isIgnoringFallDamageFromCurrentImpulse() && serverplayer.currentImpulseImpactPos != null) {
                if (serverplayer.currentImpulseImpactPos.y > serverplayer.position().y) {
                    serverplayer.currentImpulseImpactPos = serverplayer.position();
                }
            } else {
                serverplayer.currentImpulseImpactPos = serverplayer.position();
            }

            serverplayer.setIgnoreFallDamageFromCurrentImpulse(true);
            serverplayer.setDeltaMovement(serverplayer.getDeltaMovement().with(Direction.Axis.Y, 0.01F));
            serverplayer.connection.send(new ClientboundSetEntityMotionPacket(serverplayer));
            if (target.onGround()) {
                serverplayer.setSpawnExtraParticlesOnFall(true);
                SoundEvent soundevent = serverplayer.fallDistance > 5.0F ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND;
                serverlevel.playSound(
                        null, serverplayer.getX(), serverplayer.getY(), serverplayer.getZ(), soundevent, serverplayer.getSoundSource(), 1.0F, 1.0F
                );
            } else {
                serverlevel.playSound(
                        null, serverplayer.getX(), serverplayer.getY(), serverplayer.getZ(), SoundEvents.MACE_SMASH_AIR, serverplayer.getSoundSource(), 1.0F, 1.0F
                );
            }

            knockback(serverlevel, serverplayer, target);
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void postHurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        super.postHurtEnemy(stack, target, attacker);
        if (canSmashAttack(attacker)) {
            attacker.resetFallDistance();
        }
    }

    @Override
    public float getAttackDamageBonus(@NotNull Entity target, float damage, DamageSource damageSource) {
        if (damageSource.getDirectEntity() instanceof LivingEntity livingentity) {
            if (!canSmashAttack(livingentity)) {
                return 0.0F;
            } else {
                float f3 = 3.0F;
                float f = 8.0F;
                float f1 = livingentity.fallDistance;
                float f2;
                if (f1 <= 3.0F) {
                    f2 = 4.0F * f1;
                } else if (f1 <= 8.0F) {
                    f2 = 12.0F + 2.0F * (f1 - 3.0F);
                } else {
                    f2 = 22.0F + f1 - 8.0F;
                }

                return livingentity.level() instanceof ServerLevel serverlevel
                        ? f2 + EnchantmentHelper.modifyFallBasedDamage(serverlevel, livingentity.getWeaponItem(), target, damageSource, 0.0F) * f1
                        : f2;
            }
        } else {
            return super.getAttackDamageBonus(target, damage, damageSource);
        }
    }

    @Override
    public boolean supportsEnchantment(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
        return super.supportsEnchantment(stack, enchantment) || enchantment.is(Enchantments.WIND_BURST);
    }
}
