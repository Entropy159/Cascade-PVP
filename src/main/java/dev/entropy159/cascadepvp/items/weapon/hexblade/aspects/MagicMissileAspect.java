package dev.entropy159.cascadepvp.items.weapon.hexblade.aspects;

import dev.entropy159.cascadepvp.entities.projectile.MagicMissileProjectile;
import dev.entropy159.cascadepvp.items.weapon.hexblade.HexbladeAspect;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class MagicMissileAspect extends HexbladeAspect {
    public static final int COLOR = 0x00F535;
    public static Config<Double> RANGE;
    public static Config<Double> SPEED;
    public static Config<Double> DIVE_SPEED;
    public static Config<Integer> POWER;

    public MagicMissileAspect() {
        super("Magic Missile", COLOR, 30 * 20);
        RANGE = addConfig("Range", 50D);
        SPEED = addConfig("Speed", 0.2D);
        DIVE_SPEED = addConfig("Dive Speed", 2D);
        POWER = addConfig("Power", 2);
    }

    @Override
    public ItemLike getItem() {
        return Items.BLAZE_POWDER;
    }

    @Override
    public boolean onUse(ServerPlayer player, ItemStack stack, LivingEntity target) {
        if (target != null && player.distanceTo(target) <= RANGE.get()) {
            var entity = MagicMissileProjectile.create(player, player.getMainHandItem());
            entity.setTarget(target);
            return true;
        }
        return false;
    }
}
