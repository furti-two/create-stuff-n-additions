package net.furtitwo.createstuffadditions.logic.drone;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

import net.furtitwo.createstuffadditions.network.CreateSaModVariables;
import net.furtitwo.createstuffadditions.init.CreateSaModItems;
import net.furtitwo.createstuffadditions.entity.MagnetModuleEntity;
import net.furtitwo.createstuffadditions.entity.BrassDroneEntity;

/**
 * Magnet module: attracts items and entities, holds what is two blocks below, and on a pulse action
 * ("tagPulseAction") tears the block two below out as a falling block.
 */
public final class MagnetModule extends DroneModule {
	public static final MagnetModule INSTANCE = new MagnetModule();

	private MagnetModule() {
		super(() -> CreateSaModItems.COPPER_MAGNET.get(), 3, false);
	}

	@Override
	protected void onMountedTick(LevelAccessor world, Entity module, BrassDroneEntity drone) {
		Controller controller = activeController(drone);
		if (controller == null)
			return;
		double x = module.getX();
		double y = module.getY();
		double z = module.getZ();
		for (Entity target : entitiesAround(world, new Vec3(x, y - 1, z), 4)) {
			if (target instanceof ItemEntity) {
				target.setDeltaMovement(new Vec3((x - target.getX()) * 0.2, ((y - 1) - target.getY()) * 0.2, (z - target.getZ()) * 0.2));
			} else if (!(target instanceof MagnetModuleEntity) && !(target instanceof BrassDroneEntity) && !hasOwnerName(target, controller.owner())) {
				Vec3 motion = target.getDeltaMovement();
				target.setDeltaMovement(new Vec3(motion.x() + (x - target.getX()) * 0.005, motion.y() + ((y - 1) - target.getY()) * 0.005, motion.z() + (z - target.getZ()) * 0.005));
			}
		}
		for (Entity target : entitiesAround(world, new Vec3(x, y - 2, z), 1)) {
			target.setDeltaMovement(new Vec3((x - target.getX()) * 0.5, ((y - 2) - target.getY()) * 0.5, (z - target.getZ()) * 0.5));
		}
		BlockPos pos = module.blockPosition().below(2);
		BlockState state = world.getBlockState(pos);
		float hardness = state.getDestroySpeed(world, pos);
		if (hardness >= 0 && hardness < 50 && !state.is(Blocks.SPAWNER) && !world.isEmptyBlock(pos) && !CreateSaModVariables.MapVariables.get(world).MagnetModuleBlockLimitation
				&& hasFlag(controller.stack(), "tagPulseAction")) {
			if (world instanceof ServerLevel level)
				FallingBlockEntity.fall(level, pos, state);
			world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
			cooldown(controller.owner(), controller.stack());
		}
	}
}
