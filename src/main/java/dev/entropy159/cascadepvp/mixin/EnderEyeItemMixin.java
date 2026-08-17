package dev.entropy159.cascadepvp.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.entropy159.cascadepvp.registry.CascadeGameRules;
import net.minecraft.world.item.EnderEyeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderEyeItem.class)
public class EnderEyeItemMixin {
    @Definition(id = "blockstate", local = @Local(type = BlockState.class))
    @Definition(id = "is", method = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z")
    @Definition(id = "END_PORTAL_FRAME", field = "Lnet/minecraft/world/level/block/Blocks;END_PORTAL_FRAME:Lnet/minecraft/world/level/block/Block;")
    @Expression("blockstate.is(END_PORTAL_FRAME)")
    @ModifyExpressionValue(method = "useOn", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean gamerule(boolean original, @Local(argsOnly = true) UseOnContext context) {
        return original && !context.getLevel().getGameRules().getBoolean(CascadeGameRules.DISABLE_END_PORTALS);
    }
}
