package dev.entropy159.cascadepvp.config;

import dev.entropy159.cascadepvp.items.weapon.hexblade.HexbladeItem;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue REAPER_SCYTHE_LIFESTEAL = BUILDER.comment("The percentage of damage to give back as health").defineInRange("reaperScythe.lifesteal", 0.2, 0, 10);
    public static final ModConfigSpec.IntValue REAPER_SCYTHE_STRENGTH_DURATION = BUILDER.comment("The duration in ticks of the strength effect").defineInRange("reaperScythe.strengthDuration", 200, 0, 20000);
    public static final ModConfigSpec.IntValue REAPER_SCYTHE_STRENGTH_COOLDOWN = BUILDER.comment("The cooldown in ticks of the active ability").defineInRange("reaperScythe.cooldown", 60 * 20, 0, 20000);

    public static final ModConfigSpec.IntValue SHADOW_KARAMBIT_INVIS_DURATION = BUILDER.comment("The duration in ticks of the invisibility").defineInRange("shadowKarambit.invisDuration", 600, 1, 20000);
    public static final ModConfigSpec.BooleanValue SHADOW_KARAMBIT_INVIS_PARTICLES = BUILDER.comment("Whether the invisibility has particles").define("shadowKarambit.invisParticles", true);
    public static final ModConfigSpec.IntValue SHADOW_KARAMBIT_INVIS_COOLDOWN = BUILDER.comment("The cooldown in ticks of the active ability").defineInRange("shadowKarambit.invisCooldown", 600, 0, 20000);
    public static final ModConfigSpec.DoubleValue SHADOW_KARAMBIT_WITHER_CHANCE = BUILDER.comment("The chance to inflict wither on hit entities (0-1), is multiplied by the damage").defineInRange("shadowKarambit.witherChance", 0.05, 0, 1);
    public static final ModConfigSpec.IntValue SHADOW_KARAMBIT_WITHER_DURATION = BUILDER.comment("The duration of the wither effect in ticks, is multiplied by the damage").defineInRange("shadowKarambit.witherDuration", 60, 0, 20000);

    public static final ModConfigSpec.IntValue KINGS_WILL_COOLDOWN = BUILDER.comment("The cooldown in ticks of the active ability").defineInRange("kingsWill.cooldown", 60 * 20, 0, 20000);

    public static final ModConfigSpec.IntValue ABYSSAL_IMPACT_COOLDOWN = BUILDER.comment("The cooldown in ticks of the active ability").defineInRange("abyssalImpact.cooldown", 45 * 20, 0, 20000);
    public static final ModConfigSpec.DoubleValue ABYSSAL_IMPACT_VELOCITY = BUILDER.comment("The velocity of the active ability").defineInRange("abyssalImpact.velocity", 2, 0D, 15);

    public static final ModConfigSpec.DoubleValue BOW_OF_THE_GALADHRIM_VELOCITY = BUILDER.comment("The velocity multiplier of arrows shot").defineInRange("bowOfTheGaladhrim.velocity", 2D, 0, 10);
    public static final ModConfigSpec.DoubleValue BOW_OF_THE_GALADHRIM_ACCURACY = BUILDER.comment("The inaccuracy of the bow is divided by this amount. Set to zero for perfect accuracy").defineInRange("bowOfTheGaladhrim.accuracy", 2D, 0, 10);
    public static final ModConfigSpec.IntValue BOW_OF_THE_GALADHRIM_COOLDOWN = BUILDER.comment("The cooldown in ticks of the active ability").defineInRange("bowOfTheGaladhrim.cooldown", 60 * 20, 0, 20000);
    public static final ModConfigSpec.DoubleValue BOW_OF_THE_GALADHRIM_MULT = BUILDER.comment("The damage multiplier for the power shot").defineInRange("bowOfTheGaladhrim.damageMult", 5, 0, 20D);
    public static final ModConfigSpec.DoubleValue BOW_OF_THE_GALADHRIM_AIR_RESISTANCE = BUILDER.comment("The air resistance of arrows show by this bow, movement is multiplied by this every tick.").defineInRange("bowOfTheGaladhrim.resistance", 0.99, 0, 10);
    public static final ModConfigSpec.IntValue BOW_OF_THE_GALADHRIM_ARROW_EXPIRATION = BUILDER.comment("The ticks it takes for an arrow shot by this bow to expire").defineInRange("bowOfTheGaladhrim.expiration", 5 * 60 * 20, 0, 20 * 60 * 20);

    public static final ModConfigSpec.DoubleValue BOOMBOW_EXPLOSION_RADIUS = BUILDER.comment("The explosion radius of shot arrows. TNT is 4, crystal is 6").defineInRange("boombow.explosionRadius", 2D, 0, 15);
    public static final ModConfigSpec.IntValue BOOMBOW_DELAY_TICKS = BUILDER.comment("The delay in ticks before an arrow explodes on impact").defineInRange("boombow.delay", 20, 0, 20000);

    public static ModConfigSpec getSpec() {
        for (var aspect : HexbladeItem.ASPECTS) {
            aspect.registerConfigs(BUILDER);
        }
        return BUILDER.build();
    }
}
