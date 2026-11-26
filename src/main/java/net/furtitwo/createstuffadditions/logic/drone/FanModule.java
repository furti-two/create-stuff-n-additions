package net.furtitwo.createstuffadditions.logic.drone;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;

import net.furtitwo.createstuffadditions.init.CreateSaModItems;
import net.furtitwo.createstuffadditions.entity.BrassDroneEntity;

/** Fan module: pushes entities away horizontally from the drone while the controller orders an action. */
public final class FanModule extends DroneModule {
	public static final FanModule INSTANCE = new FanModule();

	private FanModule() {
		super(() -> CreateSaModItems.FAN_COMPONENT.get(), 3, true);
	}

	@Override
	protected void onMountedTick(LevelAccessor world, Entity module, BrassDroneEntity drone) {
		Controller controller = activeController(drone);
		if (controller == null) {
			module.getPersistentData().putBoolean("tagActive", false);
			return;
		}
		// Projectiles are only deflected when the controller is in the main hand
		boolean deflectProjectiles = controller.hand() == InteractionHand.MAIN_HAND;
		double x = module.getX();
		double z = module.getZ();
		for (Entity target : entitiesAround(world, new Vec3(x, module.getY() - 1, z), 4)) {
			Vec3 motion = target.getDeltaMovement();
			double dx = target.getX() - x;
			double dz = target.getZ() - z;
			if (target instanceof ItemEntity) {
				target.setDeltaMovement(new Vec3(dx * 0.2, motion.y(), dz * 0.2));
			} else if (deflectProjectiles && target instanceof ThrowableProjectile) {
				target.setDeltaMovement(new Vec3(motion.x() + dx * 2, motion.y(), motion.z() + dz * 2));
			} else if (!(target instanceof BrassDroneEntity) && !hasOwnerName(target, controller.owner())) {
				target.setDeltaMovement(new Vec3(motion.x() + dx * 0.1, motion.y(), motion.z() + dz * 0.1));
			}
		}
		module.getPersistentData().putBoolean("tagActive", true);
	}
}
