package dev.entropy159.cascadepvp;

import dev.entropy159.cascadepvp.config.ServerConfig;
import dev.entropy159.cascadepvp.dimensions.QuantumDimension;
import dev.entropy159.cascadepvp.items.CascadeItem;
import dev.entropy159.cascadepvp.items.weapon.ReaperScytheItem;
import dev.entropy159.cascadepvp.network.toClient.WorldSeedPacket;
import dev.entropy159.cascadepvp.registry.CascadeGameRules;
import dev.entropy159.cascadepvp.registry.CascadePotions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityMobGriefingEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

@EventBusSubscriber(modid = CascadePVP.MODID)
public class CascadeEvents {
    @SubscribeEvent
    public static void postDamage(LivingDamageEvent.Post event) {
        ItemStack weapon = event.getSource().getWeaponItem();
        LivingEntity target = event.getEntity();
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            if (player.hasEffect(CascadePotions.TRUE_INVISIBILITY) && target instanceof Player) {
                player.removeEffect(CascadePotions.TRUE_INVISIBILITY);
            }
            if (weapon != null && weapon.getItem() instanceof CascadeItem item) {
                if (item.canUse(player)) {
                    item.onHitEntity(player, target, event.getNewDamage(), event.getSource());
                }
            }
        }
    }

    @SubscribeEvent
    public static void entityTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            entity.setGlowingTag(entity.getMainHandItem().getItem() instanceof CascadeItem);
        }
        if (event.getEntity() instanceof ServerPlayer player) {
            Optional.ofNullable(player.getAttribute(Attributes.MAX_HEALTH)).ifPresent(attr -> attr.setBaseValue(ServerConfig.MAX_HEALTH.get()));
        }
    }

    @SubscribeEvent
    public static void effectExpire(MobEffectEvent.Expired event) {
        if (event.getEntity() instanceof Player player && ReaperScytheItem.OLD_DATA.containsKey(player.getUUID())) {
            var old = ReaperScytheItem.OLD_DATA.get(player.getUUID());
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, old.duration(), old.amplifier()));
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (player.level().dimension().equals(QuantumDimension.QUANTUM) && player.serverLevel().getGameRules().getBoolean(CascadeGameRules.QUANTUM_DEATH_PROTECTION)) {
                event.setCanceled(true);
                QuantumDimension.teleportFrom(player);
                player.setHealth(4);
            }
        }
    }

    @SubscribeEvent
    public static void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new WorldSeedPacket(player.serverLevel().getSeed()));
        }
    }

    @SubscribeEvent
    public static void tooltips(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof CascadeItem item) {
            item.modifyTooltip(event.getToolTip(), event.getItemStack(), event.getEntity(), event.getContext(), event.getFlags());
        }
    }

    @SubscribeEvent
    public static void grief(EntityMobGriefingEvent event) {
        if (event.getEntity() instanceof EnderMan && ServerConfig.LESS_MOB_GRIEFING.get()) {
            event.setCanGrief(false);
        }
    }
}
