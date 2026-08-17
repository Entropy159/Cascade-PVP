package dev.entropy159.cascadepvp.registry;

import net.minecraft.world.level.GameRules;

public class CascadeGameRules {
    public static GameRules.Key<GameRules.BooleanValue> RESTRICT_ABILITIES = GameRules.register("restrictAbilities", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
    public static GameRules.Key<GameRules.BooleanValue> QUANTUM_DEATH_PROTECTION = GameRules.register("quantumDeathProtection", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
    public static GameRules.Key<GameRules.BooleanValue> DISABLE_END_PORTALS = GameRules.register("disableEndPortals", GameRules.Category.MISC, GameRules.BooleanValue.create(false));

    public static void init() {
    }
}
