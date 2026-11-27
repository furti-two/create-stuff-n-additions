package net.furtitwo.createstuffadditions.client.equipment;

import net.furtitwo.createstuffadditions.client.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;

import net.furtitwo.createstuffadditions.client.equipment.ChestplateVisual.FirstPerson;
import net.furtitwo.createstuffadditions.client.equipment.ChestplateVisual.Frame;
import net.furtitwo.createstuffadditions.client.equipment.ChestplateVisual.Parts;
import net.furtitwo.createstuffadditions.init.CreateSaModItems;

import net.createmod.catnip.animation.AnimationTickHolder;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

import static net.furtitwo.createstuffadditions.client.equipment.CreateSAPartialModels.*;

/**
 * Client registry: powered chestplate item -> how it looks. Positions are in 1/16 block, relative to the back anchor
 * set up by {@link FuelableChestplateLayer} (same anchor as Create's backtank).
 */
public final class ChestplateVisuals {
	private static final float PX = 1 / 16f;
	private static final float HALF_PI = (float) (Math.PI / 2);
	private static Map<Item, ChestplateVisual> visuals;

	private ChestplateVisuals() {
	}

	public static ChestplateVisual get(Item item) {
		return all().get(item);
	}

	public static Map<Item, ChestplateVisual> all() {
		if (visuals == null)
			visuals = build();
		return visuals;
	}

	private static Map<Item, ChestplateVisual> build() {
		Map<Item, ChestplateVisual> map = new IdentityHashMap<>();
		Function<net.minecraft.client.model.geom.EntityModelSet, Parts> andesiteExoParts = set -> {
			Modelandesite_exoskeleton<?> m = new Modelandesite_exoskeleton<>(set.bakeLayer(Modelandesite_exoskeleton.LAYER_LOCATION));
			return new Parts(m.body, m.leftArm, m.rightArm);
		};
		Function<net.minecraft.client.model.geom.EntityModelSet, Parts> brassExoParts = set -> {
			Modelbrass_exoskeleton<?> m = new Modelbrass_exoskeleton<>(set.bakeLayer(Modelbrass_exoskeleton.LAYER_LOCATION));
			return new Parts(m.body, m.rightArm, m.leftArm); // this model names its arms from the viewer's side
		};
		Function<net.minecraft.client.model.geom.EntityModelSet, Parts> copperExoParts = set -> {
			Modelcopper_exoskeleton<?> m = new Modelcopper_exoskeleton<>(set.bakeLayer(Modelcopper_exoskeleton.LAYER_LOCATION));
			return new Parts(m.body, m.leftArm, m.rightArm);
		};
		Function<net.minecraft.client.model.geom.EntityModelSet, Parts> netheriteExoParts = set -> {
			Modelnetherite_exoskeleton<?> m = new Modelnetherite_exoskeleton<>(set.bakeLayer(Modelnetherite_exoskeleton.LAYER_LOCATION));
			return new Parts(m.body, m.rightArm, m.leftArm);
		};

		map.put(CreateSaModItems.BRASS_JETPACK_CHESTPLATE.get(), new ChestplateVisual(set -> {
			Modelbrass_jetpack<?> m = new Modelbrass_jetpack<>(set.bakeLayer(Modelbrass_jetpack.LAYER_LOCATION));
			return new Parts(m.body, m.leftarm, m.rightarm);
		}, null, ChestplateVisuals::brassJetpack));
		map.put(CreateSaModItems.ANDESITE_JETPACK_CHESTPLATE.get(), new ChestplateVisual(set -> {
			Modelandesite_jetpack<?> m = new Modelandesite_jetpack<>(set.bakeLayer(Modelandesite_jetpack.LAYER_LOCATION));
			return new Parts(m.body, m.leftarm, m.rightarm);
		}, null, ChestplateVisuals::andesiteJetpack));
		map.put(CreateSaModItems.COPPER_JETPACK_CHESTPLATE.get(), new ChestplateVisual(set -> {
			Modelcopper_jetpack<?> m = new Modelcopper_jetpack<>(set.bakeLayer(Modelcopper_jetpack.LAYER_LOCATION));
			return new Parts(m.body, m.leftarm, m.rightarm);
		}, null, ChestplateVisuals::copperJetpack));
		map.put(CreateSaModItems.NETHERITE_JETPACK_CHESTPLATE.get(), new ChestplateVisual(set -> {
			Modelnetherite_jetpack<?> m = new Modelnetherite_jetpack<>(set.bakeLayer(Modelnetherite_jetpack.LAYER_LOCATION));
			return new Parts(m.body, m.leftarm, m.rightarm);
		}, new FirstPerson(andesiteExoParts, tex("netherite_diving_arm")), ChestplateVisuals::netheriteJetpack));
		map.put(CreateSaModItems.ANDESITE_EXOSKELETON_CHESTPLATE.get(),
				new ChestplateVisual(andesiteExoParts, new FirstPerson(andesiteExoParts, tex("andesite_exoskeleton/andesite_exoskeleton_1")), ChestplateVisuals::andesiteExoskeleton));
		map.put(CreateSaModItems.BRASS_EXOSKELETON_CHESTPLATE.get(), new ChestplateVisual(brassExoParts, new FirstPerson(brassExoParts, tex("brass_exoskeleton")), ChestplateVisuals::brassExoskeleton));
		map.put(CreateSaModItems.COPPER_EXOSKELETON_CHESTPLATE.get(), new ChestplateVisual(copperExoParts, new FirstPerson(copperExoParts, tex("copper_exoskeleton")), ChestplateVisuals::copperExoskeleton));
		map.put(CreateSaModItems.NETHERITE_EXOSKELETON_CHESTPLATE.get(), new ChestplateVisual(netheriteExoParts, new FirstPerson(netheriteExoParts, tex("netherite_exoskeleton")), ChestplateVisuals::netheriteExoskeleton));
		return map;
	}

