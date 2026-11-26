package net.furtitwo.createstuffadditions.logic.armor;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import net.furtitwo.createstuffadditions.init.CreateSaModItems;

/**
 * Slime armour. Boots: no fall damage and bounce back up on landing (sneak to stop bouncing).
 * Helmet: cling to a ceiling right above the head, and bounce off walls when flying into them with an elytra.
 */
@EventBusSubscriber
public final class SlimeArmor {
	/** Downward speed of the last airborne tick, in the player's persistent data. */
	private static final String FALL_SPEED_KEY = "slimeFalling";
	private static final double MIN_BOUNCE_SPEED = 0.25;
	private static final double BOUNCE_FACTOR = 0.75;

	private SlimeArmor() {
	}

	/** Worn tick of the slime boots (both sides). */
	public static void onBootsTick(Level level, Player player) {
		CompoundTag data = player.getPersistentData();
		if (player.isShiftKeyDown()) {
			data.putDouble(FALL_SPEED_KEY, 0);
			return;
		}
		player.fallDistance = 0;
		if (!player.onGround()) {
			data.putDouble(FALL_SPEED_KEY, -player.getDeltaMovement().y());
			return;
		}
		double fallSpeed = data.getDouble(FALL_SPEED_KEY);
		if (fallSpeed <= MIN_BOUNCE_SPEED)
			return;
		Vec3 motion = player.getDeltaMovement();
		player.setDeltaMovement(new Vec3(motion.x(), fallSpeed * BOUNCE_FACTOR, motion.z()));
		playSlimeJump(level, player, 0.2f);
		if (player.isSprinting()) {
			Vec3 look = player.getLookAngle();
			motion = player.getDeltaMovement();
			player.setDeltaMovement(new Vec3(motion.x() + look.x * fallSpeed * BOUNCE_FACTOR, motion.y(), motion.z() + look.z * fallSpeed * BOUNCE_FACTOR));
		}
	}

	/** Worn tick of the slime helmet (both sides): stops falling while a solid block is two blocks above the feet. */
	public static void onHelmetTick(Level level, Player player) {
		if (player.isShiftKeyDown() || player.isSpectator() || player.onGround())
			return;
		BlockPos ceiling = BlockPos.containing(Math.floor(player.getX()), Math.floor(player.getY()) + 2, Math.floor(player.getZ()));
		if (!level.getBlockState(ceiling).canOcclude())
			return;
		player.fallDistance = 0;
		player.setDeltaMovement(new Vec3(player.getDeltaMovement().x(), 0.1, player.getDeltaMovement().z()));
	}

	/** Flying into a wall with the slime helmet: cancel the damage, take 0.2 instead and bounce back. */
	@SubscribeEvent
	public static void onIncomingDamage(LivingIncomingDamageEvent event) {
		LivingEntity entity = event.getEntity();
		DamageSource source = event.getSource();
		float amount = event.getAmount();
		if (entity.getItemBySlot(EquipmentSlot.HEAD).getItem() != CreateSaModItems.SLIME_HELMET.get() || !source.is(DamageTypes.FLY_INTO_WALL) || amount <= 0.5)
			return;
		event.setCanceled(true);
		Level level = entity.level();
		playSlimeJump(level, entity, 1f);
		// Re-enters this handler with 0.2, below the 0.5 threshold
		entity.hurt(new DamageSource(level.holderOrThrow(DamageTypes.FLY_INTO_WALL)), 0.2f);
		Vec3 look = entity.getLookAngle();
		entity.setDeltaMovement(new Vec3(look.x * -0.2 * amount, look.y * -0.2 * amount, look.z * -0.2 * amount));
		if (level instanceof ServerLevel server) {
			ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
			helmet.hurtAndBreak(1, server, null, item -> {
			});
		}
	}

	private static void playSlimeJump(Level level, LivingEntity entity, float volume) {
		if (!level.isClientSide())
			level.playSound(null, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), SoundEvents.SLIME_JUMP, SoundSource.NEUTRAL, volume, 4f);
		else
			level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.SLIME_JUMP, SoundSource.NEUTRAL, volume, 4f, false);
	}
}
