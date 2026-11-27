package net.furtitwo.createstuffadditions.client.equipment;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.furtitwo.createstuffadditions.item.armor.FuelableChestplateItem;
import net.furtitwo.createstuffadditions.fuel.FuelStorage;

import net.createmod.catnip.animation.AnimationTickHolder;

import com.simibubi.create.foundation.mixin.accessor.EntityRenderDispatcherAccessor;

import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Single render layer for every powered chestplate (replaces 7 per-item layers), modelled on Create's {@code BacktankArmorLayer}.
 */
public class FuelableChestplateLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
	/** Rotate during flight, in degrees per tick. */
	private static final float FLYING_SPIN = 15;

	public FuelableChestplateLayer(RenderLayerParent<T, M> parent) {
		super(parent);
	}

	@Override
	public void render(PoseStack ms, MultiBufferSource buffer, int light, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
		if (entity.getPose() == Pose.SLEEPING || !(getParentModel() instanceof HumanoidModel<?> model))
			return;
		ItemStack stack = entity.getItemBySlot(EquipmentSlot.CHEST);
		if (!(stack.getItem() instanceof FuelableChestplateItem item))
			return;
		ChestplateVisual visual = ChestplateVisuals.get(item);
		if (visual == null) return;

		boolean fueled = item.hasFuel(stack);
		boolean flying = fueled && FuelStorage.isFlying(stack);
		float spin = flying ? FLYING_SPIN : idleSpin(item, stack, entity);

		ms.pushPose();
		model.body.translateAndRotate(ms);
		ms.translate(-1 / 2f, 10 / 16f, 1f);
		ms.scale(1, -1, -1);
		visual.back().render(new ChestplateVisual.Frame(entity, stack, model, ms, buffer.getBuffer(Sheets.cutoutBlockSheet()), light, AnimationTickHolder.getRenderTime(entity.level()), flying, spin));
		ms.popPose();
	}

	/** Cog's idle speed scale on how much the armor item has fuel (0 to -8.5 degrees per tick). */
	private static float idleSpin(FuelableChestplateItem item, ItemStack stack, LivingEntity entity) {
		return -0.5f - 8f * FuelStorage.fillRatio(stack, item.getFuelType(), item.getDisplayCapacity(entity.level()));
	}

	public static void registerOnAll(EntityRenderDispatcher dispatcher) {
		for (EntityRenderer<? extends Player> renderer : dispatcher.getSkinMap().values())
			registerOn(renderer);
		for (EntityRenderer<?> renderer : ((EntityRenderDispatcherAccessor) dispatcher).create$getRenderers().values())
			registerOn(renderer);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static void registerOn(EntityRenderer<?> renderer) {
		if (renderer instanceof LivingEntityRenderer living && living.getModel() instanceof HumanoidModel)
			living.addLayer(new FuelableChestplateLayer<>(living));
	}
}
