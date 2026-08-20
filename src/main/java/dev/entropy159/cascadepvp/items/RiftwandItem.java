package dev.entropy159.cascadepvp.items;

import dev.entropy159.cascadepvp.entities.RealityTearEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class RiftwandItem extends Item {
    public RiftwandItem(Properties props) {
        super(props.rarity(Rarity.RARE).stacksTo(1));
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        var player = context.getPlayer();
        if (player != null && context.getLevel() instanceof ServerLevel level) {
            if (!player.getCooldowns().isOnCooldown(context.getItemInHand().getItem())) {
                for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
                    if (player.getInventory().getItem(slot).is(Items.AMETHYST_SHARD) || player.getAbilities().instabuild) {
                        BlockPos pos = context.getClickedPos().offset(context.getClickedFace().getNormal());
                        if (RealityTearEntity.startSpawn(level, pos, player)) {
                            if (!player.getAbilities().instabuild) {
                                player.getInventory().getItem(slot).consume(1, player);
                            }
                            player.getCooldowns().addCooldown(context.getItemInHand().getItem(), 60 * 20);
                            return InteractionResult.SUCCESS_NO_ITEM_USED;
                        } else {
                            return InteractionResult.FAIL;
                        }
                    }
                }
            }
        }
        return super.useOn(context);
    }
}
