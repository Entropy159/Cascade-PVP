package dev.entropy159.cascadepvp.mixin;

import dev.entropy159.cascadepvp.config.ServerConfig;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WitherSkull.class)
public class WitherSkullMixin {
    @ModifyArg(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)Lnet/minecraft/world/level/Explosion;"), index = 6)
    private Level.ExplosionInteraction lessGriefing(Level.ExplosionInteraction explosionInteraction) {
        return ServerConfig.LESS_MOB_GRIEFING.get() ? Level.ExplosionInteraction.NONE : explosionInteraction;
    }
}
