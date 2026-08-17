package dev.entropy159.cascadepvp.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.entropy159.cascadepvp.CascadePVP;
import dev.entropy159.cascadepvp.entities.projectile.MagicMissileProjectile;
import dev.entropy159.cascadepvp.items.weapon.hexblade.aspects.MagicMissileAspect;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class MagicMissileRenderer extends EntityRenderer<MagicMissileProjectile> {
    public MagicMissileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull MagicMissileProjectile entity, float yaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource source, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, 0.5D, 0);
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        poseStack.scale(1, 1, 1);
        var consumer = source.getBuffer(RenderType.entityTranslucentEmissive(getTextureLocation(entity)));
        var pose = poseStack.last().pose();
        float size = 1;

        int color = MagicMissileAspect.COLOR | 0xFF000000;

        consumer.addVertex(pose, -size, size, 0)
                .setColor(color)
                .setUv(0.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(poseStack.last(), 0.0F, 0.0F, 1.0F);

        consumer.addVertex(pose, -size, -size, 0.0F)
                .setColor(color)
                .setUv(0.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(poseStack.last(), 0.0F, 0.0F, 1.0F);

        consumer.addVertex(pose, size, -size, 0.0F)
                .setColor(color)
                .setUv(1.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(poseStack.last(), 0.0F, 0.0F, 1.0F);

        consumer.addVertex(pose, size, size, 0.0F)
                .setColor(color)
                .setUv(1.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(poseStack.last(), 0.0F, 0.0F, 1.0F);

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MagicMissileProjectile entity) {
        return CascadePVP.id("textures/entity/magic_missile.png");
    }
}
