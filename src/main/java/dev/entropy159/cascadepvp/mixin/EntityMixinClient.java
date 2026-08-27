package dev.entropy159.cascadepvp.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.entropy159.cascadepvp.registry.CascadePotions;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixinClient {
    @ModifyReturnValue(method = "isInvisible", at = @At("RETURN"))
    private boolean invis(boolean original) {
        var player = Minecraft.getInstance().player;
        if ((Object) this instanceof Player self && player != null && player.hasEffect(CascadePotions.INVERSE_INVISIBILITY) && player != self) {
            return true;
        }
        return original;
    }
}
