package dev.entropy159.cascadepvp.items.weapon;

import dev.entropy159.cascadepvp.items.CascadeItem;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BoombowItem extends BowItem implements CascadeItem {
    public BoombowItem(Properties props) {
        super(CascadeItem.defaultProps(props).durability(768));
    }

    @Override
    public @Nullable UUID ownerUUID() {
        return UUID.fromString("90332432-c94b-4799-b801-192be17906c6");
    }

    @Override
    public String description() {
        return "Ever thought TNT wasn't fun enough?";
    }

    @Override
    public int activeAbility(ServerPlayer player, ItemStack stack, LivingEntity target) {
        return 0;
    }

    @Override
    public @NotNull AbstractArrow customArrow(@NotNull AbstractArrow arrow, @NotNull ItemStack projectileStack, @NotNull ItemStack weaponStack) {
        if (arrow.getOwner() instanceof Player player && canUse(player)) {
            arrow.addTag("Explosive");
        }
        return super.customArrow(arrow, projectileStack, weaponStack);
    }

    @Override
    public boolean supportsEnchantment(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
        return super.supportsEnchantment(stack, enchantment) || enchantment.is(Enchantments.KNOCKBACK);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!stack.has(DataComponents.ENCHANTMENTS) || stack.get(DataComponents.ENCHANTMENTS).isEmpty()) {
            var enchants = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
            enchants.set(level.registryAccess().holderOrThrow(Enchantments.KNOCKBACK), 3);
            stack.set(DataComponents.ENCHANTMENTS, enchants.toImmutable());
        }
        if (entity instanceof LivingEntity living) {
            living.setGlowingTag(living.getMainHandItem().equals(stack));
        }
    }
}
