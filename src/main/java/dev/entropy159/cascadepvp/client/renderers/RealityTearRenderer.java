package dev.entropy159.cascadepvp.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.entropy159.cascadepvp.client.rendertypes.FractalManager;
import dev.entropy159.cascadepvp.entities.RealityTearEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class RealityTearRenderer extends EntityRenderer<RealityTearEntity> {
    public RealityTearRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull RealityTearEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, entity.getBbHeight() / 2d, 0);
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        poseStack.scale(1, 1, 1);

        RenderType renderType = RenderType.entityTranslucent(getTextureLocation(entity));
        var consumer = bufferSource.getBuffer(renderType);
        var pose = poseStack.last().pose();
        float size = 0.5f;

        int color = entity.getColor();
        float red = FastColor.ARGB32.red(color) / 255f;
        float green = FastColor.ARGB32.green(color) / 255f;
        float blue = FastColor.ARGB32.blue(color) / 255f;

        consumer.addVertex(pose, -size, -size, 0).setColor(red, green, blue, 1).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(poseStack.last(), 0, 0, 1);
        consumer.addVertex(pose, size, -size, 0).setColor(red, green, blue, 1).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(poseStack.last(), 0, 0, 1);
        consumer.addVertex(pose, size, size, 0).setColor(red, green, blue, 1).setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(poseStack.last(), 0, 0, 1);
        consumer.addVertex(pose, -size, size, 0).setColor(red, green, blue, 1).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(poseStack.last(), 0, 0, 1);

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull RealityTearEntity entity) {
        int type = new Random(entity.blockPosition().getX() * 73856093L ^ entity.blockPosition().getY() * 19349663L ^ entity.blockPosition().getZ() * 83492791L).nextInt(FractalManager.TYPES);
        return FractalManager.getTexture(type);
    }
}
