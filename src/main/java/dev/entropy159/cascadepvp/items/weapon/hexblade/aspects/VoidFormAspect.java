package dev.entropy159.cascadepvp.items.weapon.hexblade.aspects;

import dev.entropy159.cascadepvp.items.weapon.hexblade.HexbladeAspect;
import dev.entropy159.cascadepvp.registry.CascadePotions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class VoidFormAspect extends HexbladeAspect {
    public static final int COLOR = 0x7D00FF;
    public Config<Integer> DURATION;

    public VoidFormAspect() {
        super("Void Form", COLOR, 60 * 20);
        DURATION = addConfig("Duration", 8 * 20);
    }

    @Override
    public ItemLike getItem() {
        return Items.ENDER_EYE;
    }

    @Override
    public boolean onUse(ServerPlayer player, ItemStack stack, LivingEntity target) {
        player.addEffect(new MobEffectInstance(CascadePotions.VOID_FORM, DURATION.get()));
        return true;
    }
}
