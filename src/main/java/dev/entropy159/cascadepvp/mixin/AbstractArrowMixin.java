package dev.entropy159.cascadepvp.mixin;

import dev.entropy159.cascadepvp.config.ServerConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {
    protected AbstractArrowMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "onHitBlock", at = @At("TAIL"))
    private void explodeBlock(BlockHitResult result, CallbackInfo ci) {
        cascadePVP$explode(true);
    }

    @Inject(method = "onHitEntity", at = @At("TAIL"))
    private void explodeEntity(EntityHitResult result, CallbackInfo ci) {
        cascadePVP$explode(false);
    }

    @Unique
    private void cascadePVP$explode(boolean checkRemove) {
        if (!(isRemoved() && checkRemove) && level() instanceof ServerLevel level && getTags().contains("Explosive")) {
            level.explode(this, position().x, position().y, position().z, ServerConfig.BOOMBOW_EXPLOSION_RADIUS.get(), false, Level.ExplosionInteraction.NONE);
            removeTag("Explosive");
        }
    }
}
