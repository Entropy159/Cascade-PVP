package dev.entropy159.cascadepvp.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue REALITY_TEAR_RESOLUTION = BUILDER.comment("The resolution of the reality tear texture").defineInRange("realityTearResolution", 512, 1, 16384);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
