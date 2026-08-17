package dev.entropy159.cascadepvp.entities.projectile;

import dev.entropy159.cascadepvp.items.weapon.hexblade.aspects.MagicMissileAspect;
import dev.entropy159.cascadepvp.registry.CascadeEntities;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MagicMissileProjectile extends AbstractArrow {
    private int targetID = -1;
    private boolean diving = false;
    private boolean explodeNextTick = false;

    public MagicMissileProjectile(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
    }

    private MagicMissileProjectile(LivingEntity owner, ItemStack stack) {
        super(CascadeEntities.MAGIC_MISSILE.get(), owner, owner.level(), ItemStack.EMPTY, stack);
    }

    public static MagicMissileProjectile create(Player shooter, ItemStack stack) {
        var entity = new MagicMissileProjectile(shooter, stack);
        entity.setPos(shooter.getEyePosition());
        shooter.level().addFreshEntity(entity);
        return entity;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("targetID", targetID);
        tag.putBoolean("diving", diving);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        targetID = tag.getInt("targetID");
        diving = tag.getBoolean("diving");
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            return;
        }
        for (int i = 0; i < 5; i++) {
            level().addParticle(new DustParticleOptions(Vec3.fromRGB24(MagicMissileAspect.COLOR | 0xFF000000).toVector3f(), 1), getX(), getY(), getZ(), 0, 0, 0);
        }
        if (explodeNextTick) {
            explode();
            return;
        }
        if (inGround) {
            explodeNextTick = true;
            return;
        }
        if (!diving) {
            Entity target = getTarget();
            if (target == null) {
                return;
            }
            Vec3 targetPos = target.getEyePosition().add(0, 30, 0);
            Vec3 direction = targetPos.subtract(position());
            double speed = MagicMissileAspect.SPEED.get();
            if (direction.length() > speed) {
                direction.normalize().scale(speed);
            }
            setDeltaMovement(direction);
            if (direction.length() < 1) {
                diving = true;
                setDeltaMovement(0, -MagicMissileAspect.DIVE_SPEED.get(), 0);
            }
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        if (!ownedBy(result.getEntity())) {
            explodeNextTick = true;
        }
    }

    public void explode() {
        level().explode(this, getX(), getY(), getZ(), MagicMissileAspect.POWER.get(), Level.ExplosionInteraction.MOB);
        discard();
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
    }

    public void setTarget(@Nullable Entity entity) {
        targetID = entity == null ? -1 : entity.getId();
    }

    public @Nullable Entity getTarget() {
        return targetID >= 0 ? level().getEntity(targetID) : null;
    }
}
