package net.furtitwo.createstuffadditions.init;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import net.furtitwo.createstuffadditions.configuration.CreateSaConfigConfiguration;
import net.furtitwo.createstuffadditions.fuel.FuelType;
import net.furtitwo.createstuffadditions.fuel.FuelableItem;
import net.furtitwo.createstuffadditions.item.CustomFluidHandlerItemStack;

/**
 * Fluid capability of every tank and fuelable item. Fuelable items get tanks matching their {@link FuelType}
 * automatically; capacities are config values in display units, stored in mB (x10).
 */
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ModCapabilities {
	@SubscribeEvent
	public static void registerCaps(RegisterCapabilitiesEvent event) {
		int smallCapacity = (int) Math.round(CreateSaConfigConfiguration.SMALLTANKCAPACITY.get()) * 10;
		int mediumCapacity = (int) Math.round(CreateSaConfigConfiguration.MEDIUMTANKCAPACITY.get()) * 10;
		int largeCapacity = (int) Math.round(CreateSaConfigConfiguration.LARGETANKCAPACITY.get()) * 10;
		int gadgetCapacity = (int) Math.round(CreateSaConfigConfiguration.GADGETCAPACITY.get()) * 10;
		// Tanks
		tank(event, CreateSaModItems.SMALL_FILLING_TANK.get(), smallCapacity, FuelType.HYDRAULIC);
		tank(event, CreateSaModItems.MEDIUM_FILLING_TANK.get(), mediumCapacity, FuelType.HYDRAULIC);
		tank(event, CreateSaModItems.LARGE_FILLING_TANK.get(), largeCapacity, FuelType.HYDRAULIC);
		tank(event, CreateSaModItems.SMALL_FUELING_TANK.get(), smallCapacity, FuelType.HEAT);
		tank(event, CreateSaModItems.MEDIUM_FUELING_TANK.get(), mediumCapacity, FuelType.HEAT);
		tank(event, CreateSaModItems.LARGE_FUELING_TANK.get(), largeCapacity, FuelType.HEAT);
		// Gadgets not yet on the FuelableItem API
		tank(event, CreateSaModItems.BLOCK_PICKER.get(), gadgetCapacity, FuelType.HYDRAULIC);
		tank(event, CreateSaModItems.GRAPPLIN_WHISK.get(), gadgetCapacity, FuelType.HEAT);
		tank(event, CreateSaModItems.FLAMETHROWER.get(), gadgetCapacity, FuelType.HEAT);
		tank(event, CreateSaModItems.PORTABLE_DRILL.get(), gadgetCapacity, FuelType.STEAM);
		// Every FuelableItem (jetpacks, exoskeletons, ...)
		for (Item item : BuiltInRegistries.ITEM) {
			if (item instanceof FuelableItem fuelable && !event.isItemRegistered(Capabilities.FluidHandler.ITEM, item))
				tank(event, item, gadgetCapacity, fuelable.getFuelType());
		}
	}

	private static void tank(RegisterCapabilitiesEvent event, Item item, int capacity, FuelType type) {
		int[] capacities = new int[type.tankCount()];
		java.util.Arrays.fill(capacities, capacity);
		event.registerItem(Capabilities.FluidHandler.ITEM, (stack, context) -> new CustomFluidHandlerItemStack(stack, capacities, type.fluidTags()), item);
	}
}
