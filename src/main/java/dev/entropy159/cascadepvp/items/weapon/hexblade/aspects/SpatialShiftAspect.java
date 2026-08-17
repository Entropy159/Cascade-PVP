package dev.entropy159.cascadepvp.items.weapon.hexblade.aspects;

import dev.entropy159.cascadepvp.items.weapon.hexblade.HexbladeAspect;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class SpatialShiftAspect extends HexbladeAspect {
    public static Config<Double> RANGE;

    public SpatialShiftAspect() {
        super("Spatial Shift", 0xFF0000, 30 * 20);
        RANGE = addConfig("Range", 25d);
    }

    @Override
    public ItemLike getItem() {
        return Items.ENDER_PEARL;
    }

    @Override
    public boolean onUse(ServerPlayer player, ItemStack stack, LivingEntity target) {
        if (target != null && player.distanceTo(target) <= RANGE.get()) {
            Vec3 currentPos = player.position();
            float xRot = player.getXRot();
            float yRot = player.getYRot();
            player.teleportTo(player.serverLevel(), target.getX(), target.getY(), target.getZ(), Set.of(), target.getYRot(), target.getXRot());
            target.teleportTo(player.serverLevel(), currentPos.x, currentPos.y, currentPos.z, Set.of(), yRot, xRot);
            return true;
        }
        return false;
    }
}
