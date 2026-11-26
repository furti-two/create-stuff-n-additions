package net.furtitwo.createstuffadditions.fuel;

import net.neoforged.neoforge.items.ItemHandlerHelper;

import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

/** Converts items (buckets, potions, sponges, burnables) into stored fluid. */
public final class FuelRefuel {
	private FuelRefuel() {
	}

	/** mB of lava-equivalent fuel one item gives: 10 mB per 200 ticks of burn time, as before. */
	public static int burnValueMb(ItemStack stack) {
		int burnTime = stack.getBurnTime(null);
		return burnTime <= 0 ? 0 : (int) Math.round(burnTime * 0.005) * FuelStorage.MB_PER_UNIT;
	}

	/** mB of water one item gives, or 0. */
	public static int waterValueMb(ItemStack stack) {
		if (stack.is(Items.WATER_BUCKET))
			return 1000;
		if (stack.is(Items.WET_SPONGE))
			return 500;
		if (stack.is(Items.POTION) && stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).is(Potions.WATER))
			return 250;
		return 0;
	}

	public static ItemStack waterRemainder(ItemStack stack) {
		if (stack.is(Items.WATER_BUCKET))
			return new ItemStack(Items.BUCKET);
		if (stack.is(Items.WET_SPONGE))
			return new ItemStack(Items.SPONGE);
		if (stack.is(Items.POTION))
			return new ItemStack(Items.GLASS_BOTTLE);
		return ItemStack.EMPTY;
	}

	/**
	 * Consumes one source item into the matching tank of {@code target}.
	 * Returns false (letting vanilla swap the stacks) when the source is not usable or the tank is full.
	 */
	public static boolean insertOne(ItemStack target, FuelType type, ItemStack source, Player player, SlotAccess access) {
		if (source.isEmpty())
			return false;
		FuelType.Resource resource;
		int value;
		ItemStack remainder;
		SoundEvent sound;
		int water = waterValueMb(source);
		if (water > 0 && type.uses(FuelType.Resource.WATER)) {
			resource = FuelType.Resource.WATER;
			value = water;
			remainder = waterRemainder(source);
			sound = SoundEvents.BUCKET_EMPTY;
		} else if (type.uses(FuelType.Resource.LAVA) && (value = burnValueMb(source)) > 0) {
			resource = FuelType.Resource.LAVA;
			remainder = source.getCraftingRemainingItem();
			sound = SoundEvents.FIRE_EXTINGUISH;
		} else {
			return false;
		}
		if (FuelStorage.space(target, type, resource) <= 0)
			return false;
		FuelStorage.fill(target, resource, value, false);
		boolean lastOne = source.getCount() == 1;
		source.shrink(1);
		if (!remainder.isEmpty()) {
			if (lastOne)
				access.set(remainder);
			else
				ItemHandlerHelper.giveItemToPlayer(player, remainder);
		}
		player.playSound(sound, 0.8f, 0.8f + player.level().getRandom().nextFloat() * 0.1f);
		return true;
	}
}
