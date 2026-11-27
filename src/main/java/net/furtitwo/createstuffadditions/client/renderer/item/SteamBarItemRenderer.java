package net.furtitwo.createstuffadditions.client.renderer.item;

import org.joml.Matrix4f;

import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.capabilities.Capabilities;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.Minecraft;

import net.furtitwo.createstuffadditions.network.CreateSaModVariables;

import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class SteamBarItemRenderer extends CustomRenderedItemModelRenderer {
	public void renderItem(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		render(stack, model, renderer, transformType, ms, buffer, light, overlay);
	}

	public static void renderBars(ItemStack stack, PoseStack ms, MultiBufferSource buffer, int light) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		float fuel = 0f;
		float water = 0f;
		IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
		if (handler != null) {
			fuel = Math.round(handler.getFluidInTank(0).getAmount() * 0.1f);
			water = Math.round(handler.getFluidInTank(1).getAmount() * 0.1f);
		}
		float max = (float) CreateSaModVariables.MapVariables.get(player.level()).gadgetCapacity;
		float fuelRatio = Mth.clamp(fuel / max, 0f, 1f);
		float waterRatio = Mth.clamp(water / max, 0f, 1f);
		ms.pushPose();
		// move in front of item 
		ms.translate(0, 0, 1f);
		ms.scale(0.0625f, 0.0625f, -0.0625f);
		drawVanillaBar(ms, buffer, -6f, -5f, fuelRatio, 16099696);
		drawVanillaBar(ms, buffer, -6f, -7f, waterRatio, 8361684);
		ms.popPose();
	}

	@Override
	protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		Minecraft mc = Minecraft.getInstance();
		ItemRenderer itemRenderer = mc.getItemRenderer();
		renderer.render(model.getOriginalModel(), light);
		if (transformType == ItemDisplayContext.GUI)
			renderBars(stack, ms, buffer, light);
	}

	private static void drawVanillaBar(PoseStack poseStack, MultiBufferSource buffer, float x, float y, float ratio, int color) {
		ratio = Mth.clamp(ratio, 0.0F, 1.0F);
		// Vanilla bar size
		float width = 13.0F;
		float height = 2.0F;
		// Filled width
		float filled = Mth.floor(width * ratio);
		Matrix4f matrix = poseStack.last().pose();
		VertexConsumer vc = buffer.getBuffer(RenderType.debugQuads());
		// Background (black)
		addQuad(vc, matrix, x, y, x + width, y + height, 0, 0, 0, 0, 255);
		// Foreground
		int r = (color >> 16) & 255;
		int g = (color >> 8) & 255;
		int b = color & 255;
		addQuad(vc, matrix, x, y + height * 0.5f, x + filled, y + height, -0.5f, // foreground slightly in front
				r, g, b, 255);
	}

	private static void addQuad(VertexConsumer vc, Matrix4f matrix, float x1, float y1, float x2, float y2, float z, int r, int g, int b, int a) {
		vc.addVertex(matrix, x1, y1, z).setColor(r, g, b, a);
		vc.addVertex(matrix, x2, y1, z).setColor(r, g, b, a);
		vc.addVertex(matrix, x2, y2, z).setColor(r, g, b, a);
		vc.addVertex(matrix, x1, y2, z).setColor(r, g, b, a);
	}
}
