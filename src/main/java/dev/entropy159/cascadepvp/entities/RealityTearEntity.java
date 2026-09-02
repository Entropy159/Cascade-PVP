package dev.entropy159.cascadepvp.entities;

import dev.entropy159.cascadepvp.config.ServerConfig;
import dev.entropy159.cascadepvp.dimensions.QuantumDimension;
import dev.entropy159.cascadepvp.items.RiftwandItem;
import dev.entropy159.cascadepvp.registry.CascadeEntities;
import dev.entropy159.entropylib.util.EventScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

public class RealityTearEntity extends Entity {
    private static final int DEFAULT_COLOR = 0x7300F0;
    private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(RealityTearEntity.class, EntityDataSerializers.INT);
    private int removeTimer = -1;

    public RealityTearEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
    }

    public static @Nullable RealityTearEntity create(ServerLevel level, BlockPos pos, @Nullable Player owner) {
        if (!level.getEntities((Entity) null, new AABB(pos), e -> e instanceof RealityTearEntity).isEmpty()) {
            return null;
        }
        var entity = new RealityTearEntity(CascadeEntities.REALITY_TEAR.get(), level);
        entity.setPos(pos.getBottomCenter().add(0, entity.getBbHeight() / 2d, 0));
        level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS);
        if (owner != null && owner.getTeamColor() != 16777215) {
            entity.setColor(owner.getTeamColor());
        }
        return level.addFreshEntity(entity) ? entity : null;
    }

    public static boolean startSpawn(ServerLevel level, BlockPos pos, @Nullable Player owner) {
        if (!level.getEntities((Entity) null, new AABB(pos), e -> e instanceof RealityTearEntity).isEmpty()) {
            return false;
        }
        int color = owner == null || owner.getTeam() == null ? DEFAULT_COLOR : owner.getTeamColor();
        int delay = ServerConfig.RIFTWAND_SPAWN_DELAY.get();
        Vec3 center = pos.getCenter();
        AtomicInteger timer = new AtomicInteger();
        EventScheduler.scheduleUntil(1, () -> timer.getAndIncrement() >= delay, () -> level.sendParticles(new DustParticleOptions(Vec3.fromRGB24(color).toVector3f(), 1), center.x, center.y, center.z, 1, 0, 0, 0, 0));
        EventScheduler.schedule(1, () -> timer.get() >= delay, () -> {
            create(level, pos, owner);
        });
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        builder.define(DATA_COLOR, DEFAULT_COLOR);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        if (tag.contains("Color")) {
            setColor(tag.getInt("Color"));
        }
        if (tag.contains("RemoveTimer")) {
            removeTimer = tag.getInt("RemoveTimer");
        }
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putInt("Color", getColor());
        tag.putInt("RemoveTimer", removeTimer);
    }

    public int getColor() {
        return getEntityData().get(DATA_COLOR);
    }

    public void setColor(int color) {
        getEntityData().set(DATA_COLOR, color);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public @NotNull InteractionResult interact(@NotNull Player p, @NotNull InteractionHand hand) {
        if (p instanceof ServerPlayer player && hand == InteractionHand.MAIN_HAND) {
            if (player.getItemInHand(hand).getItem() instanceof RiftwandItem && player.isCrouching()) {
                removeTimer = ServerConfig.REALITY_TEAR_REMOVE_DELAY.get();
                return InteractionResult.SUCCESS;
            }
            if (level().dimension().equals(QuantumDimension.QUANTUM)) {
                if (QuantumDimension.teleportFrom(player, blockPosition())) {
                    return InteractionResult.SUCCESS;
                }
            }
            if (level().dimension().equals(Level.OVERWORLD)) {
                if (QuantumDimension.teleportTo(player, blockPosition(), true)) {
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.interact(p, hand);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide()) {
            if (removeTimer > 0) {
                removeTimer--;
                if (level() instanceof ServerLevel level) {
                    int color = getColor();
                    Vec3 center = blockPosition().getCenter();
                    level.sendParticles(new DustParticleOptions(Vec3.fromRGB24(color).toVector3f(), 1), center.x, center.y, center.z, 1, 0, 0, 0, 0);
                }
            } else if (removeTimer == 0) {
                level().playSound(null, blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS);
                discard();
            }
        }
    }
}
