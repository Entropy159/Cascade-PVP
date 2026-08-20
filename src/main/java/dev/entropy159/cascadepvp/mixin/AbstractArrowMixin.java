package dev.entropy159.cascadepvp.mixin;

import dev.entropy159.cascadepvp.config.ServerConfig;
import dev.entropy159.entropylib.util.EventScheduler;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {
    @Shadow
    protected int inGroundTime;

    protected AbstractArrowMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "onHitBlock", at = @At("TAIL"))
    private void explodeBlock(BlockHitResult result, CallbackInfo ci) {
        if (ServerConfig.BOOMBOW_DELAY_TICKS.get() < 1) {
            cascadePVP$explode();
        }
    }

    @Inject(method = "onHitEntity", at = @At("TAIL"))
    private void explodeEntity(EntityHitResult result, CallbackInfo ci) {
        int delay = ServerConfig.BOOMBOW_DELAY_TICKS.get();
        if (delay < 1) {
            cascadePVP$explode();
        } else {
            EventScheduler.schedule(delay, () -> cascadePVP$explode(result.getEntity().position()));
        }
    }

    @Unique
    private void cascadePVP$explode() {
        cascadePVP$explode(position());
    }

    @Unique
    private void cascadePVP$explode(Position pos) {
        if (level() instanceof ServerLevel level && getTags().contains("Explosive")) {
            level.explode(this, pos.x(), pos.y(), pos.z(), ServerConfig.BOOMBOW_EXPLOSION_RADIUS.get().floatValue(), false, Level.ExplosionInteraction.NONE);
            removeTag("Explosive");
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void despawn(CallbackInfo ci) {
        int delay = ServerConfig.BOOMBOW_DELAY_TICKS.get();
        if (delay > 0 && inGroundTime >= delay) {
            cascadePVP$explode();
        }
        if (getTags().contains("Expires") && tickCount >= ServerConfig.BOW_OF_THE_GALADHRIM_ARROW_EXPIRATION.get()) {
            discard();
        }
    }

    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 0.99F))
    private float airResistance(float constant) {
        return getTags().contains("NoResistance") ? ServerConfig.BOW_OF_THE_GALADHRIM_AIR_RESISTANCE.get().floatValue() : constant;
    }
}
