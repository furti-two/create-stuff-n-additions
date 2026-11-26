package net.furtitwo.createstuffadditions.events;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.furtitwo.createstuffadditions.client.equipment.ArmorModelCache;
import net.furtitwo.createstuffadditions.client.equipment.ChestplateVisual;
import net.furtitwo.createstuffadditions.client.equipment.ChestplateVisuals;
import net.furtitwo.createstuffadditions.client.equipment.FuelableChestplateLayer;
import net.furtitwo.createstuffadditions.client.equipment.SteamBarDecorator;
import net.furtitwo.createstuffadditions.fuel.FuelableItem;

import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;

import net.minecraft.core.registries.BuiltInRegistries;

import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
	/** Chestplate worn by the local player when it replaces the first-person arm, refreshed every client tick. */
	@Nullable
	private static Item firstPersonItem;

	@SubscribeEvent
	public static void onTickPost(ClientTickEvent.Post event) {
		Minecraft mc = Minecraft.getInstance();
		firstPersonItem = null;
		if (mc.level == null || mc.player == null)
			return;
		Item chest = mc.player.getItemBySlot(EquipmentSlot.CHEST).getItem();
		ChestplateVisual visual = ChestplateVisuals.get(chest);
		if (visual != null && visual.firstPerson() != null)
			firstPersonItem = chest;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onRenderArm(RenderArmEvent event) {
		Item item = firstPersonItem;
		if (item == null)
			return;
		ChestplateVisual.FirstPerson spec = ChestplateVisuals.get(item).firstPerson();
		ChestplateVisual.Parts parts = ArmorModelCache.firstPerson(item, spec);
		ModelPart arm = event.getArm() == HumanoidArm.LEFT ? parts.leftArm() : parts.rightArm();
		arm.xRot = 0;
		arm.render(event.getPoseStack(), event.getMultiBufferSource().getBuffer(RenderType.entityCutoutNoCull(spec.texture())), event.getPackedLight(), OverlayTexture.NO_OVERLAY);
		event.setCanceled(true);
	}

	@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
	public static class ModBusEvents {
		@SubscribeEvent
		public static void addEntityRendererLayers(EntityRenderersEvent.AddLayers event) {
			ArmorModelCache.clear();
			FuelableChestplateLayer.registerOnAll(Minecraft.getInstance().getEntityRenderDispatcher());
		}

		@SubscribeEvent
		public static void registerItemDecorations(RegisterItemDecorationsEvent event) {
			for (Item item : BuiltInRegistries.ITEM)
				if (item instanceof FuelableItem fuelable && fuelable.getFuelType().tankCount() > 1)
					event.register(item, SteamBarDecorator.INSTANCE);
		}

		@SubscribeEvent
		public static void registerItemExtensions(RegisterClientExtensionsEvent event) {
			ChestplateVisuals.all().forEach((item, visual) -> event.registerItem(new IClientItemExtensions() {
				@Override
				public HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
					return ArmorModelCache.armor(item, visual);
				}
			}, item));
		}
	}
}
