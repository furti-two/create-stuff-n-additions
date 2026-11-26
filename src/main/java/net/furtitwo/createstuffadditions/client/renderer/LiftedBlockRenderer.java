package net.furtitwo.createstuffadditions.client.renderer;

import org.jetbrains.annotations.NotNull;

import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.Minecraft;

import net.furtitwo.createstuffadditions.entity.LiftedBlockEntity;

import net.createmod.catnip.animation.AnimationTickHolder;

import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.PoseStack;

public class LiftedBlockRenderer extends HumanoidMobRenderer<LiftedBlockEntity, HumanoidModel<LiftedBlockEntity>> {
	public LiftedBlockRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0f);
		this.addLayer(new BlocksLayer<>(this));
	}

	@OnlyIn(Dist.CLIENT)
	public static class BlocksLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
		private static Vec3 smoothed = Vec3.ZERO;

		public BlocksLayer(RenderLayerParent<T, M> parent) {
			super(parent);
		}

		@Override
		public void render(@NotNull PoseStack pose, @NotNull MultiBufferSource source, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
			BlockState blockState = entity.getEntityData().get(LiftedBlockEntity.DATA_BLOCK).orElse(Blocks.AIR.defaultBlockState());
			if (blockState.isAir())
				return;
			BlockPos blockPos = BlockPos.containing(entity.getX(), entity.getY(), entity.getZ());
			pose.pushPose();
			pose.translate(0.0D, 1.5D, 0.0D);
			float t = AnimationTickHolder.getRenderTime();
			Vec3 mv = entity.getPosition(t).subtract(entity.getPosition(t - 1));
			double alpha = 0.15;
			smoothed = smoothed.lerp(mv, alpha);
			if (smoothed.lengthSqr() < 1e-6) {
				smoothed = Vec3.ZERO;
			}
			pose.mulPose(Axis.XP.rotationDegrees((float) smoothed.z * 25.0f));
			pose.mulPose(Axis.ZP.rotationDegrees((float) smoothed.x * 25.0f));
			pose.mulPose(Axis.XP.rotationDegrees(180));
			float scale = 1.0F;
			pose.scale(scale, scale, scale);
			pose.translate(-0.5F, 0.0F, -0.5F);
			renderBlockEntity(blockState, blockPos, pose, source, packedLight);
			if (entity instanceof LiftedBlockEntity liftedBlock) {
				renderBlockModel(liftedBlock, blockState, blockPos, pose, source, packedLight);
			}
			pose.popPose();
		}

		private void renderBlockEntity(BlockState blockState, BlockPos blockPos, PoseStack pose, MultiBufferSource source, int packedLight) {
			if (!(blockState.getBlock() instanceof EntityBlock entityBlock))
				return;
			Minecraft minecraft = Minecraft.getInstance();
			ClientLevel level = minecraft.level;
			if (level == null)
				return;
			BlockEntity blockEntity = entityBlock.newBlockEntity(blockPos, blockState);
			if (blockEntity == null)
				return;
			blockEntity.setLevel(level);
			BlockEntityRenderer<BlockEntity> renderer = minecraft.getBlockEntityRenderDispatcher().getRenderer(blockEntity);
			if (renderer == null)
				return;
			renderer.render(blockEntity, 0.0F, pose, source, packedLight, OverlayTexture.NO_OVERLAY);
		}

		private void renderBlockModel(LiftedBlockEntity liftedBlock, BlockState blockState, BlockPos blockPos, PoseStack pose, MultiBufferSource source, int packedLight) {
			if (blockState.getRenderShape() != RenderShape.MODEL)
				return;
			Minecraft minecraft = Minecraft.getInstance();
			if (minecraft.level == null)
				return;
			minecraft.getBlockRenderer().getModelRenderer().tesselateBlock(minecraft.level, minecraft.getBlockRenderer().getBlockModel(blockState), blockState, blockPos, pose, source.getBuffer(net.minecraft.client.renderer.RenderType.cutout()),
					false, minecraft.level.random, blockState.getSeed(liftedBlock.getStartPos()), OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
		}
	}

	@Override
	public ResourceLocation getTextureLocation(LiftedBlockEntity entity) {
		return ResourceLocation.parse("create_sa:textures/entities/invisible_layer_2.png");
	}
}
