package net.furtitwo.createstuffadditions.logic.drone;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

/**
 * Drone controller: right click toggles the action state (with a one tick pulse when switched on),
 * the state and the reach are reset when the controller is no longer held.
 */
public final class DroneControllerBehaviour {
	private static final String ACTION = "tagAction";
	private static final String PULSE_ACTION = "tagPulseAction";
	private static final String REACH = "tagReach";

	private DroneControllerBehaviour() {
	}

	public static void onUse(Level level, Entity user, ItemStack controller) {
		if (readTag(controller).getBoolean(ACTION)) {
			CustomData.update(DataComponents.CUSTOM_DATA, controller, tag -> tag.putBoolean(ACTION, false));
			DroneCommon.playSound(level, user.getX(), user.getY(), user.getZ(), SoundEvents.STONE_BUTTON_CLICK_OFF, 0.25f, -2);
		} else {
			CustomData.update(DataComponents.CUSTOM_DATA, controller, tag -> {
				tag.putBoolean(PULSE_ACTION, true);
				tag.putBoolean(ACTION, true);
			});
			DroneCommon.playSound(level, user.getX(), user.getY(), user.getZ(), SoundEvents.STONE_BUTTON_CLICK_ON, 0.25f, -2);
		}
	}

	public static void onInventoryTick(Entity holder, ItemStack controller) {
		Item item = controller.getItem();
		if (item != DroneCommon.mainHand(holder).getItem() && item != DroneCommon.offHand(holder).getItem()) {
			CustomData.update(DataComponents.CUSTOM_DATA, controller, tag -> {
				tag.putBoolean(ACTION, false);
				tag.putDouble(REACH, 0);
			});
		}
		if (readTag(controller).getBoolean(PULSE_ACTION))
			CustomData.update(DataComponents.CUSTOM_DATA, controller, tag -> tag.putBoolean(PULSE_ACTION, false));
	}

	private static CompoundTag readTag(ItemStack stack) {
		return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
	}
}
