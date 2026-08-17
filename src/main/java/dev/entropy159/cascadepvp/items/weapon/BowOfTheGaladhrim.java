package dev.entropy159.cascadepvp.items.weapon;

import dev.entropy159.cascadepvp.config.ServerConfig;
import dev.entropy159.cascadepvp.items.CascadeItem;
import dev.entropy159.cascadepvp.registry.CascadeDataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class BowOfTheGaladhrim extends BowItem implements CascadeItem {
    public BowOfTheGaladhrim(Properties props) {
        super(CascadeItem.defaultProps(props).durability(768));
    }

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ItemAbility ability) {
        return super.canPerformAction(stack, ability) || ability.equals(ItemAbilities.SPYGLASS_SCOPE);
    }

    @Override
    public @Nullable UUID ownerUUID() {
        return UUID.fromString("ea7cae2b-b354-4c06-9f40-e80c1b1d1670");
    }

    @Override
    public String description() {
        return "Basically a cheat code for archery.";
    }

    @Override
    public int activeAbility(ServerPlayer player, ItemStack stack, LivingEntity target) {
        stack.set(CascadeDataComponents.SUPERCHARGED, true);
        player.level().playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS);
        return ServerConfig.BOW_OF_THE_GALADHRIM_COOLDOWN.get();
    }

    @Override
    protected void shoot(@NotNull ServerLevel level, @NotNull LivingEntity shooter, @NotNull InteractionHand hand, @NotNull ItemStack weapon, @NotNull List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, @Nullable LivingEntity target) {
        if (shooter instanceof Player player && canUse(player)) {
            velocity *= ServerConfig.BOW_OF_THE_GALADHRIM_VELOCITY.get();
            float accuracy = ServerConfig.BOW_OF_THE_GALADHRIM_ACCURACY.get();
            inaccuracy = (accuracy == 0 ? 0 : inaccuracy / accuracy);
        }
        super.shoot(level, shooter, hand, weapon, projectileItems, velocity, inaccuracy, isCrit, target);
    }

    @Override
    public @NotNull AbstractArrow customArrow(AbstractArrow arrow, @NotNull ItemStack projectileStack, @NotNull ItemStack weaponStack) {
        if (arrow.getOwner() instanceof Player player && canUse(player)) {
            arrow.setNoGravity(true);
            if (weaponStack.getOrDefault(CascadeDataComponents.SUPERCHARGED, false)) {
                weaponStack.remove(CascadeDataComponents.SUPERCHARGED);
                arrow.setBaseDamage(arrow.getBaseDamage() * ServerConfig.BOW_OF_THE_GALADHRIM_MULT.get());
                arrow.level().playSound(null, arrow.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS);
            }
        }
        return super.customArrow(arrow, projectileStack, weaponStack);
    }
}