	private static ResourceLocation tex(String name) {
		return ResourceLocation.fromNamespaceAndPath("create_sa", "textures/entities/" + name + ".png");
	}

	// Back Layouts

	private static void brassJetpack(Frame f) {
		float prop = f.flying() ? 20 : 0;
		f.part(BRASS_JETPACK_PROPELLER, b -> b.translate(4.25f * PX, 2 * PX, 1.75f * PX).center().rotateY(f.angle(prop)).uncenter());
		f.part(BRASS_JETPACK_PROPELLER, b -> b.translate(-4.25f * PX, 2 * PX, 1.75f * PX).center().rotateY(f.angle(-prop)).uncenter());
		f.part(BRASS_COG, b -> b.translate(0, -1.5f * PX, 15 * PX).rotateX(-HALF_PI).center().rotateY(f.angle(f.spin())).uncenter());
	}

	private static void andesiteJetpack(Frame f) {
		float prop = f.flying() ? 30 : 0;
		for (float z : new float[]{-3.5f, 2.5f}) {
			f.part(ANDESITE_JETPACK_PROPELLER, b -> b.translate(2.25f * PX, 0, z * PX).center().rotateY(f.angle(prop)).uncenter());
			f.part(ANDESITE_JETPACK_PROPELLER, b -> b.translate(-2.25f * PX, 0, z * PX).center().rotateY(f.angle(-prop)).uncenter());
		}
		for (float x : new float[]{5.25f, -8.25f})
			f.part(BRASS_COG, b -> b.translate(x * PX, 12.5f * PX, -0.5f * PX).rotateZ(-HALF_PI).center().rotateY(f.angle(f.spin())).uncenter());
	}

	private static void copperJetpack(Frame f) {
		f.part(STONE_COG, b -> b.translate(0, -3.5f * PX, 15 * PX).rotateX(-HALF_PI).center().rotateY(f.angle(f.spin())).uncenter());
	}

	private static void netheriteJetpack(Frame f) {
		float prop = f.flying() ? 20 : 0;
		float tilt = (float) Math.toRadians(netheriteTilt(f) * 50f);
		float pi = (float) Math.PI;
		f.part(NETHERITE_JETPACK_BACK, b -> b.translate(0, -1.5f * PX, 13 * PX).rotateX(-HALF_PI).center().rotateY(pi).uncenter().center().rotateY(tilt).uncenter());
		f.part(STONE_COG, b -> b.translate(0, -1.5f * PX, 15 * PX).rotateX(-HALF_PI).center().rotateY(tilt).uncenter().center().rotateY(f.angle(f.spin())).uncenter());
		f.part(NETHERITE_JETPACK_PROPELLER, b -> b.translate(0, -1.5f * PX, 16.66f * PX).rotateX(-HALF_PI).center().rotateY(pi).uncenter().center().rotateY(tilt)
				.translate(-5.05f * PX, 0, 0).uncenter().center().rotateZ(f.angle(prop)).uncenter());
		f.part(NETHERITE_JETPACK_PROPELLER, b -> b.translate(0, -1.5f * PX, 16.66f * PX).rotateX(-HALF_PI).center().rotateY(pi).uncenter().center().rotateY(tilt)
				.translate(5.05f * PX, 0, 0).uncenter().center().rotateZ(f.angle(-prop)).uncenter());
	}

	/** Sideways movement relative to the look direction; the back assembly leans into turns. */
	private static float netheriteTilt(Frame f) {
		var e = f.entity();
		if (!f.flying() && e.onGround())
			return 0;
		Vec3 view = e.getViewVector(AnimationTickHolder.getPartialTicks()).normalize();
		double mvX = e.xo - e.getX(), mvZ = e.zo - e.getZ();
		return (float) -(view.z * mvX - mvZ * view.x);
	}

	private static void andesiteExoskeleton(Frame f) {
		for (float x : new float[]{3, -3})
			f.part(ANDESITE_JETPACK_PROPELLER, b -> b.translate(x * PX, 2 * PX, 1.75f * PX).center().rotateY(f.angle(f.spin())).uncenter());
		f.part(BRASS_COG, b -> b.translate(0, 15 * PX, -6.25f * PX).rotateX(HALF_PI).center().rotateY(f.angle(f.spin())).uncenter());
	}

	private static void brassExoskeleton(Frame f) {
		f.part(FLYWHEEL, b -> b.translate(0, 14.5f * PX, 5 * PX).rotateX(HALF_PI).center().rotateY(f.angle(f.spin())).uncenter());
	}

	private static void copperExoskeleton(Frame f) {
		f.part(STONE_COG, b -> b.translate(0, -1.5f * PX, 15 * PX).rotateX(-HALF_PI).center().rotateY(f.angle(f.spin())).uncenter());
	}

	private static void netheriteExoskeleton(Frame f) {
		f.part(NETHERITE_FLYWHEEL, b -> b.translate(0, 14.5f * PX, 5 * PX).rotateX(HALF_PI).center().rotateY(f.angle(f.spin())).uncenter());
	}
}
