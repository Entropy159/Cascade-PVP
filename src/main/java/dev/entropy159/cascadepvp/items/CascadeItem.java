package dev.entropy159.cascadepvp.items;

import dev.entropy159.cascadepvp.network.toServer.UtilityPacket;
import dev.entropy159.cascadepvp.registry.CascadeGameRules;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface CascadeItem {
    static Item.Properties defaultProps(Item.Properties props) {
        return props.fireResistant().rarity(Rarity.EPIC);
    }

    static Item.Properties sword(Item.Properties props) {
        return defaultProps(props).stacksTo(1).attributes(SwordItem.createAttributes(Tiers.NETHERITE, 3, -2.4F));
    }

    @Nullable
    UUID ownerUUID();

    String description();

    default void onUseAbility(ServerPlayer player, int entityID) {
        ItemStack stack = player.getMainHandItem();
        if (canUse(player) && !player.getCooldowns().isOnCooldown(stack.getItem())) {
            LivingEntity target = entityID == -1 ? null : (LivingEntity) player.level().getEntity(entityID);
            int cooldown = activeAbility(player, stack, target);
            if (cooldown > 0) {
                player.getCooldowns().addCooldown(stack.getItem(), cooldown);
            }
        }
    }

    default boolean isValidTarget(LivingEntity target) {
        return target.isPickable() && target.isAlive();
    }

    int activeAbility(ServerPlayer player, ItemStack stack, @Nullable LivingEntity target);

    default boolean canUse(Player player) {
        return player.getUUID().equals(ownerUUID()) || player.level().isClientSide() || !player.level().getGameRules().getBoolean(CascadeGameRules.RESTRICT_ABILITIES);
    }

    default void modifyTooltip(List<Component> tooltip, ItemStack stack, Player player, Item.TooltipContext context, TooltipFlag flag) {
        tooltip.add(Component.literal(description()).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
    }

    default void onHitEntity(ServerPlayer player, LivingEntity target, float damage, DamageSource source) {
    }

    default void utilityClient(Player player, ItemStack stack) {
        if (FMLEnvironment.dist.isClient()) {
            PacketDistributor.sendToServer(new UtilityPacket());
        }
    }

    default void utilityServer(ServerPlayer player, ItemStack stack) {

    }
}
