package net.furtitwo.createstuffadditions.logic.drone;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.BlockPos;

import net.furtitwo.createstuffadditions.init.CreateSaModItems;
import net.furtitwo.createstuffadditions.entity.DrillModuleEntity;
import net.furtitwo.createstuffadditions.entity.BrassDroneEntity;

/** Drill module: hurts mobs in the block below and mines that block on each controller action. */
public final class DrillModule extends DroneModule {
	public static final DrillModule INSTANCE = new DrillModule();

	private DrillModule() {
		super(() -> CreateSaModItems.BRASS_DRILL_HEAD.get(), 3, true);
	}

	@Override
	protected void onMountedTick(LevelAccessor world, Entity module, BrassDroneEntity drone) {
		Controller controller = activeController(drone);
		if (controller == null) {
			module.getPersistentData().putBoolean("tagActive", false);
			return;
		}
		BlockPos below = module.blockPosition().below();
		float damage = controller.hand() == InteractionHand.MAIN_HAND ? 6 : 1;
		Vec3 center = Vec3.atCenterOf(below);
		for (Entity target : entitiesAround(world, center, 0.5)) {
			if (target instanceof Mob && !(target instanceof DrillModuleEntity) && !(target instanceof BrassDroneEntity) && !hasOwnerName(target, controller.owner()))
				target.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), damage);
		}
		BlockState state = world.getBlockState(below);
		float hardness = state.getDestroySpeed(world, below);
		if (hardness >= 0 && hardness < 50 && !world.isEmptyBlock(below)) {
			Block.dropResources(state, world, below, null);
			world.destroyBlock(below, false);
			CustomData.update(DataComponents.CUSTOM_DATA, controller.stack(), tag -> tag.putBoolean("tagAction", false));
			cooldown(controller.owner(), controller.stack());
		}
		module.getPersistentData().putBoolean("tagActive", true);
	}
}
