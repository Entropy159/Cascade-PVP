package dev.entropy159.cascadepvp.items.weapon.hexblade.aspects;

import dev.entropy159.cascadepvp.items.weapon.hexblade.HexbladeAspect;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;

public class FireballAspect extends HexbladeAspect {
    private final Config<Integer> power;

    public FireballAspect() {
        super("Fireball", 0xFF8C00, 30 * 20);
        power = addConfig("Power", 3);
    }

    @Override
    public ItemLike getItem() {
        return Items.FIRE_CHARGE;
    }

    @Override
    public boolean onUse(ServerPlayer player, ItemStack stack, LivingEntity target) {
        var fireball = new LargeFireball(player.serverLevel(), player, Vec3.ZERO, power.get());
        fireball.setPos(player.getEyePosition().add(player.getLookAngle().scale(2)));
        player.serverLevel().addFreshEntity(fireball);
        return true;
    }
}
