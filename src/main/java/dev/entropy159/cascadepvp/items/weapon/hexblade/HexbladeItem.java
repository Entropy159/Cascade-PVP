package dev.entropy159.cascadepvp.items.weapon.hexblade;

import dev.entropy159.cascadepvp.items.weapon.CascadeSword;
import dev.entropy159.cascadepvp.items.weapon.hexblade.aspects.*;
import dev.entropy159.cascadepvp.registry.CascadeDataComponents;
import dev.entropy159.cascadepvp.registry.CascadeItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class HexbladeItem extends CascadeSword {
    public static final List<HexbladeAspect> ASPECTS = List.of(new SpatialShiftAspect(), new VoidFormAspect(), new MagicMissileAspect(), new FireballAspect(), new ShieldAspect());

    public HexbladeItem(Properties props) {
        super(props.component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false));
    }

    @Override
    public @Nullable UUID ownerUUID() {
        return UUID.fromString("02900643-b684-41bf-8d40-bef53b9426bd");
    }

    @Override
    public String description() {
        return "Forged with a mix of the elements and a hint of entropic magic, the blade is powerful yet unstable.";
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        if (!player.getOffhandItem().isEmpty() && player.getMainHandItem().is(CascadeItems.HEXBLADE)) {
            ItemStack stack = player.getMainHandItem();
            ItemStack other = player.getOffhandItem();
            var aspects = getAspects(stack);
            for (int i = 0; i < ASPECTS.size(); i++) {
                var aspect = ASPECTS.get(i);
                if (!aspects.contains(aspect) && other.is(aspect.getItem().asItem())) {
                    if (unlockAspect(stack, i)) {
                        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS);
                        other.shrink(1);
                        return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
                    }
                }
            }
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public int activeAbility(ServerPlayer player, ItemStack stack, LivingEntity target) {
        HexbladeAspect aspect = getAspect(stack);
        if (aspect == null) {
            newAspect(stack);
            return 0;
        }
        if (aspect.onUse(player, stack, target)) {
            newAspect(stack);
            aspect = getAspect(stack);
            return aspect == null ? 0 : aspect.cooldown();
        }
        return 0;
    }

    public static @Nullable HexbladeAspect getAspect(ItemStack stack) {
        var aspects = getAspects(stack);
        int index = stack.getOrDefault(CascadeDataComponents.HEXBLADE_ASPECT, -1);
        if (index >= 0 && index < aspects.size()) {
            return aspects.get(index);
        }
        return null;
    }

    public static @Nullable HexbladeAspect getNextAspect(ItemStack stack) {
        var aspects = getAspects(stack);
        int index = stack.getOrDefault(CascadeDataComponents.NEXT_HEXBLADE_ASPECT, -1);
        if (index >= 0 && index < aspects.size()) {
            return aspects.get(index);
        }
        return null;
    }

    public static void newAspect(ItemStack stack) {
        var aspects = getAspects(stack);
        if (aspects.isEmpty()) {
            return;
        }
        int newIndex = new Random().nextInt(aspects.size());
        int queuedIndex = stack.getOrDefault(CascadeDataComponents.NEXT_HEXBLADE_ASPECT, new Random().nextInt(aspects.size()));
        stack.set(CascadeDataComponents.HEXBLADE_ASPECT, queuedIndex);
        stack.set(CascadeDataComponents.NEXT_HEXBLADE_ASPECT, newIndex);
    }

    public static int getAspectColor(ItemStack stack) {
        var aspect = getAspect(stack);
        return aspect == null ? 0xFF666666 : aspect.color() | 0xFF000000;
    }

    public static int getNextAspectColor(ItemStack stack) {
        var aspect = getNextAspect(stack);
        return aspect == null ? 0xFF666666 : aspect.color() | 0xFF000000;
    }

    public static List<HexbladeAspect> getAspects(ItemStack stack) {
        return ASPECTS.stream().filter(aspect -> stack.getOrDefault(CascadeDataComponents.HEXBLADE_ASPECTS, List.of()).contains(ASPECTS.indexOf(aspect))).toList();
    }

    public static boolean unlockAspect(ItemStack stack, int index) {
        ArrayList<Integer> aspects = new ArrayList<>(stack.getOrDefault(CascadeDataComponents.HEXBLADE_ASPECTS, List.of()));
        if (index >= 0 && index < ASPECTS.size() && aspects.stream().noneMatch(aspect -> index == aspect)) {
            boolean update = aspects.isEmpty();
            aspects.add(index);
            stack.set(CascadeDataComponents.HEXBLADE_ASPECTS, aspects);
            if (update) {
                newAspect(stack);
            }
            return true;
        }
        return false;
    }

    @Override
    public void modifyTooltip(List<Component> tooltip, ItemStack stack, Player player, TooltipContext context, TooltipFlag flag) {
        var aspect = getAspect(stack);
        if (aspect != null) {
            tooltip.add(Component.literal("Current: ").withStyle(ChatFormatting.DARK_GRAY).append(Component.literal(aspect.name()).withColor(aspect.color())));
        }
        var next = getNextAspect(stack);
        if (next != null) {
            tooltip.add(Component.literal("Next: ").withStyle(ChatFormatting.DARK_GRAY).append(Component.literal(next.name()).withColor(next.color())));
        }
        tooltip.add(Component.literal("Unlocked " + getAspects(stack).size() + "/" + ASPECTS.size()).withStyle(ChatFormatting.DARK_AQUA));
        super.modifyTooltip(tooltip, stack, player, context, flag);
    }
}
