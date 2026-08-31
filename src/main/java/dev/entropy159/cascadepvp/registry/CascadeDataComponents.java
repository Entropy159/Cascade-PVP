package dev.entropy159.cascadepvp.registry;

import com.mojang.serialization.Codec;
import dev.entropy159.cascadepvp.CascadePVP;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class CascadeDataComponents {
    public static final DeferredRegister.DataComponents REGISTRY = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CascadePVP.MODID);

    public static final Supplier<DataComponentType<Boolean>> SUPERCHARGED = REGISTRY.registerComponentType("supercharged", builder -> builder.networkSynchronized(ByteBufCodecs.BOOL).persistent(Codec.BOOL));
    public static final Supplier<DataComponentType<Boolean>> USING_ABILITY = REGISTRY.registerComponentType("using_ability", builder -> builder.networkSynchronized(ByteBufCodecs.BOOL));

    public static final Supplier<DataComponentType<Integer>> HEXBLADE_ASPECT = REGISTRY.registerComponentType("hexblade_aspect", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));
    public static final Supplier<DataComponentType<Integer>> NEXT_HEXBLADE_ASPECT = REGISTRY.registerComponentType("next_hexblade_aspect", builder -> builder.networkSynchronized(ByteBufCodecs.INT).persistent(Codec.INT));
    public static final Supplier<DataComponentType<List<Integer>>> HEXBLADE_ASPECTS = REGISTRY.registerComponentType("hexblade_aspects", builder -> builder.persistent(Codec.list(Codec.INT)).networkSynchronized(ByteBufCodecs.INT.apply(ByteBufCodecs.list())));

    public static void init(IEventBus bus) {
        REGISTRY.register(bus);
    }
}
