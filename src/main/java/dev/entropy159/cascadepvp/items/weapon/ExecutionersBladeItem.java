package dev.entropy159.cascadepvp.items.weapon;

import dev.entropy159.cascadepvp.CascadePVP;
import dev.entropy159.cascadepvp.config.ServerConfig;
import dev.entropy159.cascadepvp.registry.CascadeDataComponents;
import dev.entropy159.entropylib.util.EventScheduler;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public class ExecutionersBladeItem extends CascadeSword {
    public ExecutionersBladeItem(Properties props) {
        super(props, SwordItem.createAttributes(Tiers.NETHERITE, 3, -2.4f).withModifierAdded(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(CascadePVP.id("extended_reach"), 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND));
    }

    @Override
    public int activeAbility(ServerPlayer player, ItemStack stack, LivingEntity target) {
        Vec3 pos = player.position();
        var cloud = new AreaEffectCloud(player.level(), pos.x, pos.y, pos.z);
        cloud.setOwner(player);
        cloud.addEffect(new MobEffectInstance(MobEffects.WITHER, ServerConfig.EXECUTIONERS_BLADE_EFFECT_DURATION.get(), ServerConfig.EXECUTIONERS_BLADE_POWER.get()));
        cloud.setRadius(ServerConfig.EXECUTIONERS_BLADE_RADIUS.get().floatValue());
        cloud.setDuration(ServerConfig.EXECUTIONERS_BLADE_AOE_DURATION.get());
        player.level().addFreshEntity(cloud);
        setUsing(stack, true);
        EventScheduler.scheduleUntil(1, () -> !cloud.isAlive() || !player.isAlive(), () -> {
            cloud.setPos(player.position());
            player.serverLevel().players().forEach(p -> p.connection.send(new ClientboundTeleportEntityPacket(cloud)));
        });
        EventScheduler.schedule(1, () -> !cloud.isAlive() || !player.isAlive(), () -> setUsing(stack, false));
        return ServerConfig.EXECUTIONERS_BLADE_COOLDOWN.get();
    }

    public static boolean isUsing(ItemStack stack) {
        return stack.getOrDefault(CascadeDataComponents.USING_ABILITY, false);
    }

    public static void setUsing(ItemStack stack, boolean using) {
        stack.set(CascadeDataComponents.USING_ABILITY, using);
    }

    @Override
    public @Nullable UUID ownerUUID() {
        return UUID.fromString("eb51016b-3e52-48b0-a1a5-da8f53e44de0");
    }

    @Override
    public String description() {
        return "Your time has come... Prepare to meet your maker.";
    }
}
