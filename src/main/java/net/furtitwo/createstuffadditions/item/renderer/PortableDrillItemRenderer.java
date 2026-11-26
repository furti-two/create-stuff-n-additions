package net.furtitwo.createstuffadditions.item.renderer;

import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.capabilities.Capabilities;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.Minecraft;

import net.furtitwo.createstuffadditions.network.CreateSaModVariables;

import net.createmod.catnip.animation.AnimationTickHolder;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;

import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.CreateClient;

import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.PoseStack;

public class PortableDrillItemRenderer extends CustomRenderedItemModelRenderer {
	protected static final PartialModel COG = PartialModel.of(ResourceLocation.parse("create_sa:item_renderer/portable_drill_cog"));
	protected static final PartialModel HEAD = PartialModel.of(ResourceLocation.parse("create_sa:item_renderer/portable_drill_head"));

	@Override
	protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		Minecraft mc = Minecraft.getInstance();
		ItemRenderer itemRenderer = mc.getItemRenderer();
		// base model
		renderer.render(model.getOriginalModel(), light);
		// drill animation
		LocalPlayer player = mc.player;
		boolean mainHand = player.getMainHandItem() == stack;
		boolean offHand = player.getOffhandItem() == stack;
		boolean leftHanded = player.getMainArm() == HumanoidArm.LEFT;
		float offset = -0.125f;
		float worldTime = AnimationTickHolder.getRenderTime() / 10;
		float angle = worldTime * -12.5f;
		IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
		int fuel = 0;
		int water = 0;
		if (handler != null) {
			fuel = Math.round(handler.getFluidInTank(0).getAmount() * 0.1f);
			water = Math.round(handler.getFluidInTank(1).getAmount() * 0.1f);
		}
		if (fuel > 0 && water > 0)
			angle = worldTime * (-12.5f + (float) Math.round(((fuel + water) / (CreateSaModVariables.MapVariables.get(player.level()).gadgetCapacity)) * -15) * 4f);
		float speed = CreateClient.POTATO_CANNON_RENDER_HANDLER.getAnimation(mainHand ^ leftHanded, AnimationTickHolder.getPartialTicks());
		if (mainHand || offHand)
			angle += 360 * Mth.clamp(speed * 5, 0, 1);
		angle %= 360;
		ms.pushPose();
		ms.translate(0, offset, 0);
		ms.mulPose(Axis.ZP.rotationDegrees(angle));
		ms.translate(0, -offset, 0);
		renderer.render(COG.get(), light);
		ms.translate(0, offset, 0);
		ms.mulPose(Axis.ZP.rotationDegrees(angle * 1.5f));
		ms.translate(0, -offset, 0);
		renderer.render(HEAD.get(), light);
		ms.popPose();
		if (transformType == ItemDisplayContext.GUI) {
			ms.pushPose();
			ms.mulPose(Axis.ZP.rotationDegrees(-16f));
			ms.mulPose(Axis.YP.rotationDegrees(-133f));
			ms.mulPose(Axis.XP.rotationDegrees(-23f));
			ms.translate(-0.14f, -0.065f, 0f);
			SteamBarItemRenderer.renderBars(stack, ms, buffer, light);
			ms.popPose();
		}
	}
}
