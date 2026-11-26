package net.furtitwo.createstuffadditions.item.renderer;

import net.neoforged.neoforge.capabilities.Capabilities;

import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.component.DataComponents;
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

public class GrapplinWhiskItemRenderer extends CustomRenderedItemModelRenderer {
	protected static final PartialModel COG = PartialModel.of(ResourceLocation.parse("create_sa:item_renderer/portable_drill_cog"));
	protected static final PartialModel HOOK = PartialModel.of(ResourceLocation.parse("create_sa:item_renderer/grapplin_whisk_hook"));

	@Override
	protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		Minecraft mc = Minecraft.getInstance();
		ItemRenderer itemRenderer = mc.getItemRenderer();
		renderer.render(model.getOriginalModel(), light);
		LocalPlayer player = mc.player;
		boolean mainHand = player.getMainHandItem() == stack;
		boolean offHand = player.getOffhandItem() == stack;
		boolean leftHanded = player.getMainArm() == HumanoidArm.LEFT;
		float offset = -0.125f;
		float worldTime = AnimationTickHolder.getRenderTime() / 10;
		float angle = worldTime * -12.5f;
		if (stack.getCapability(Capabilities.FluidHandler.ITEM).getFluidInTank(0).getAmount() > 0) {
			angle = worldTime * (-12.5f + (float) Math.round((stack.getCapability(Capabilities.FluidHandler.ITEM).getFluidInTank(0).getAmount() / (CreateSaModVariables.MapVariables.get(player.level()).gadgetCapacity * 5f)) * -15) * 4f);
			if (stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("tagHooked"))
				angle = worldTime * 140.0f;
		}
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
		if (stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("tagHooked"))
			ms.scale(0.0f, 0.0f, 0.0f);
		ms.mulPose(Axis.ZP.rotationDegrees(-angle));
		ms.translate(0, -offset, 0);
		renderer.render(HOOK.get(), light);
		ms.popPose();
	}
}
