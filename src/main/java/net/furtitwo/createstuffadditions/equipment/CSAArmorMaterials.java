package net.furtitwo.createstuffadditions.equipment;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

/**
 * Every armour material of the mod. Ids and values are unchanged from the MCreator versions.
 * Powered chestplates use an invisible texture layer: their visible geometry comes from the custom armour models.
 */
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public final class CSAArmorMaterials {
	public static Holder<ArmorMaterial> ANDESITE_JETPACK;
	public static Holder<ArmorMaterial> BRASS_JETPACK;
	public static Holder<ArmorMaterial> COPPER_JETPACK;
	public static Holder<ArmorMaterial> NETHERITE_JETPACK;
	public static Holder<ArmorMaterial> ANDESITE_EXOSKELETON;
	public static Holder<ArmorMaterial> BRASS_EXOSKELETON;
	public static Holder<ArmorMaterial> COPPER_EXOSKELETON;
	public static Holder<ArmorMaterial> BRASS;
	public static Holder<ArmorMaterial> COPPER;
	public static Holder<ArmorMaterial> ZINC;
	public static Holder<ArmorMaterial> SLIME;

	private CSAArmorMaterials() {
	}

	@SubscribeEvent
	public static void register(RegisterEvent event) {
		event.register(Registries.ARMOR_MATERIAL, helper -> {
			Supplier<Ingredient> andesite = () -> Ingredient.of(Items.IRON_INGOT, Blocks.ANDESITE);
			Supplier<Ingredient> copper = () -> Ingredient.of(Items.COPPER_INGOT);
			ANDESITE_JETPACK = make(helper, "andesite_jetpack", 6, andesite, 0f, 0f);
			BRASS_JETPACK = make(helper, "brass_jetpack", 6, () -> Ingredient.EMPTY, 0f, 0.1f);
			COPPER_JETPACK = make(helper, "copper_jetpack", 6, copper, 0f, 0f);
			NETHERITE_JETPACK = make(helper, "netherite_jetpack", 8, () -> Ingredient.of(Items.NETHERITE_INGOT), 3f, 0.1f);
			ANDESITE_EXOSKELETON = make(helper, "andesite_exoskeleton", 7, andesite, 0f, 0f);
			BRASS_EXOSKELETON = make(helper, "brass_exoskeleton", 8, () -> Ingredient.of(ItemTags.create(ResourceLocation.parse("forge:ingots/brass"))), 0f, 0.1f);
			COPPER_EXOSKELETON = make(helper, "copper_exoskeleton", 6, copper, 0f, 0f);
			// Plain armour sets, textured by their own layer (textures/models/armor/<name>_layer_1/2.png)
			BRASS = make(helper, "brass", 2, 7, SoundEvents.ARMOR_EQUIP_GOLD, () -> Ingredient.of(tag("c:ingots/brass")), "create_sa:brass", 0f, 0.1f);
			COPPER = make(helper, "copper", 1, 6, SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(tag("c:ingots/copper")), "create_sa:copper", 0f, 0f);
			ZINC = make(helper, "zinc", 2, 6, SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(tag("c:ingots/zinc")), "create_sa:zinc", 0f, 0f);
			SLIME = make(helper, "slime", 2, 6, DeferredHolder.create(Registries.SOUND_EVENT, ResourceLocation.parse("block.slime_block.place")), () -> Ingredient.of(Items.SLIME_BALL),
					"create_sa:slime", 0f, 0f);
		});
	}

	private static TagKey<Item> tag(String id) {
		return ItemTags.create(ResourceLocation.parse(id));
	}

	/** Powered chestplate material: netherite equip sound and an invisible texture layer. */
	private static Holder<ArmorMaterial> make(RegisterEvent.RegisterHelper<ArmorMaterial> helper, String name, int chest, Supplier<Ingredient> repair, float toughness, float knockback) {
		return make(helper, name, 2, chest, SoundEvents.ARMOR_EQUIP_NETHERITE, repair, "create_sa:invisible", toughness, knockback);
	}

	/** Defense: helmet 2, leggings 5, the given boots and chestplate values; enchantability 9. */
	private static Holder<ArmorMaterial> make(RegisterEvent.RegisterHelper<ArmorMaterial> helper, String name, int boots, int chest, Holder<SoundEvent> equipSound, Supplier<Ingredient> repair,
			String layer, float toughness, float knockback) {
		ArmorMaterial material = new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
			map.put(ArmorItem.Type.BOOTS, boots);
			map.put(ArmorItem.Type.LEGGINGS, 5);
			map.put(ArmorItem.Type.CHESTPLATE, chest);
			map.put(ArmorItem.Type.HELMET, 2);
			map.put(ArmorItem.Type.BODY, chest);
		}), 9, equipSound, repair, List.of(new ArmorMaterial.Layer(ResourceLocation.parse(layer))), toughness, knockback);
		helper.register(ResourceLocation.fromNamespaceAndPath("create_sa", name), material);
		return BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(material);
	}
}
