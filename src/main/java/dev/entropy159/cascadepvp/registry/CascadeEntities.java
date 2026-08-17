package dev.entropy159.cascadepvp.registry;

import com.tterrag.registrate.util.entry.EntityEntry;
import dev.entropy159.cascadepvp.client.renderers.MagicMissileRenderer;
import dev.entropy159.cascadepvp.client.renderers.RealityTearRenderer;
import dev.entropy159.cascadepvp.entities.RealityTearEntity;
import dev.entropy159.cascadepvp.entities.projectile.MagicMissileProjectile;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.MobCategory;

import static dev.entropy159.cascadepvp.CascadePVP.REGISTRATE;

public class CascadeEntities {
    public static EntityEntry<MagicMissileProjectile> MAGIC_MISSILE = REGISTRATE.entity("magic_missile", MagicMissileProjectile::new, MobCategory.MISC).renderer(() -> MagicMissileRenderer::new).register();
    public static EntityEntry<RealityTearEntity> REALITY_TEAR = REGISTRATE.entity("reality_tear", RealityTearEntity::new, MobCategory.MISC).tag(EntityTypeTags.FALL_DAMAGE_IMMUNE, EntityTypeTags.IMMUNE_TO_INFESTED, EntityTypeTags.IMMUNE_TO_OOZING, EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES).properties(builder -> builder.fireImmune().sized(0.6f, 0.6f)).renderer(() -> RealityTearRenderer::new).register();

    public static void init() {
    }
}
