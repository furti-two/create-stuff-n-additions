package net.furtitwo.createstuffadditions.client.equipment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Baked armour and first-person models, built once per resource reload (NOT once per frame).
 * One shared instance per item
 */
public final class ArmorModelCache {
	private static final Map<Item, HumanoidModel<LivingEntity>> ARMOR = new IdentityHashMap<>();
	private static final Map<Item, ChestplateVisual.Parts> FIRST_PERSON = new IdentityHashMap<>();

	private ArmorModelCache() {
	}

	public static void clear() {
		ARMOR.clear();
		FIRST_PERSON.clear();
	}

	public static HumanoidModel<LivingEntity> armor(Item item, ChestplateVisual visual) {
		return ARMOR.computeIfAbsent(item, i -> {
			ChestplateVisual.Parts parts = visual.armorParts().apply(Minecraft.getInstance().getEntityModels());
			return new HumanoidModel<>(new ModelPart(Collections.emptyList(),
					Map.of("body", parts.body(), "left_arm", parts.leftArm(), "right_arm", parts.rightArm(), "head", empty(), "hat", empty(), "right_leg", empty(), "left_leg", empty())));
		});
	}

	public static ChestplateVisual.Parts firstPerson(Item item, ChestplateVisual.FirstPerson spec) {
		return FIRST_PERSON.computeIfAbsent(item, i -> spec.parts().apply(Minecraft.getInstance().getEntityModels()));
	}

	private static ModelPart empty() {
		return new ModelPart(Collections.emptyList(), Collections.emptyMap());
	}
}
