package dev.entropy159.cascadepvp.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.entropy159.cascadepvp.CascadePVP;
import dev.entropy159.cascadepvp.client.rendertypes.CascadeRenderTypes;
import dev.entropy159.cascadepvp.items.CascadeItem;
import dev.entropy159.cascadepvp.network.toServer.AbilityPacket;
import dev.entropy159.cascadepvp.registry.CascadeItems;
import dev.entropy159.cascadepvp.registry.CascadePotions;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

@Mod(value = CascadePVP.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = CascadePVP.MODID, value = Dist.CLIENT)
public class CascadePVPClient {
    private static final ClampedItemPropertyFunction BOW_PULL = (stack, level, entity, seed) -> {
        if (entity == null) {
            return 0;
        }
        return entity.getUseItem() != stack ? 0 : stack.getUseDuration(entity) - entity.getUseItemRemainingTicks() / 20f;
    };
    private static final ClampedItemPropertyFunction BOW_PULLING = (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1 : 0;

    public static final Lazy<KeyMapping> ABILITY = Lazy.of(() -> new KeyMapping("key.cascadepvp.ability", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.categories.gameplay"));
    public static final Lazy<KeyMapping> TOGGLE_SCOPE = Lazy.of(() -> new KeyMapping("key.cascadepvp.toggle_scope", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z, "key.categories.gameplay"));

    public CascadePVPClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        ItemProperties.register(CascadeItems.BOOMBOW.get(), ResourceLocation.withDefaultNamespace("pull"), BOW_PULL);
        ItemProperties.register(CascadeItems.BOOMBOW.get(), ResourceLocation.withDefaultNamespace("pulling"), BOW_PULLING);
        ItemProperties.register(CascadeItems.BOW_OF_THE_GALADHRIM.get(), ResourceLocation.withDefaultNamespace("pull"), BOW_PULL);
        ItemProperties.register(CascadeItems.BOW_OF_THE_GALADHRIM.get(), ResourceLocation.withDefaultNamespace("pulling"), BOW_PULLING);
    }

    @SubscribeEvent
    public static void keybinds(RegisterKeyMappingsEvent event) {
        event.register(ABILITY.get());
        event.register(TOGGLE_SCOPE.get());
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        while (ABILITY.get().consumeClick()) {
            var player = Minecraft.getInstance().player;
            if (player != null && player.getMainHandItem().getItem() instanceof CascadeItem item) {
                var entity = CascadePVP.getTargetedEntity(player, 250, e -> e instanceof LivingEntity living && item.isValidTarget(living));
                PacketDistributor.sendToServer(new AbilityPacket(entity == null ? -1 : entity.getId()));
            }
        }
        while (TOGGLE_SCOPE.get().consumeClick()) {
            var player = Minecraft.getInstance().player;
            if (player != null) {
                ClientData.SCOPE_ENABLED = !ClientData.SCOPE_ENABLED;
                player.sendSystemMessage(Component.literal(ClientData.SCOPE_ENABLED ? "Enabled scope" : "Disabled scope").withStyle(ChatFormatting.YELLOW));
            }
        }
    }

    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasEffect(CascadePotions.INVERSE_INVISIBILITY)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void renderTypes(RegisterShadersEvent event) {
        try {
            CascadeRenderTypes.registerShaders(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SubscribeEvent
    public static void onLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        ClientData.SEED = null;
    }
}
