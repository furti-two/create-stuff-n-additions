package net.furtitwo.createstuffadditions.fuel;

import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

import org.jetbrains.annotations.Nullable;

/**
 * An item that stores lava and/or water through the fluid capability and spends it to work.
 * Implementors delegate the matching {@code Item} overrides to these defaults.
 */
public interface FuelableItem {
	FuelType getFuelType();

	/** Capacity shown in tooltips, in display units (mB / 10). Synced from the server through map variables. */
	default double getDisplayCapacity(@Nullable LevelAccessor level) {
		return FuelStorage.gadgetCapacity(level);
	}

	/** Single-tank items use the vanilla bar; multi-tank (steam) items get one bar per tank from {@code SteamBarDecorator}. */
	default boolean fuelBarVisible(ItemStack stack) {
		return getFuelType().tankCount() == 1;
	}

	/** Bar follows the emptiest tank, since that is the one that stops the item from working. */
	default int fuelBarWidth(ItemStack stack) {
		int tank = limitingTank(stack);
		IFluidHandlerItem handler = FuelStorage.handler(stack);
		if (handler == null || handler.getTankCapacity(tank) <= 0)
			return 0;
		return Math.min(13, (int) (13f * handler.getFluidInTank(tank).getAmount() / handler.getTankCapacity(tank)));
	}

	default int fuelBarColor(ItemStack stack) {
		return getFuelType().tank(limitingTank(stack)).barColor;
	}

	private int limitingTank(ItemStack stack) {
		IFluidHandlerItem handler = FuelStorage.handler(stack);
		int best = 0;
		if (handler == null)
			return best;
		float bestRatio = Float.MAX_VALUE;
		for (int i = 0; i < getFuelType().tankCount() && i < handler.getTanks(); i++) {
			int capacity = handler.getTankCapacity(i);
			float ratio = capacity <= 0 ? 0 : handler.getFluidInTank(i).getAmount() / (float) capacity;
			if (ratio < bestRatio) {
				bestRatio = ratio;
				best = i;
			}
		}
		return best;
	}

	/** Right-click a fuel/water source onto this item in an inventory. */
	default boolean refuelFromCursor(ItemStack self, ItemStack source, Slot slot, ClickAction action, Player player, SlotAccess access) {
		if (self.getCount() > 1 || action != ClickAction.SECONDARY || !slot.allowModification(player))
			return false;
		return FuelRefuel.insertOne(self, getFuelType(), source, player, access);
	}
}
