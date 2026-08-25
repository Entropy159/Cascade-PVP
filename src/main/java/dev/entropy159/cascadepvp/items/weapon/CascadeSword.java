package dev.entropy159.cascadepvp.items.weapon;

import dev.entropy159.cascadepvp.items.CascadeItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class CascadeSword extends SwordItem implements CascadeItem {
    public CascadeSword(Properties props) {
        super(Tiers.NETHERITE, CascadeItem.sword(props));
    }

    public CascadeSword(Properties props, int damage, float speed) {
        super(Tiers.NETHERITE, CascadeItem.sword(props).attributes(SwordItem.createAttributes(Tiers.NETHERITE, damage, speed)));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (entity instanceof LivingEntity living) {
            living.setGlowingTag(living.getMainHandItem().equals(stack));
        }
    }
}
