package dev.entropy159.cascadepvp.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.entropy159.cascadepvp.CascadePVP;
import dev.entropy159.cascadepvp.client.rendertypes.CascadeRenderTypes;
import dev.entropy159.cascadepvp.entities.RealityTearEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class RealityTearRenderer extends EntityRenderer<RealityTearEntity> {
    private static final int STYLES = 4;

    public RealityTearRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull RealityTearEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, entity.getBbHeight() / 2d, 0);
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        poseStack.scale(1, 1, 1);
        var consumer = bufferSource.getBuffer(CascadeRenderTypes.REALITY_TEAR);
        var pose = poseStack.last().pose();
        float size = 0.5f;

        int styleNum = new Random(entity.blockPosition().getX() * 73856093L ^ entity.blockPosition().getY() * 19349663L ^ entity.blockPosition().getZ() * 83492791L).nextInt(STYLES);
        float style = (float) styleNum / STYLES;

        int color = entity.getColor();
        float red = FastColor.ARGB32.red(color) / 255f;
        float green = FastColor.ARGB32.green(color) / 255f;
        float blue = FastColor.ARGB32.blue(color) / 255f;

        consumer.addVertex(pose, -size, size, 0).setColor(red, green, blue, style).setUv(0, 0);
        consumer.addVertex(pose, -size, -size, 0.0F).setColor(red, green, blue, style).setUv(0, 1);
        consumer.addVertex(pose, size, size, 0.0F).setColor(red, green, blue, style).setUv(1, 0);
        consumer.addVertex(pose, size, -size, 0.0F).setColor(red, green, blue, style).setUv(1, 1);

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull RealityTearEntity entity) {
        return CascadePVP.id("textures/empty.png");
    }
}
