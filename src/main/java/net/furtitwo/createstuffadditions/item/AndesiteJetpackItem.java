package net.furtitwo.createstuffadditions.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.furtitwo.createstuffadditions.item.armor.AnimatedArmorTextures;
import net.furtitwo.createstuffadditions.item.armor.CSAArmorMaterials;
import net.furtitwo.createstuffadditions.item.armor.JetpackItem;
import net.furtitwo.createstuffadditions.fuel.FuelStorage;
import net.furtitwo.createstuffadditions.fuel.FuelType;

/** Heat jetpack: burns lava or fuel items, smoky, with an animated texture while flying. */
public class AndesiteJetpackItem extends JetpackItem {
	public static final Stats STATS = new Stats(v -> v.andesiteJetpackSpeed, v -> v.andesiteJetpackMaxHeight, 10, 10, 5, 3, 4, 0.15, 0.4, 0.2, 0.15, ResourceLocation.parse("entity.generic.extinguish_fire"), 0.02f, -4f);
	private static final ResourceLocation[] FRAMES = AnimatedArmorTextures.frames("create_sa:textures/entities/andesite_jetpack/andesite_jetpack_", 8);

	public AndesiteJetpackItem() {
		super(CSAArmorMaterials.ANDESITE_JETPACK, new Item.Properties(), FuelType.HEAT, "andesite_jetpack", false, STATS);
	}

	@Override
	protected void swimParticles(ServerLevel level, Player player) {
		level.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP, player.getX(), player.getY() + 0.45, player.getZ(), 3, 0.15, 0.15, 0.15, 0);
	}

	@Override
	protected void glideParticles(ServerLevel level, Player player) {
		level.sendParticles(ParticleTypes.LARGE_SMOKE, player.getX(), player.getY() + 0.45, player.getZ(), 3, 0.15, 0.15, 0.15, 0);
	}

	@Override
	protected void hoverParticles(ServerLevel level, Player player, boolean inWater) {
		if (inWater)
			backPoint(level, player, ParticleTypes.BUBBLE_COLUMN_UP, 0.5, 0, 4, 0.1, 0);
		else
			backPoint(level, player, ParticleTypes.LARGE_SMOKE, 0.5, 0, 1, 0, 0);
	}

	@Override
	protected void onFueledTick(ItemStack stack, Level level, Player player, boolean flying) {
		AnimatedArmorTextures.chimneySmoke(level, player);
	}

	@Override
	public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
		return AnimatedArmorTextures.pick(FRAMES, entity, FuelStorage.isFlying(stack) && hasFuel(stack));
	}
}
