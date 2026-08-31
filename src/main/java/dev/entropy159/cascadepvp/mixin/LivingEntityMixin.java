package dev.entropy159.cascadepvp.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.entropy159.cascadepvp.items.weapon.ExecutionersBladeItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nonnull;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    @Nonnull
    public abstract ItemStack getWeaponItem();

    @Shadow
    public abstract ItemStack getMainHandItem();

    @ModifyReturnValue(method = "canDisableShield", at = @At("RETURN"))
    private boolean moreAxes(boolean original) {
        return original || getWeaponItem().getItem() instanceof ExecutionersBladeItem item && (Object) this instanceof Player player && item.canUse(player);
    }

    @ModifyReturnValue(method = "canBeAffected", at = @At("RETURN"))
    private boolean protectOwner(boolean original, @Local(argsOnly = true) MobEffectInstance effect) {
        if ((Object) this instanceof Player player) {
            for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
                ItemStack stack = player.getInventory().getItem(slot);
                if (stack.getItem() instanceof ExecutionersBladeItem item && item.canUse(player)) {
                    if (ExecutionersBladeItem.isUsing(stack) && effect.is(MobEffects.WITHER)) {
                        return false;
                    }
                }
            }
        }
        return original;
    }
}
