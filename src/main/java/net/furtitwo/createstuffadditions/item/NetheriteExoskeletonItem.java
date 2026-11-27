package net.furtitwo.createstuffadditions.item;

import net.furtitwo.createstuffadditions.fuel.FuelType;
import net.furtitwo.createstuffadditions.item.armor.CSAArmorMaterials;
import net.furtitwo.createstuffadditions.item.armor.ExoskeletonItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** Steam exoskeleton: Strength I + Haste I. */
public class NetheriteExoskeletonItem extends ExoskeletonItem {
	private static final ResourceLocation TEXTURE = ResourceLocation.parse("create_sa:textures/entities/netherite_exoskeleton.png");

	public NetheriteExoskeletonItem() {
		super(CSAArmorMaterials.BRASS_EXOSKELETON, new Properties().fireResistant(), FuelType.STEAM, "netherite_exoskeleton", true,
				List.of(new Effect(MobEffects.DAMAGE_BOOST, 18, 0), new Effect(MobEffects.DIG_SPEED, 19, 0)), 80, ResourceLocation.parse("create_sa:steam_noise"), 0.1f, 2f);
	}

	@Override
	public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
		return TEXTURE;
	}
}
