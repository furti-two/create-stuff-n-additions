package net.furtitwo.createstuffadditions.item;

import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.capabilities.Capabilities;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;

public class CustomFluidHandlerItemStack implements IFluidHandlerItem {
	private final ItemStack container;
	private final int[] capacities;
	private final TagKey<Fluid>[] fluidTags;
	private static final String TAG_FLUID = "Fluid";
	private static final String TAG_AMOUNT = "Amount";

	// Single tank constructor
	public CustomFluidHandlerItemStack(ItemStack container, int capacity, TagKey<Fluid> fluidTag) {
		this(container, new int[]{capacity}, new TagKey[]{fluidTag});
	}

	// Multi tank constructor
	public CustomFluidHandlerItemStack(ItemStack container, int[] capacities, TagKey<Fluid>[] fluidTags) {
		this.container = container;
		this.capacities = capacities;
		this.fluidTags = fluidTags;
		if (capacities.length != fluidTags.length) {
			throw new IllegalArgumentException("capacities and fluidTags length mismatch");
		}
	}

	public static int fillTank(ItemStack stack, FluidStack fluid, boolean simulate) {
		if (stack == null || fluid == null || fluid.isEmpty())
			return 0;
		IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
		if (handler == null)
			return 0;
		return handler.fill(fluid, simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE);
	}

	public static FluidStack drainTank(ItemStack stack, FluidStack fluid, boolean simulate) {
		if (stack == null || fluid == null || fluid.isEmpty())
			return FluidStack.EMPTY;
		IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
		if (handler == null)
			return FluidStack.EMPTY;
		return handler.drain(fluid, simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE);
	}

	private String fluidKey(int tank) {
		return TAG_FLUID + tank;
	}

	private String amountKey(int tank) {
		return TAG_AMOUNT + tank;
	}

	private FluidStack getStoredFluid(int tank) {
		CompoundTag tag = container.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		String fluidKey = fluidKey(tank);
		String amountKey = amountKey(tank);
		if (!tag.contains(fluidKey))
			return FluidStack.EMPTY;
		ResourceLocation id = ResourceLocation.parse(tag.getString(fluidKey));
		Fluid fluid = BuiltInRegistries.FLUID.get(id);
		if (fluid == null)
			return FluidStack.EMPTY;
		return new FluidStack(fluid, tag.getInt(amountKey));
	}

	private void setStoredFluid(int tank, FluidStack stack) {
		CompoundTag tag = container.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		String fluidKey = fluidKey(tank);
		String amountKey = amountKey(tank);
		if (stack.isEmpty()) {
			tag.remove(fluidKey);
			tag.remove(amountKey);
		} else {
			tag.putString(fluidKey, BuiltInRegistries.FLUID.getKey(stack.getFluid()).toString());
			tag.putInt(amountKey, stack.getAmount());
		}
		container.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
	}

	@Override
	public ItemStack getContainer() {
		return container;
	}

	@Override
	public int getTanks() {
		return capacities.length;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		if (tank < 0 || tank >= getTanks())
			return FluidStack.EMPTY;
		return getStoredFluid(tank);
	}

	@Override
	public int getTankCapacity(int tank) {
		if (tank < 0 || tank >= getTanks())
			return 0;
		return capacities[tank];
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		if (tank < 0 || tank >= getTanks())
			return false;
		return !stack.isEmpty() && stack.getFluid().is(fluidTags[tank]);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (resource.isEmpty())
			return 0;
		for (int tank = 0; tank < getTanks(); tank++) {
			if (!isFluidValid(tank, resource))
				continue;
			FluidStack current = getStoredFluid(tank);
			// Different fluid already inside
			if (!current.isEmpty() && !current.isFluidEqual(resource))
				continue;
			int capacity = getTankCapacity(tank);
			int space = capacity - current.getAmount();
			if (space <= 0)
				continue;
			int fillAmount = Math.min(space, resource.getAmount());
			if (action.execute()) {
				if (current.isEmpty()) {
					setStoredFluid(tank, new FluidStack(resource.getFluid(), fillAmount));
				} else {
					setStoredFluid(tank, new FluidStack(current.getFluid(), current.getAmount() + fillAmount));
				}
			}
			return fillAmount;
		}
		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		if (resource.isEmpty())
			return FluidStack.EMPTY;
		for (int tank = 0; tank < getTanks(); tank++) {
			FluidStack current = getStoredFluid(tank);
			if (current.isEmpty())
				continue;
			if (!current.isFluidEqual(resource))
				continue;
			int drained = Math.min(resource.getAmount(), current.getAmount());
			FluidStack result = new FluidStack(current.getFluid(), drained);
			if (action.execute()) {
				int remaining = current.getAmount() - drained;
				setStoredFluid(tank, remaining <= 0 ? FluidStack.EMPTY : new FluidStack(current.getFluid(), remaining));
			}
			return result;
		}
		return FluidStack.EMPTY;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		if (maxDrain <= 0)
			return FluidStack.EMPTY;
		for (int tank = 0; tank < getTanks(); tank++) {
			FluidStack current = getStoredFluid(tank);
			if (current.isEmpty())
				continue;
			int drained = Math.min(maxDrain, current.getAmount());
			FluidStack result = new FluidStack(current.getFluid(), drained);
			if (action.execute()) {
				int remaining = current.getAmount() - drained;
				setStoredFluid(tank, remaining <= 0 ? FluidStack.EMPTY : new FluidStack(current.getFluid(), remaining));
			}
			return result;
		}
		return FluidStack.EMPTY;
	}
}
