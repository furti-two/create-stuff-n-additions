package net.furtitwo.createstuffadditions.logic.tank;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import net.furtitwo.createstuffadditions.fuel.FuelStorage;
import net.furtitwo.createstuffadditions.fuel.FuelType;
import net.furtitwo.createstuffadditions.init.CreateSaModParticleTypes;

import org.jetbrains.annotations.Nullable;

/**
 * Tossing (Q) a water container or a burnable item while holding a fillable / fuelable item in the off hand pours it
 * into that item instead of dropping it. Empty containers are given back.
 */
@EventBusSubscriber
public final class TossRefuelling {
	private static final double EFFECT_HEIGHT = 1.45;

	private TossRefuelling() {
	}

	/** Water given by a tossed item, and the empty container handed back for each item of the stack. */
	private record WaterSource(Item emptied, int mb) {
	}

	@Nullable
	private static WaterSource waterSource(ItemStack stack) {
		if (stack.is(Items.WATER_BUCKET))
			return new WaterSource(Items.BUCKET, 1000);
		if (stack.is(Items.POTION))
			return new WaterSource(Items.GLASS_BOTTLE, 250);
		if (stack.is(Items.WET_SPONGE))
			return new WaterSource(Items.SPONGE, 500);
		return null;
	}

	@SubscribeEvent
	public static void onItemToss(ItemTossEvent event) {
		Player player = event.getPlayer();
		ItemStack tossed = event.getEntity().getItem();
		pourWater(event, player, tossed);
		pourFuel(event, player, tossed);
	}

	private static void pourWater(ItemTossEvent event, Player player, ItemStack tossed) {
		ItemStack target = player.getOffhandItem();
		if (!target.is(TankFilling.FILLABLE))
			return;
		IFluidHandlerItem handler = FuelStorage.handler(target);
		WaterSource source = waterSource(tossed);
		if (handler == null || source == null || !TankFilling.hasSpace(handler, target, FuelType.Resource.WATER))
			return;
		// The amount poured does not depend on the stack size, the containers given back do
		ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(source.emptied(), tossed.getCount()));
		FuelStorage.fill(target, FuelType.Resource.WATER, source.mb(), false);
		event.setCanceled(true);
		Level level = player.level();
		if (level instanceof ServerLevel server)
			server.sendParticles((SimpleParticleType) CreateSaModParticleTypes.WATER_CLOUD.get(), player.getX(), player.getY() + EFFECT_HEIGHT, player.getZ(), 2, 0.2, 0.2, 0.2, 0);
		TankFilling.playSound(level, player.getX(), player.getY(), player.getZ(), SoundEvents.BUCKET_EMPTY, 1f, 0.5f);
	}

	private static void pourFuel(ItemTossEvent event, Player player, ItemStack tossed) {
		ItemStack target = player.getOffhandItem();
		if (!target.is(TankFilling.FUELABLE))
			return;
		IFluidHandlerItem handler = FuelStorage.handler(target);
		int burnTime = tossed.getBurnTime(null);
		if (handler == null || burnTime <= 1 || !TankFilling.hasSpace(handler, target, FuelType.Resource.LAVA))
			return;
		if (tossed.is(Items.LAVA_BUCKET))
			ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.BUCKET, tossed.getCount()));
		event.setCanceled(true);
		// 10 mB of lava per 200 ticks of burn time (rounded), per item
		FuelStorage.fill(target, FuelType.Resource.LAVA, (int) (10 * Math.round(burnTime * 0.005) * tossed.getCount()), false);
		Level level = player.level();
		if (level instanceof ServerLevel server)
			server.sendParticles(ParticleTypes.LARGE_SMOKE, player.getX(), player.getY() + EFFECT_HEIGHT, player.getZ(), 2, 0.2, 0.2, 0.2, 0);
		TankFilling.playSound(level, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRE_EXTINGUISH, 1f, 0.5f);
	}
}
