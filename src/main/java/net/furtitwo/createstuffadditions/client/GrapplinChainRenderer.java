package net.furtitwo.createstuffadditions.client;

import net.furtitwo.createstuffadditions.compat.sable.SubLevels;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import net.furtitwo.createstuffadditions.fuel.FuelStorage;
import net.furtitwo.createstuffadditions.init.CreateSaModItems;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

/**
 * Draws the Grapplin Whisk chain: for every entity holding a hooked, fuelled whisk, a line of chain items (two per block)
 * from just in front of its eyes to the hook position stored on the stack. Enchanted whisks show glinting chains.
 */
@EventBusSubscriber(value = Dist.CLIENT)
public final class GrapplinChainRenderer {
	private static final ResourceKey<Enchantment> IMPACT = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("create_sa:impact"));

	private GrapplinChainRenderer() {
	}

	@SubscribeEvent
	public static void onRenderLevelStage(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES)
			return;
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		ClientLevel level = Minecraft.getInstance().level;
		if (level != null)
			renderChains(event, level, event.getPartialTick().getGameTimeDeltaPartialTick(false));
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableBlend();
		RenderSystem.enableCull();
		RenderSystem.enableDepthTest();
		RenderSystem.depthMask(true);
	}

	private static void renderChains(RenderLevelStageEvent event, ClientLevel level, float partialTick) {
		for (Entity entity : level.entitiesForRendering()) {
			if (!(entity instanceof LivingEntity living))
				continue;
			ItemStack main = living.getMainHandItem();
			ItemStack off = living.getOffhandItem();
			if (isHookedWhisk(main))
				renderChain(event, level, living, main, partialTick);
			else if (isHookedWhisk(off))
				renderChain(event, level, living, off, partialTick);
		}
	}

	private static boolean isHookedWhisk(ItemStack stack) {
		return stack.getItem() == CreateSaModItems.GRAPPLIN_WHISK.get() && stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("tagHooked") && FuelStorage.amount(stack, 0) > 0;
	}

	private static void renderChain(RenderLevelStageEvent event, ClientLevel level, LivingEntity holder, ItemStack whisk, float partialTick) {
		Vec3 position = holder.getPosition(partialTick);
		Vec3 view = holder.getViewVector(partialTick);
		double startX = position.x() + view.x() * 1.25;
		double startY = holder.getBbHeight() * 0.75 + position.y() + view.y() * 1.25;
		double startZ = position.z() + view.z() * 1.25;

		CompoundTag tag = whisk.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		Vec3 hook = SubLevels.toWorld(holder.level(), new Vec3(tag.getDouble("xPostion") + 0.5, tag.getDouble("yPostion") + 0.5, tag.getDouble("zPostion") + 0.5));
		double hookX = hook.x;
		double hookY = hook.y;
		double hookZ = hook.z;

		double distance = Math.sqrt(Math.pow(startX - hookX, 2) + Math.pow(startY - hookY, 2) + Math.pow(startZ - hookZ, 2));
		double divisor = distance == 0 ? 0.01 : distance;
		double dx = hookX - startX;
		double dy = hookY - startY;
		double dz = hookZ - startZ;
		float yaw = (float) (Math.atan2(dx, dz) * (-180 / Math.PI) + 180);
		float pitch = (float) (Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)) * (180 / Math.PI));

		ItemStack chain = new ItemStack(CreateSaModItems.CHAINS.get());
		if (whisk.isEnchanted())
			chain.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(IMPACT), 1);

		int links = (int) (Math.ceil(distance) * 2);
		double offset = 0;
		for (int i = 0; i < links; i++) {
			renderItem(event, level, chain, startX + ((startX - hookX) / divisor) * offset * (-1), startY + ((startY - hookY) / divisor) * offset * (-1),
					startZ + ((startZ - hookZ) / divisor) * offset * (-1), yaw, pitch);
			offset += 0.5;
		}
	}

	private static void renderItem(RenderLevelStageEvent event, ClientLevel level, ItemStack stack, double x, double y, double z, float yaw, float pitch) {
		Minecraft minecraft = Minecraft.getInstance();
		MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
		Vec3 camera = event.getCamera().getPosition();
		int packedLight = LevelRenderer.getLightColor(level, BlockPos.containing(x, y, z));
		PoseStack poseStack = event.getPoseStack();
		poseStack.pushPose();
		poseStack.translate(x - camera.x(), y - camera.y(), z - camera.z());
		poseStack.mulPose(Axis.YN.rotationDegrees(yaw));
		poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getItemRenderer().renderStatic(null, stack, ItemDisplayContext.FIXED, false, poseStack, bufferSource, level, packedLight, OverlayTexture.NO_OVERLAY, 0);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		poseStack.popPose();
	}
}
