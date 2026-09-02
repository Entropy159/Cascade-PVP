package dev.entropy159.cascadepvp;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.logging.LogUtils;
import dev.entropy159.cascadepvp.config.ClientConfig;
import dev.entropy159.cascadepvp.config.ServerConfig;
import dev.entropy159.cascadepvp.dimensions.QuantumDimension;
import dev.entropy159.cascadepvp.entities.RealityTearEntity;
import dev.entropy159.cascadepvp.registry.*;
import dev.entropy159.entropylib.registrate.EntropyRegistrate;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.function.Predicate;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@Mod(CascadePVP.MODID)
public class CascadePVP {
    public static final String MODID = "cascadepvp";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final EntropyRegistrate REGISTRATE = EntropyRegistrate.create(MODID);

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + MODID))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> CascadeItems.HEXBLADE.get().getDefaultInstance())
            .build());

    public CascadePVP(IEventBus bus, ModContainer container) {
        NeoForge.EVENT_BUS.register(this);

        TABS.register(bus);
        REGISTRATE.defaultCreativeTab(TAB.getKey());
        CascadeGameRules.init();
        CascadeItems.init();
        CascadePotions.init();
        CascadeDataComponents.init(bus);
        CascadeEntities.init();

        addLang();

        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        container.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(literal("quantum").then(literal("tp").requires(ctx -> ctx.isPlayer() && ctx.hasPermission(2)).executes(ctx -> {
            var player = ctx.getSource().getPlayer();
            if (player != null) {
                if (player.level().dimension().equals(QuantumDimension.QUANTUM)) {
                    if (QuantumDimension.teleportFrom(player)) {
                        return 1;
                    }
                    return 0;
                } else if (player.level().dimension().equals(Level.OVERWORLD)) {
                    if (QuantumDimension.teleportTo(player, false)) {
                        return 1;
                    }
                    return 0;
                }
                ctx.getSource().sendFailure(Component.literal("You need to be in the overworld or the quantum realm!").withStyle(ChatFormatting.RED));
            }
            return 0;
        })).then(literal("route").then(argument("start", ColumnPosArgument.columnPos()).then(argument("destination", ColumnPosArgument.columnPos()).then(argument("radius", IntegerArgumentType.integer(1, 10000)).executes(ctx -> {
            var start = ColumnPosArgument.getColumnPos(ctx, "start");
            var destination = ColumnPosArgument.getColumnPos(ctx, "destination");
            int radius = IntegerArgumentType.getInteger(ctx, "radius");
            var route = QuantumDimension.findBestRoute(ctx.getSource().getServer(), start, destination, radius);
            if (route != null) {
                double distance = route.totalDistance(start, destination);
                ctx.getSource().sendSuccess(() -> Component.literal("Found route with distance " + distance).withStyle(ChatFormatting.GREEN), false);
                ctx.getSource().sendSystemMessage(route.toComponent(start, destination));
                return 1;
            }
            ctx.getSource().sendFailure(Component.literal("No route found!").withStyle(ChatFormatting.RED));
            return 0;
        }))))).then(literal("rift").requires(ctx -> ctx.hasPermission(2)).then(argument("position", BlockPosArgument.blockPos()).executes(ctx -> {
            var pos = BlockPosArgument.getBlockPos(ctx, "position");
            var rift = RealityTearEntity.create(ctx.getSource().getLevel(), pos, ctx.getSource().getPlayer());
            return rift == null ? 0 : 1;
        }))));
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static @Nullable Entity getTargetedEntity(Entity source, double range, Predicate<Entity> predicate) {
        Vec3 start = source.getEyePosition();
        Vec3 end = start.add(source.getLookAngle().scale(range));
        AABB box = source.getBoundingBox().expandTowards(source.getLookAngle().scale(range)).inflate(1);
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(source, start, end, box, predicate, Double.MAX_VALUE);
        return hit == null ? null : hit.getEntity();
    }

    private static void addLang() {
        REGISTRATE.addRawLang("cpvp.message.cannot_use", "You cannot use this item!");

        REGISTRATE.addRawLang("itemGroup." + MODID, "Cascade PVP");

        REGISTRATE.addRawLang("key.cascadepvp.ability", "Cascade Ability");
        REGISTRATE.addRawLang("key.cascadepvp.utility", "Cascade Utility");

        REGISTRATE.configLang("reaperScythe", "Reaper Scythe");
        REGISTRATE.configLang(ServerConfig.REAPER_SCYTHE_LIFESTEAL, "Lifesteal Amount");
        REGISTRATE.configLang(ServerConfig.REAPER_SCYTHE_STRENGTH_COOLDOWN, "Strength Cooldown");
        REGISTRATE.configLang(ServerConfig.REAPER_SCYTHE_STRENGTH_DURATION, "Strength Duration");
        REGISTRATE.configLang("shadowKarambit", "Shadow Karambit");
        REGISTRATE.configLang(ServerConfig.SHADOW_KARAMBIT_INVIS_COOLDOWN, "Invisibility Cooldown");
        REGISTRATE.configLang(ServerConfig.SHADOW_KARAMBIT_INVIS_DURATION, "Invisibility Duration");
        REGISTRATE.configLang(ServerConfig.SHADOW_KARAMBIT_INVIS_PARTICLES, "Invisibility Particles");
        REGISTRATE.configLang(ServerConfig.SHADOW_KARAMBIT_WITHER_CHANCE, "Wither Chance");
        REGISTRATE.configLang(ServerConfig.SHADOW_KARAMBIT_WITHER_DURATION, "Wither Duration");
        REGISTRATE.configLang("kingsWill", "King's Will");
        REGISTRATE.configLang(ServerConfig.KINGS_WILL_COOLDOWN, "Cooldown");
        REGISTRATE.configLang("hexblade", "Hexblade");
        REGISTRATE.configLang("abyssalImpact", "Abyssal Impact");
        REGISTRATE.configLang("executionersBlade", "Executioner's Blade");
    }
}
