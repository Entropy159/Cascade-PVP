package dev.entropy159.cascadepvp.mixin;

import dev.entropy159.cascadepvp.config.ServerConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LargeFireball.class)
public class LargeFireballMixin extends Fireball {
    public LargeFireballMixin(EntityType<? extends Fireball> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyArg(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)Lnet/minecraft/world/level/Explosion;"), index = 6)
    private Level.ExplosionInteraction griefing(Level.ExplosionInteraction explosionInteraction) {
        return ServerConfig.LESS_MOB_GRIEFING.get() ? Level.ExplosionInteraction.NONE : explosionInteraction;
    }
}
