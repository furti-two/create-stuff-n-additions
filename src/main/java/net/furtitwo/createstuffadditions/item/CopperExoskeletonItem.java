package net.furtitwo.createstuffadditions.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.furtitwo.createstuffadditions.item.armor.CSAArmorMaterials;
import net.furtitwo.createstuffadditions.item.armor.ExoskeletonItem;
import net.furtitwo.createstuffadditions.fuel.FuelType;

import java.util.List;

/** Hydraulic exoskeleton: Strength II but Mining Fatigue I. */
public class CopperExoskeletonItem extends ExoskeletonItem {
	private static final ResourceLocation TEXTURE = ResourceLocation.parse("create_sa:textures/entities/copper_exoskeleton.png");

	public CopperExoskeletonItem() {
		super(CSAArmorMaterials.COPPER_EXOSKELETON, new Item.Properties(), FuelType.HYDRAULIC, "copper_exoskeleton", false,
				List.of(new Effect(MobEffects.DAMAGE_BOOST, 18, 1), new Effect(MobEffects.DIG_SLOWDOWN, 19, 0)), 40, ResourceLocation.parse("item.bucket.empty"), 0.05f, 2f);
	}

	@Override
	public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
		return TEXTURE;
	}
}
