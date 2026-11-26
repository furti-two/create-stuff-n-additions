package net.furtitwo.createstuffadditions.equipment;

import net.furtitwo.createstuffadditions.compat.sable.SubLevels;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.furtitwo.createstuffadditions.fuel.FuelStorage;
import net.furtitwo.createstuffadditions.fuel.FuelType;

/**
 * Grapplin Whisk mechanic, ported from the 1.20.1 version (createsa_casser workspace):
 * right-click hooks the block in sight (75 blocks); while hooked and held, the player is pulled toward the hook by
 * {@link #PULL} per tick and fall damage is cancelled unless falling fast; right-click again lets go (Impact hit).
 * Hook state lives in the stack's custom data ({@code tagHooked}, {@code xPostion/yPostion/zPostion}), read by the
 * chain and item renderers, and only changes when hooking or letting go.
 */
public final class GrapplinHook {
	public static final double RANGE = 75;
	/** Speed added toward the hook every tick. */
	public static final double PULL = 0.2;
	/** Fuel is spent every {@code DRAIN_INTERVAL + 1} ticks while hooked (old cooldown counter of 5). */
	public static final int DRAIN_INTERVAL = 5;
	public static final int DRAIN_MB = 10;

	private static final String HOOKED = "tagHooked";
	private static final String X = "xPostion", Y = "yPostion", Z = "zPostion";
	private static final ResourceKey<Enchantment> IMPACT = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("create_sa:impact"));
	private static final ResourceKey<DamageType> IMPACT_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("create_sa:grapplin_whisk_attack"));

	private GrapplinHook() {
	}

	public static boolean isHooked(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data != null && data.copyTag().getBoolean(HOOKED);
	}

	/** The hooked block. */
	public static BlockPos hookPos(ItemStack stack) {
		CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		return BlockPos.containing(tag.getDouble(X), tag.getDouble(Y), tag.getDouble(Z));
	}

	/** Right-click: lets go when hooked (with the Impact hit), otherwise hooks the block in sight. */
	public static boolean toggle(Level level, Player player, ItemStack stack) {
		if (isHooked(stack)) {
			impact(level, player, stack);
			release(stack);
			return true;
		}
		if (!FuelStorage.hasFuel(stack, FuelType.HEAT)) {
			if (!level.isClientSide())
				player.displayClientMessage(Component.translatableWithFallback("message.create_sa.grapplin_whisk.no_fuel", "The Grapplin Whisk needs fuel"), true);
			return false;
		}
		Vec3 eye = player.getEyePosition(1f);
		BlockHitResult hit = level.clip(new ClipContext(eye, eye.add(player.getViewVector(1f).scale(RANGE)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
		if (hit.getType() != HitResult.Type.BLOCK)
			return true; // the old version hooked thin air and let go on the next tick: just swing the arm
		BlockPos pos = hit.getBlockPos();
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
			tag.putBoolean(HOOKED, true);
			tag.putDouble(X, pos.getX());
			tag.putDouble(Y, pos.getY());
			tag.putDouble(Z, pos.getZ());
		});
		return true;
	}

	/** Every tick while the whisk is held in a hand. */
	public static void tickHeld(Level level, Player player, ItemStack stack) {
		if (!isHooked(stack))
			return;
		BlockPos pos = hookPos(stack);
		if (!FuelStorage.hasFuel(stack, FuelType.HEAT) || level.isEmptyBlock(pos)) {
			release(stack);
			return;
		}
		// Old formula: direction to the block centre, normalised by the distance to the block corner.
		// Both are projected to the world, as the hooked block may be on a moving Sable structure.
		double distance = player.position().distanceTo(SubLevels.toWorld(level, Vec3.atLowerCornerOf(pos)));
		if (distance > 1.0E-4) {
			Vec3 toCenter = SubLevels.toWorld(level, Vec3.atCenterOf(pos)).subtract(player.position());
			player.setDeltaMovement(player.getDeltaMovement().add(toCenter.scale(PULL / distance)));
		}
		if (player.getDeltaMovement().y >= -0.2)
			player.fallDistance = 0;
		if (!level.isClientSide()) {
			level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_CHAIN.value(), SoundSource.NEUTRAL, 0.25f, 0.5f);
			if (player.tickCount % (DRAIN_INTERVAL + 1) == 0)
				FuelStorage.consume(stack, FuelType.HEAT, DRAIN_MB);
		}
	}

	public static void release(ItemStack stack) {
		if (isHooked(stack))
			CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean(HOOKED, false));
	}

	/** Impact enchantment: hurts and bounces off entities next to the player, scaled by the player's speed. */
	private static void impact(Level level, Player player, ItemStack stack) {
		int impact = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(IMPACT).map(stack::getEnchantmentLevel).orElse(0);
		if (impact <= 0 || level.isClientSide())
			return;
		Vec3 v = player.getDeltaMovement();
		float damage = (float) ((Math.abs(v.x) + Math.abs(v.y) + Math.abs(v.z)) * 3 * impact);
		for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, new AABB(player.position(), player.position()).inflate(1.5), e -> e != player)) {
			target.hurt(new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(IMPACT_DAMAGE), player), damage);
			level.playSound(null, player.blockPosition(), SoundEvents.GOAT_RAM_IMPACT, SoundSource.NEUTRAL, 1, 1);
			Vec3 away = player.position().subtract(target.position());
			if (away.lengthSqr() > 1.0E-4) {
				player.setDeltaMovement(away.normalize().scale(0.75));
				player.hurtMarked = true; // send the server-side velocity change to the client
			}
		}
	}
}
