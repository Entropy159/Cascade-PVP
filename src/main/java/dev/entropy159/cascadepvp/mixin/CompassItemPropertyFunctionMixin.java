package dev.entropy159.cascadepvp.mixin;

import dev.entropy159.cascadepvp.dimensions.QuantumDimension;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CompassItemPropertyFunction.class)
public class CompassItemPropertyFunctionMixin {
    @Redirect(method = "getCompassRotation", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/CompassItemPropertyFunction$CompassTarget;getPos(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/core/GlobalPos;"))
    private GlobalPos quantumCompass(CompassItemPropertyFunction.CompassTarget instance, ClientLevel clientLevel, ItemStack itemStack, Entity entity) {
        var pos = instance.getPos(clientLevel, itemStack, entity);
        if (pos != null && !clientLevel.dimension().equals(pos.dimension())) {
            return QuantumDimension.convert(clientLevel, pos);
        }
        return pos;
    }
}
