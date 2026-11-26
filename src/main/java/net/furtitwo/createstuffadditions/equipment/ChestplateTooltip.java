package net.furtitwo.createstuffadditions.equipment;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.furtitwo.createstuffadditions.fuel.FuelStorage;
import net.furtitwo.createstuffadditions.fuel.FuelType;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Client-only tooltip layout shared by the powered chestplates (kept out of the item class so servers never load Screen). */
final class ChestplateTooltip {
	private static final String GRAY = "§7";
	private static final String WHITE = "§f";

	private ChestplateTooltip() {
	}

	static void append(FuelableChestplateItem item, ItemStack stack, @Nullable Level level, List<Component> list) {
		boolean shift = Screen.hasShiftDown();
		boolean ctrl = item.hasControlSection() && !shift && Screen.hasControlDown();
		list.add(highlight("item_desc.holdshift", shift));
		if (item.hasControlSection())
			list.add(highlight("item_desc.holdctrl", ctrl));
		appendFuelLines(item.getFuelType(), stack, (int) Math.round(item.getDisplayCapacity(level)), list);
		if (shift) {
			list.add(Component.literal(" "));
			list.addAll(FuelableChestplateItem.wrap("item_desc." + item.getDescKey() + ".desc", 0));
			item.appendShiftLines(level, list);
		} else if (ctrl) {
			list.add(Component.literal(" "));
			item.appendControlLines(level, list);
		}
	}

	static void appendFuelLines(FuelType type, ItemStack stack, int max, List<Component> list) {
		for (int i = 0; i < type.tankCount(); i++) {
			int stored = Math.round(FuelStorage.amount(stack, i) / (float) FuelStorage.MB_PER_UNIT);
			list.add(Component.literal(Component.translatable(type.tank(i).tooltipKey).getString() + " §8" + stored + "/" + max));
		}
	}

	private static Component highlight(String key, boolean active) {
		return active ? Component.literal(Component.translatable(key).getString().replace(GRAY, WHITE)) : Component.translatable(key);
	}
}
