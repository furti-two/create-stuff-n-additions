package net.furtitwo.createstuffadditions.logic.drone;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * Helpers shared by the brass drone, the brass cube and the drone attachments.
 */
public final class DroneCommon {
	private DroneCommon() {
	}

	/** True when the entity is a player in creative mode (works on both logical sides). */
	public static boolean isCreative(@Nullable Entity entity) {
		return entity instanceof Player player && player.isCreative();
	}

	public static ItemStack mainHand(Entity entity) {
		return entity instanceof LivingEntity living ? living.getMainHandItem() : ItemStack.EMPTY;
	}

	public static ItemStack offHand(Entity entity) {
		return entity instanceof LivingEntity living ? living.getOffhandItem() : ItemStack.EMPTY;
	}

	/** Removes the entity, server side only. */
	public static void discardOnServer(Entity entity) {
		if (!entity.level().isClientSide())
			entity.discard();
	}

	/** Drops the given item at the entity position (pickup delay 10) and removes the entity. */
	public static void dropAndDiscard(Entity entity, ItemLike item) {
		if (entity.level() instanceof ServerLevel level) {
			ItemEntity drop = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(item));
			drop.setPickUpDelay(10);
			level.addFreshEntity(drop);
		}
		discardOnServer(entity);
	}

	/** Server: broadcast sound at the block containing the position. Client: local sound at the exact position. */
	public static void playSound(Level level, double x, double y, double z, SoundEvent sound, float volume, float pitch) {
		if (!level.isClientSide()) {
			level.playSound(null, BlockPos.containing(x, y, z), sound, SoundSource.NEUTRAL, volume, pitch);
		} else {
			level.playLocalSound(x, y, z, sound, SoundSource.NEUTRAL, volume, pitch, false);
		}
	}

	/** Spawns an entity (server only), turned to the given yaw and motionless. */
	public static void spawnFacing(Level level, EntityType<?> type, BlockPos pos, float yaw) {
		if (!(level instanceof ServerLevel serverLevel))
			return;
		Entity spawned = type.spawn(serverLevel, pos, MobSpawnType.MOB_SUMMONED);
		if (spawned != null) {
			spawned.setYRot(yaw);
			spawned.setYBodyRot(yaw);
			spawned.setYHeadRot(yaw);
			spawned.setDeltaMovement(0, 0, 0);
		}
	}
}
