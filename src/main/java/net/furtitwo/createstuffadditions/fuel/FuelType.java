package net.furtitwo.createstuffadditions.fuel;

import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

/**
 * Power source of a fuelable item and the layout of its internal tanks.
 * Tank order matches the capability registered in {@code ModCapabilities}: lava first, then water.
 */
public enum FuelType {
	/** Andesite tier: burns lava / burnable items. */
	HEAT(Resource.LAVA),
	/** Copper tier: water only. */
	HYDRAULIC(Resource.WATER),
	/** Brass and Netherite tiers: needs both. */
	STEAM(Resource.LAVA, Resource.WATER);

	private final Resource[] tanks;

	FuelType(Resource... tanks) {
		this.tanks = tanks;
	}

	public int tankCount() {
		return tanks.length;
	}

	public Resource tank(int index) {
		return tanks[index];
	}

	/** Index of the tank holding this resource, or -1 when the type does not use it. */
	public int indexOf(Resource resource) {
		for (int i = 0; i < tanks.length; i++)
			if (tanks[i] == resource)
				return i;
		return -1;
	}

	public boolean uses(Resource resource) {
		return indexOf(resource) >= 0;
	}

	@SuppressWarnings("unchecked")
	public TagKey<Fluid>[] fluidTags() {
		TagKey<Fluid>[] tags = new TagKey[tanks.length];
		for (int i = 0; i < tanks.length; i++)
			tags[i] = tanks[i].tag;
		return tags;
	}

	public enum Resource {
		LAVA(FluidTags.LAVA, Fluids.LAVA, 0xF5A970, "item_desc.fuel"),
		WATER(FluidTags.WATER, Fluids.WATER, 0x7F96D4, "item_desc.water");

		public final TagKey<Fluid> tag;
		public final Fluid fluid;
		public final int barColor;
		public final String tooltipKey;

		Resource(TagKey<Fluid> tag, Fluid fluid, int barColor, String tooltipKey) {
			this.tag = tag;
			this.fluid = fluid;
			this.barColor = barColor;
			this.tooltipKey = tooltipKey;
		}
	}
}
