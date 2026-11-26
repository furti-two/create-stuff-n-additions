package net.furtitwo.createstuffadditions.item;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.furtitwo.createstuffadditions.equipment.CSAArmorMaterials;
import net.furtitwo.createstuffadditions.equipment.JetpackItem;
import net.furtitwo.createstuffadditions.fuel.FuelType;
import net.furtitwo.createstuffadditions.init.CreateSaModParticleTypes;

/** Hydraulic jetpack: water only, slow and low. */
public class CopperJetpackItem extends JetpackItem {
	public static final Stats STATS = new Stats(v -> v.copperJetpackSpeed, v -> v.copperJetpackMaxHeight, 10, 10, 5, 1, 0.8, 0.1, 0.2, 0.1, 0.15, ResourceLocation.parse("item.bucket.empty"), 0.05f, 2f);
	private static final ResourceLocation TEXTURE = ResourceLocation.parse("create_sa:textures/entities/copper_jetpack.png");

	public CopperJetpackItem() {
		super(CSAArmorMaterials.COPPER_JETPACK, new Item.Properties(), FuelType.HYDRAULIC, "copper_jetpack", false, STATS);
	}

	@Override
	protected ParticleOptions trailParticle() {
		return (ParticleOptions) CreateSaModParticleTypes.WATER_CLOUD.get();
	}

	@Override
	public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
		return TEXTURE;
	}
}
