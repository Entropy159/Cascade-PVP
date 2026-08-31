package dev.entropy159.cascadepvp.items.weapon;

import dev.entropy159.cascadepvp.items.CascadeItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public abstract class CascadeSword extends SwordItem implements CascadeItem {
    public CascadeSword(Properties props) {
        super(Tiers.NETHERITE, CascadeItem.sword(props));
    }

    public CascadeSword(Properties props, int damage, float speed) {
        this(props, SwordItem.createAttributes(Tiers.NETHERITE, damage, speed));
    }

    public CascadeSword(Properties props, ItemAttributeModifiers attributes) {
        super(Tiers.NETHERITE, CascadeItem.sword(props).attributes(attributes));
    }
}
