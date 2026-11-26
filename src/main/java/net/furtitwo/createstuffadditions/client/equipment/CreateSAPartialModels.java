package net.furtitwo.createstuffadditions.client.equipment;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;

import net.minecraft.resources.ResourceLocation;

/**
 * Moving parts of the powered chestplates, rendered like Create's backtank cogs.
 * Must be class-loaded before model loading ({@link #init()} from the client mod constructor) so Flywheel registers them.
 * Replaces the old {@code propeller_render_prop} block whose blockstates only existed to carry these models.
 */
public final class CreateSAPartialModels {
	public static final PartialModel BRASS_JETPACK_PROPELLER = partial("brass_jetpack_propeller");
	public static final PartialModel BRASS_COG = partial("brass_cog");
	public static final PartialModel ANDESITE_JETPACK_PROPELLER = partial("andesite_jetpack_propeller");
	public static final PartialModel STONE_COG = partial("stone_cog");
	public static final PartialModel FLYWHEEL = partial("flywheel");
	public static final PartialModel NETHERITE_JETPACK_BACK = partial("netherite_jetpack_back");
	public static final PartialModel NETHERITE_JETPACK_PROPELLER = partial("netherite_jetpack_propeller");

	private CreateSAPartialModels() {
	}

	private static PartialModel partial(String name) {
		return PartialModel.of(ResourceLocation.fromNamespaceAndPath("create_sa", "partial/" + name));
	}

	public static void init() {
	}
}
