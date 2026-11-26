package net.furtitwo.createstuffadditions.logic.control;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import net.furtitwo.createstuffadditions.equipment.JetpackItem;
import net.furtitwo.createstuffadditions.init.CreateSaModItems;

/** Effects of the mod key bindings, run by the key messages on the server and, through their pressAction, on the client. */
public final class KeyActions {
	private static final TagKey<Item> JETPACKS = ItemTags.create(ResourceLocation.parse("create_sa:jetpack"));
	/** Drone controller reach, a double in the stack's custom data. */
	private static final String REACH_KEY = "tagReach";
	private static final double MAX_REACH = 28;
	private static final double MIN_REACH = 0;

	private KeyActions() {
	}

	/** Flight key pressed / released: stores the flight wish while a jetpack is worn. */
	public static void onFlyKey(Player player, boolean pressed) {
		if (player.getItemBySlot(EquipmentSlot.CHEST).is(JETPACKS))
			player.getPersistentData().putBoolean(JetpackItem.FLYING_KEY, pressed);
	}

	/** Reach keys: moves the reach of the drone controller held in the main hand, else in the off hand, by one block within 0..28. */
	public static void onReachKey(Player player, boolean increase) {
		ItemStack controller = player.getMainHandItem();
		if (controller.getItem() != CreateSaModItems.DRONE_CONTROLLER.get()) {
			controller = player.getOffhandItem();
			if (controller.getItem() != CreateSaModItems.DRONE_CONTROLLER.get())
				return;
		}
		double reach = controller.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getDouble(REACH_KEY);
		if (increase ? reach >= MAX_REACH : reach <= MIN_REACH)
			return;
		double newReach = reach + (increase ? 1 : -1);
		CustomData.update(DataComponents.CUSTOM_DATA, controller, tag -> tag.putDouble(REACH_KEY, newReach));
	}
}
