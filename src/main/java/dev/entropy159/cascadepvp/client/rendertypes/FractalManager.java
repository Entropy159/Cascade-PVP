package dev.entropy159.cascadepvp.client.rendertypes;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.entropy159.cascadepvp.CascadePVP;
import dev.entropy159.cascadepvp.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public class FractalManager {
    public static final int TYPES = 4;
    private static final RenderTarget[] fractalTargets = new RenderTarget[TYPES];

    public static void init() {
        for (int type = 0; type < TYPES; type++) {
            init(type);
        }
    }

    public static void init(int type) {
        var target = fractalTargets[type];
        if (target != null) {
            target.destroyBuffers();
        }
        int resolution = ClientConfig.REALITY_TEAR_RESOLUTION.get();
        target = new TextureTarget(resolution, resolution, true, Minecraft.ON_OSX);
        target.setClearColor(0, 0, 0, 0);
        fractalTargets[type] = target;
    }

    public static RenderTarget getTarget(int type) {
        if (fractalTargets[type] == null) {
            init(type);
        }
        return fractalTargets[type];
    }

    public static void renderFractals(float partialTick) {
        for (int type = 0; type < TYPES; type++) {
            renderFractalToTexture(type, partialTick);
        }
    }

    public static void renderFractalToTexture(int type, float partialTick) {
        var fractalTarget = getTarget(type);

        int resolution = ClientConfig.REALITY_TEAR_RESOLUTION.get();
        if (resolution != fractalTarget.width) {
            fractalTarget.resize(resolution, resolution, Minecraft.ON_OSX);
        }
        RenderSystem.viewport(0, 0, fractalTarget.width, fractalTarget.height);
        fractalTarget.clear(Minecraft.ON_OSX);
        fractalTarget.bindWrite(true);

        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();
        modelViewStack.identity();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(new Matrix4f().identity(), VertexSorting.ORTHOGRAPHIC_Z);

        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();

        var shader = CascadeRenderTypes.REALITY_TEAR_SHADER;
        RenderSystem.setShader(() -> shader);
        if (Minecraft.getInstance().level != null) {
            float timeInSeconds = (Minecraft.getInstance().level.getGameTime() + partialTick) / 20.0f;

            var uniform = shader.getUniform("GameTime");
            if (uniform != null) {
                uniform.set(timeInSeconds);
            }
        }

        float alpha = (float) type / TYPES;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, CascadeRenderTypes.REALITY_TEAR_FORMAT);
        builder.addVertex(-1, -1, 0).setColor(1f, 1f, 1f, alpha).setUv(0, 0);
        builder.addVertex(1, -1, 0).setColor(1f, 1f, 1f, alpha).setUv(1, 0);
        builder.addVertex(1, 1, 0).setColor(1f, 1f, 1f, alpha).setUv(1, 1);
        builder.addVertex(-1, 1, 0).setColor(1f, 1f, 1f, alpha).setUv(0, 1);
        MeshData meshData = builder.buildOrThrow();
        BufferUploader.drawWithShader(meshData);

        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();

        RenderSystem.restoreProjectionMatrix();
        modelViewStack.popMatrix();
        RenderSystem.applyModelViewMatrix();

        fractalTarget.unbindWrite();
        var mainTarget = Minecraft.getInstance().getMainRenderTarget();
        mainTarget.bindWrite(true);
        RenderSystem.viewport(0, 0, mainTarget.width, mainTarget.height);
    }

    public static void registerDynamicTextures() {
        for (int type = 0; type < TYPES; type++) {
            Minecraft.getInstance().getTextureManager().register(getTexture(type), new RenderTargetTexture(type));
        }
    }

    public static ResourceLocation getTexture(int type) {
        return CascadePVP.id("textures/fractal_fbo_" + type + ".png");
    }

    public static class RenderTargetTexture extends AbstractTexture {
        private final int type;

        public RenderTargetTexture(int type) {
            this.type = type;
        }

        @Override
        public void load(@NotNull ResourceManager resourceManager) {
        }

        @Override
        public int getId() {
            var target = getTarget(type);
            return target == null ? 0 : target.getColorTextureId();
        }
    }
}
