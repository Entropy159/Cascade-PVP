package dev.entropy159.cascadepvp.client.rendertypes;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.entropy159.cascadepvp.CascadePVP;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

public final class CascadeRenderTypes {
    public static ShaderInstance REALITY_TEAR_SHADER;
    public static final RenderType REALITY_TEAR = createRealityTear();
    public static final VertexFormat REALITY_TEAR_FORMAT = DefaultVertexFormat.POSITION_TEX_COLOR;

    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), CascadePVP.id("reality_tear"), REALITY_TEAR_FORMAT), shader -> REALITY_TEAR_SHADER = shader);
    }

    private static RenderType createRealityTear() {
        RenderType.CompositeState state = RenderType.CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard(() -> REALITY_TEAR_SHADER)).setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).setDepthTestState(RenderStateShard.NO_DEPTH_TEST).setLightmapState(RenderStateShard.NO_LIGHTMAP).setOverlayState(RenderStateShard.NO_OVERLAY).createCompositeState(false);
        assert REALITY_TEAR_FORMAT != null;
        return RenderType.create("reality_tear", REALITY_TEAR_FORMAT, VertexFormat.Mode.TRIANGLE_STRIP, 256, state);
    }
}
