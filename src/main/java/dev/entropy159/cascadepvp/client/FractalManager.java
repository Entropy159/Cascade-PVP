package dev.entropy159.cascadepvp.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.entropy159.cascadepvp.CascadePVP;
import dev.entropy159.cascadepvp.config.ClientConfig;
import dev.entropy159.entropylib.client.util.render.CustomRenderType;
import dev.entropy159.entropylib.client.util.render.FBOManager;
import dev.entropy159.entropylib.client.util.render.RenderTypeUtil;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class FractalManager {
    public static final int TYPES = 4;
    public static final CustomRenderType REALITY_TEAR_TYPE = RenderTypeUtil.create(CascadePVP.id("reality_tear"), DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.TRIANGLE_STRIP, builder -> builder.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST).setLightmapState(RenderStateShard.NO_LIGHTMAP).setOverlayState(RenderStateShard.NO_OVERLAY));
    public static final List<FBOManager.FBO> FBOS = new ArrayList<>();

    public static void init() {
        int size = ClientConfig.REALITY_TEAR_RESOLUTION.getDefault();
        for (int type = 0; type < TYPES; type++) {
            float styleNum = type;
            var fbo = FBOManager.create(getTexture(type), () -> REALITY_TEAR_TYPE, size, size);
            fbo.setUniforms(shader -> {
                var style = shader.getUniform("TearStyle");
                if (style != null) {
                    style.set(styleNum);
                }
            });
            FBOS.add(fbo);
        }
    }

    public static ResourceLocation getTexture(int type) {
        return CascadePVP.id("textures/fractal_fbo_" + type + ".png");
    }
}
