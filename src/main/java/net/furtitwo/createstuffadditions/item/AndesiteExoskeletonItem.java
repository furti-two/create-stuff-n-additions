package net.furtitwo.createstuffadditions.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.furtitwo.createstuffadditions.item.armor.AnimatedArmorTextures;
import net.furtitwo.createstuffadditions.item.armor.CSAArmorMaterials;
import net.furtitwo.createstuffadditions.item.armor.ExoskeletonItem;
import net.furtitwo.createstuffadditions.fuel.FuelType;

import java.util.List;

/** Heat exoskeleton: Haste II but Weakness I, smoky, animated texture while fuelled. */
public class AndesiteExoskeletonItem extends ExoskeletonItem {
	private static final ResourceLocation[] FRAMES = AnimatedArmorTextures.frames("create_sa:textures/entities/andesite_exoskeleton/andesite_exoskeleton_", 8);

	public AndesiteExoskeletonItem() {
		super(CSAArmorMaterials.ANDESITE_EXOSKELETON, new Item.Properties(), FuelType.HEAT, "andesite_exoskeleton", false,
				List.of(new Effect(MobEffects.WEAKNESS, 18, 0), new Effect(MobEffects.DIG_SPEED, 19, 1)), 40, ResourceLocation.parse("entity.generic.extinguish_fire"), 0.02f, -4f);
	}

	@Override
	protected void onFueledTick(ItemStack stack, Level level, Player player) {
		AnimatedArmorTextures.chimneySmoke(level, player);
	}

	@Override
	public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
		return AnimatedArmorTextures.pick(FRAMES, entity, hasFuel(stack));
	}
}
