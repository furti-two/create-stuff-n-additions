package net.furtitwo.createstuffadditions.logic.drone;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import net.furtitwo.createstuffadditions.init.CreateSaModBlocks;
import net.furtitwo.createstuffadditions.init.CreateSaModEntities;
import net.furtitwo.createstuffadditions.init.CreateSaModItems;

import javax.annotation.Nullable;

/**
 * Brass cube: the placeable block turns into a pushable entity that takes 3 hits to break
 * and bounces like a ball when named "ball".
 */
public final class BrassCubeBehaviour {
	private static final String HEALTH = "tagHealth";
	private static final String BALL = "tagBall";
	private static final String BOUNCE = "tagBounce";

	private BrassCubeBehaviour() {
	}

	/** Block placed (onPlace and setPlacedBy): replace the block by the cube entity. */
	public static void onBlockPlaced(Level level, BlockPos pos) {
		level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
		if (level instanceof ServerLevel serverLevel)
			CreateSaModEntities.BRASS_CUBE_R.get().spawn(serverLevel, pos, MobSpawnType.MOB_SUMMONED);
	}

	public static void onSpawn(Entity cube) {
		cube.getPersistentData().putDouble(HEALTH, 3);
	}

	public static void onDeath(Entity cube) {
		DroneCommon.discardOnServer(cube);
	}

	public static void onHurt(Entity cube, @Nullable Entity source) {
		if (source == null)
			return;
		boolean holdsPicker = DroneCommon.mainHand(source).getItem() == CreateSaModItems.BLOCK_PICKER.get();
		CompoundTag data = cube.getPersistentData();
		if (DroneCommon.isCreative(source)) {
			if (!holdsPicker)
				DroneCommon.discardOnServer(cube);
		} else if (data.getDouble(HEALTH) <= 0) {
			DroneCommon.dropAndDiscard(cube, CreateSaModBlocks.BRASS_CUBE.get());
		} else if (!holdsPicker) {
			data.putDouble(HEALTH, data.getDouble(HEALTH) - 1);
		}
	}

	public static void onTick(LivingEntity cube) {
		cube.setYRot(0);
		cube.setXRot(0);
		cube.setYBodyRot(0);
		cube.setYHeadRot(0);
		cube.yRotO = 0;
		cube.xRotO = 0;
		cube.yBodyRotO = 0;
		cube.yHeadRotO = 0;

		CompoundTag data = cube.getPersistentData();
		if (cube.getDisplayName().getString().equals("ball")) {
			data.putBoolean(BALL, true);
			if (!cube.onGround())
				data.putDouble(BOUNCE, -cube.getDeltaMovement().y());
			if (cube.isShiftKeyDown())
				data.putDouble(BOUNCE, 0);
			if (cube.onGround() && !cube.isShiftKeyDown() && data.getDouble(BOUNCE) > 0.5) {
				Vec3 motion = cube.getDeltaMovement();
				cube.setDeltaMovement(new Vec3(motion.x(), data.getDouble(BOUNCE) * 0.75, motion.z()));
			}
		} else {
			data.putBoolean(BALL, false);
		}
		cube.fallDistance = 0;
	}
}
