package by.ts.hmxy.client.renderer.entity;

import java.awt.Color;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.entity.MinbusOrb;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MinbusOrbRenderer extends EntityRenderer<MinbusOrb> {
	private static final ResourceLocation MINBUS_ORB_LOCATION = HmxyMod.modLoc("textures/entity/minbus_orb.png");
	private static final RenderType RENDER_TYPE = RenderType.itemEntityTranslucentCull(MINBUS_ORB_LOCATION);
	private static final Color[] COLORS= {
			new Color(0xe8fafa),
			new Color(0xc0e6ef),
			new Color(0xca9d8e8),
			new Color(0x95c1df),
			new Color(0x6f9bd1),
			new Color(0x5985cf),
			new Color(0x597ccf),
			new Color(0x3c51ca),
			new Color(0x755cca),
			new Color(0x755cca),
			new Color(0xdcb3e6),
			new Color(0xdcb3e6),
			new Color(0xede5f0),
			new Color(0xecebf2),
			new Color(0xebf0f5)};
	public MinbusOrbRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius = 0.15F;
		this.shadowStrength = 0.75F;
	}

	protected int getBlockLightLevel(MinbusOrb pEntity, BlockPos pPos) {
		return Mth.clamp(super.getBlockLightLevel(pEntity, pPos) + 7, 0, 15);
	}

	public void render(MinbusOrb pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack,
			MultiBufferSource pBuffer, int pPackedLight) {
		pMatrixStack.pushPose();
		int i = pEntity.getIcon();
		float f = (float) (i % 4 * 16 + 0) / 64.0F;
		float f1 = (float) (i % 4 * 16 + 16) / 64.0F;
		float f2 = (float) (i / 4 * 16 + 0) / 64.0F;
		float f3 = (float) (i / 4 * 16 + 16) / 64.0F;
		int tick =  ((pEntity.tickCount +(int)pPartialTicks) / 2%COLORS.length);
		pMatrixStack.translate(0.0D, (double) 0.1F, 0.0D);
		pMatrixStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
		pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		pMatrixStack.scale(0.3F, 0.3F, 0.3F);
		VertexConsumer vertexconsumer = pBuffer.getBuffer(RENDER_TYPE);
		PoseStack.Pose posestack$pose = pMatrixStack.last();
		Matrix4f matrix4f = posestack$pose.pose();
		Matrix3f matrix3f = posestack$pose.normal();
		vertex(vertexconsumer, matrix4f, matrix3f, -0.5F, -0.25F,COLORS[tick].getRed(), COLORS[tick].getGreen(), COLORS[tick].getRed(), f, f3, pPackedLight);
		vertex(vertexconsumer, matrix4f, matrix3f, 0.5F, -0.25F, COLORS[tick].getRed(), COLORS[tick].getGreen(), COLORS[tick].getRed(), f1, f3, pPackedLight);
		vertex(vertexconsumer, matrix4f, matrix3f, 0.5F, 0.75F,COLORS[tick].getRed(), COLORS[tick].getGreen(), COLORS[tick].getRed(), f1, f2, pPackedLight);
		vertex(vertexconsumer, matrix4f, matrix3f, -0.5F, 0.75F, COLORS[tick].getRed(), COLORS[tick].getGreen(), COLORS[tick].getRed(), f, f2, pPackedLight);
		pMatrixStack.popPose();
		super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
	}

	private static void vertex(VertexConsumer pBuffer, Matrix4f pMatrix, Matrix3f pMatrixNormal, float pX, float pY,
			int pRed, int pGreen, int pBlue, float pTexU, float pTexV, int pPackedLight) {
		pBuffer.vertex(pMatrix, pX, pY, 0.0F).color(pRed, pGreen, pBlue, 200).uv(pTexU, pTexV)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(pPackedLight).normal(pMatrixNormal, 0.0F, 1.0F, 0.0F)
				.endVertex();
	}

	public ResourceLocation getTextureLocation(MinbusOrb pEntity) {
		return MINBUS_ORB_LOCATION;
	}
}