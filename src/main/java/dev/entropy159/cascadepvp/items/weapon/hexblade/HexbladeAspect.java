package dev.entropy159.cascadepvp.items.weapon.hexblade;

import dev.entropy159.cascadepvp.CascadePVP;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.ModConfigSpec;

import javax.annotation.Nullable;
import java.util.ArrayList;

public abstract class HexbladeAspect {
    private final String name;
    private final int color;

    private ModConfigSpec.IntValue cooldown;
    private final int defaultCooldown;
    @SuppressWarnings("rawtypes")
    private final ArrayList<Config> configs = new ArrayList<>();

    public HexbladeAspect(String name, int color, int cooldown) {
        this.name = name;
        this.color = color;
        defaultCooldown = cooldown;
    }

    public String name() {
        return name;
    }

    public int color() {
        return color;
    }

    public int cooldown() {
        return cooldown.getAsInt();
    }

    public abstract boolean onUse(ServerPlayer player, ItemStack stack, @Nullable LivingEntity target);

    public void registerConfigs(ModConfigSpec.Builder builder) {
        CascadePVP.REGISTRATE.configLang(configName(""), name());
        cooldown = builder.defineInRange(configName("cooldown"), defaultCooldown, 0, 60 * 60 * 20);
        CascadePVP.REGISTRATE.configLang(cooldown, "Cooldown");
        configs.forEach(config -> {
            config.value = builder.define(configName(config.name.toLowerCase().replace(" ", "")), config.defaultValue);
            CascadePVP.REGISTRATE.configLang(config.value, config.name);
        });
    }

    private String configName(String suffix) {
        return "hexblade." + name().replace(" ", "") + (suffix.isBlank() ? "" : ".") + suffix;
    }

    public <T> Config<T> addConfig(String name, T defaultValue) {
        var config = new Config<>(name, defaultValue);
        configs.add(config);
        return config;
    }

    public abstract ItemLike getItem();

    public static class Config<T> {
        private final String name;
        private final T defaultValue;
        private ModConfigSpec.ConfigValue<T> value;

        public Config(String name, T defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
        }

        public T get() {
            return value.get();
        }

        public T getDefault() {
            return defaultValue;
        }
    }
}
