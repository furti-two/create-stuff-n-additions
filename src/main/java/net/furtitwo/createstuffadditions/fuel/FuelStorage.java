package net.furtitwo.createstuffadditions.fuel;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.LevelAccessor;

import net.furtitwo.createstuffadditions.network.CreateSaModVariables;

import org.jetbrains.annotations.Nullable;

/**
 * Single entry point to read and change the fluids stored in a fuelable item, in the spirit of Create's {@code BacktankUtil}.
 * Amounts are in mB. "Display units" (what tooltips show and what the config calls capacity) are mB / 10.
 */
public final class FuelStorage {
	public static final int MB_PER_UNIT = 10;
	private static final String LEGACY_FUEL = "tagFuel";
	private static final String LEGACY_WATER = "tagWater";
	private static final String FLYING = "isFlying";

	private FuelStorage() {
	}

	@Nullable
	public static IFluidHandlerItem handler(ItemStack stack) {
		return stack.isEmpty() ? null : stack.getCapability(Capabilities.FluidHandler.ITEM);
	}

	public static int amount(ItemStack stack, int tank) {
		IFluidHandlerItem handler = handler(stack);
		return handler == null || tank >= handler.getTanks() ? 0 : handler.getFluidInTank(tank).getAmount();
	}

	public static int amount(ItemStack stack, FuelType type, FuelType.Resource resource) {
		int index = type.indexOf(resource);
		return index < 0 ? 0 : amount(stack, index);
	}

	/** True when every tank the type needs holds some fluid (steam needs both lava and water). */
	public static boolean hasFuel(ItemStack stack, FuelType type) {
		IFluidHandlerItem handler = handler(stack);
		if (handler == null)
			return false;
		for (int i = 0; i < type.tankCount(); i++)
			if (handler.getFluidInTank(i).getAmount() <= 0)
				return false;
		return true;
	}

	/** Average fill ratio (0..1) of the item's tanks, against the synced display capacity. */
	public static float fillRatio(ItemStack stack, FuelType type, double displayCapacity) {
		if (displayCapacity <= 0)
			return 0;
		float sum = 0;
		for (int i = 0; i < type.tankCount(); i++)
			sum += Math.min(1f, amount(stack, i) / (float) (displayCapacity * MB_PER_UNIT));
		return sum / type.tankCount();
	}

	/** Drains {@code mbPerTank} from every tank the type uses. */
	public static void consume(ItemStack stack, FuelType type, int mbPerTank) {
		IFluidHandlerItem handler = handler(stack);
		if (handler == null)
			return;
		for (int i = 0; i < type.tankCount(); i++)
			handler.drain(new FluidStack(type.tank(i).fluid, mbPerTank), IFluidHandler.FluidAction.EXECUTE);
	}

	/** Fills the tank matching {@code resource}; returns the amount accepted in mB. */
	public static int fill(ItemStack stack, FuelType.Resource resource, int mb, boolean simulate) {
		IFluidHandlerItem handler = handler(stack);
		if (handler == null || mb <= 0)
			return 0;
		return handler.fill(new FluidStack(resource.fluid, mb), simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
	}

	public static int space(ItemStack stack, FuelType type, FuelType.Resource resource) {
		IFluidHandlerItem handler = handler(stack);
		int index = type.indexOf(resource);
		if (handler == null || index < 0)
			return 0;
		return handler.getTankCapacity(index) - handler.getFluidInTank(index).getAmount();
	}

	public static double gadgetCapacity(@Nullable LevelAccessor level) {
		return level != null ? CreateSaModVariables.MapVariables.get(level).gadgetCapacity : 1600;
	}

	// ---- transient flags stored on the stack (written only when they change) ----

	public static boolean isFlying(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data != null && data.contains(FLYING) && data.copyTag().getBoolean(FLYING);
	}

	public static void setFlying(ItemStack stack, boolean flying) {
		if (isFlying(stack) != flying)
			CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean(FLYING, flying));
	}

	/**
	 * Older versions stored right-click refuels in "tagFuel"/"tagWater" (display units) that nothing else read.
	 * Moves those values into the real tanks once, then removes the keys.
	 */
	public static void migrateLegacy(ItemStack stack, FuelType type) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		if (data == null || !(data.contains(LEGACY_FUEL) || data.contains(LEGACY_WATER)))
			return;
		CompoundTag tag = data.copyTag();
		double fuel = tag.getDouble(LEGACY_FUEL);
		double water = tag.getDouble(LEGACY_WATER);
		CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> {
			t.remove(LEGACY_FUEL);
			t.remove(LEGACY_WATER);
		});
		if (fuel > 0 && type.uses(FuelType.Resource.LAVA))
			fill(stack, FuelType.Resource.LAVA, (int) Math.round(fuel * MB_PER_UNIT), false);
		if (water > 0 && type.uses(FuelType.Resource.WATER))
			fill(stack, FuelType.Resource.WATER, (int) Math.round(water * MB_PER_UNIT), false);
	}
}
