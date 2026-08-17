package dev.entropy159.cascadepvp.mixin;

import dev.entropy159.cascadepvp.items.weapon.BowOfTheGaladhrim;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @Redirect(method = "useItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemCooldowns;isOnCooldown(Lnet/minecraft/world/item/Item;)Z"))
    private boolean excludeBow(ItemCooldowns instance, Item item) {
        if (item instanceof BowOfTheGaladhrim) {
            return false;
        }
        return instance.isOnCooldown(item);
    }
}
