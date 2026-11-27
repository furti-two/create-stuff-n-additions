package net.furtitwo.createstuffadditions.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.furtitwo.createstuffadditions.item.armor.CSAArmorMaterials;
import net.furtitwo.createstuffadditions.item.armor.JetpackItem;
import net.furtitwo.createstuffadditions.fuel.FuelType;

/** Steam jetpack: lava + water. */
public class BrassJetpackItem extends JetpackItem {
	public static final Stats STATS = new Stats(v -> v.brassJetpackSpeed, v -> v.brassJetpackMaxHeight, 40, 10, 20, 2, 3, 0.1, 0.3, 0.15, 0.1, ResourceLocation.parse("create_sa:steam_noise"), 0.1f, 2f);
	private static final ResourceLocation TEXTURE = ResourceLocation.parse("create_sa:textures/entities/brass_jetpack.png");

	public BrassJetpackItem() {
		super(CSAArmorMaterials.BRASS_JETPACK, new Item.Properties(), FuelType.STEAM, "brass_jetpack", true, STATS);
	}

	@Override
	public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
		return TEXTURE;
	}
}
