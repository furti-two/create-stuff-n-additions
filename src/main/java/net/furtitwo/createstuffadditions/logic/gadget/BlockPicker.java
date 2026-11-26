package net.furtitwo.createstuffadditions.logic.gadget;

import net.furtitwo.createstuffadditions.compat.sable.SubLevels;
import net.neoforged.neoforge.fluids.FluidStack;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.furtitwo.createstuffadditions.CreateSaMod;
import net.furtitwo.createstuffadditions.entity.BrassCubeREntity;
import net.furtitwo.createstuffadditions.entity.LiftedBlockEntity;
import net.furtitwo.createstuffadditions.fuel.FuelStorage;
import net.furtitwo.createstuffadditions.init.CreateSaModEntities;
import net.furtitwo.createstuffadditions.item.CustomFluidHandlerItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Block Picker (gravity gun): right-click toggles the beam, right-click on a block lifts it as a
 * {@link LiftedBlockEntity}, holding the active beam drags the targeted entity in front of the player (spending water),
 * and swinging throws it. The Gravity Gun enchantment extends the targets to living entities and arrows.
 */
public final class BlockPicker {
	private static final String ACTIVE = "tagAction";
	private static final String COOLDOWN = "tagCooldown";
	private static final ResourceKey<Enchantment> GRAVITY_GUN = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("create_sa:gravity_gun"));
	/** Reach of the beam, in blocks. */
	private static final double REACH = 3;
	/** Water (mB) drained every 6 ticks of holding a target. */
	private static final int DRAIN_MB = 10;

	private BlockPicker() {
	}

	/** Right-click in the air: toggles the beam. */
	public static void onUse(Level level, double x, double y, double z, ItemStack stack) {
		boolean active = isActive(stack);
		setActive(stack, !active);
		GadgetSounds.play(level, x, y, z, active ? SoundEvents.BEACON_DEACTIVATE : SoundEvents.BEACON_ACTIVATE, 0.1f, -4);
	}

	/** Right-click on a block: lifts it (server side) when it is breakable, then reactivates the beam next tick. */
	public static void onUseOnBlock(Level level, BlockPos pos, ItemStack stack) {
		GadgetSounds.play(level, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEACON_ACTIVATE, 0.1f, -4);
		if (FuelStorage.amount(stack, 0) > 0) {
			BlockState state = level.getBlockState(pos);
			float hardness = state.getDestroySpeed(level, pos);
			if (hardness >= 0 && hardness < 50 && state.getBlock() != Blocks.SPAWNER && !level.isEmptyBlock(pos) && level.isEmptyBlock(pos.above()) && !level.isClientSide()) {
				if (level instanceof ServerLevel serverLevel) {
					// The clicked block may be on a Sable structure (pos is then in its plot): lift it where it is in the world
					BlockPos spawnPos = BlockPos.containing(SubLevels.toWorld(level, Vec3.atBottomCenterOf(pos)));
					Entity lifted = CreateSaModEntities.LIFTED_BLOCK.get().spawn(serverLevel, spawnPos, MobSpawnType.MOB_SUMMONED);
					if (lifted != null) {
						lifted.setDeltaMovement(0, 0, 0);
						lifted.setYBodyRot(0f);
						if (lifted instanceof LiftedBlockEntity liftedBlock)
							liftedBlock.getEntityData().set(LiftedBlockEntity.DATA_BLOCK, Optional.of(state));
					}
				}
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
				level.updateNeighborsAt(pos, level.getBlockState(pos).getBlock());
			}
		}
		CreateSaMod.queueServerWork(1, () -> setActive(stack, true));
	}

	/** Swing while the beam is active: releases the beam and throws the targeted entity along the look direction. */
	public static void onSwing(LivingEntity entity, ItemStack stack) {
		if (!isActive(stack) || FuelStorage.amount(stack, 0) <= 0)
			return;
		Level level = entity.level();
		setActive(stack, false);
		GadgetSounds.play(level, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BEACON_DEACTIVATE, 0.3f, -4);
		Vec3 target = blockTarget(entity);
		Vec3 look = entity.getLookAngle();
		boolean sprinting = entity.isSprinting();
		if (hasGravityGun(level, stack)) {
			Vec3 push = look.scale(sprinting ? 1 : 0.5);
			for (Entity candidate : nearby(level, entity)) {
				if (isClosest(level, PrimedTnt.class, target, candidate)
						|| isClosest(level, LivingEntity.class, target, candidate) && !sameName(entity, candidate)
						|| isClosest(level, Arrow.class, target, candidate)) {
					candidate.setDeltaMovement(candidate.getDeltaMovement().add(push));
					break;
				}
			}
		} else {
			Vec3 push = look.scale(sprinting ? 0.4 : 0.2);
			for (Entity candidate : nearby(level, entity)) {
				if (isClosest(level, BrassCubeREntity.class, target, candidate) || isClosest(level, PrimedTnt.class, target, candidate)) {
					candidate.setDeltaMovement(candidate.getDeltaMovement().add(push));
					break;
				}
			}
		}
	}

	/** Every tick while selected: holds the targeted entity in front of the player, or turns the beam off when empty. */
	public static void onHeldTick(Level level, Entity entity, ItemStack stack) {
		if (!isActive(stack) || FuelStorage.amount(stack, 0) <= 0) {
			setActive(stack, false);
			return;
		}
		double yaw = -entity.getYRot() * Math.PI / 180;
		double pitch = -entity.getXRot() * Math.PI / 180;
		double aimX = entity.getX() + REACH * Math.cos(pitch) * Math.sin(yaw);
		double aimY = entity.getY() + entity.getBbHeight() + REACH * Math.sin(pitch);
		double aimZ = entity.getZ() + REACH * Math.cos(pitch) * Math.cos(yaw);
		Vec3 blockTarget = blockTarget(entity);
		boolean gravityGun = hasGravityGun(level, stack);
		for (Entity candidate : nearby(level, entity)) {
			// Entities are held with their centre on the aim point; lifted blocks snap to the block in sight.
			Vec3 aim = new Vec3(aimX, aimY - candidate.getBbHeight() * 0.5, aimZ);
			if (isClosest(level, BrassCubeREntity.class, aim, candidate)) {
				pullTo(candidate, aim, true);
			} else if (isClosest(level, LiftedBlockEntity.class, blockTarget, candidate)) {
				pullTo(candidate, blockTarget, false);
			} else if (isClosest(level, PrimedTnt.class, aim, candidate)) {
				pullTo(candidate, aim, false);
			} else if (gravityGun && (isClosest(level, LivingEntity.class, aim, candidate) && !sameName(entity, candidate) || isClosest(level, Arrow.class, aim, candidate))) {
				pullTo(candidate, aim, true);
			} else {
				continue;
			}
			tickCooldown(stack);
			break;
		}
	}

	private static void pullTo(Entity candidate, Vec3 target, boolean resetFall) {
		candidate.setDeltaMovement(target.subtract(candidate.position()).scale(0.6));
		if (resetFall)
			candidate.fallDistance = 0;
	}

	/** Drains water every 6th tick of holding something. */
	private static void tickCooldown(ItemStack stack) {
		double cooldown = tag(stack).getDouble(COOLDOWN);
		if (cooldown >= 5) {
			CustomFluidHandlerItemStack.drainTank(stack, new FluidStack(Fluids.WATER, DRAIN_MB), false);
			CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putDouble(COOLDOWN, 0));
		} else {
			CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putDouble(COOLDOWN, cooldown + 1));
		}
	}

	/** Centre-top of the block (or air cell) at the end of the beam, in world space (also when it is on a Sable structure). */
	private static Vec3 blockTarget(Entity entity) {
		Vec3 eye = entity.getEyePosition(1f);
		BlockPos hit = entity.level().clip(new ClipContext(eye, eye.add(entity.getViewVector(1f).scale(REACH)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getBlockPos();
		return SubLevels.toWorld(entity.level(), new Vec3(hit.getX() + 0.5, hit.getY() + 0.3, hit.getZ() + 0.5));
	}

	/** Every entity within 4 blocks of the holder's chest, closest first. */
	private static List<Entity> nearby(Level level, Entity entity) {
		Vec3 center = new Vec3(entity.getX(), entity.getY() + 1.3, entity.getZ());
		return level.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(4), e -> true).stream().sorted(Comparator.comparingDouble(e -> e.distanceToSqr(center))).toList();
	}

	/** True when {@code candidate} is the entity of {@code type} closest to {@code target} (searched in an 8-block box). */
	private static <T extends Entity> boolean isClosest(Level level, Class<T> type, Vec3 target, Entity candidate) {
		if (!type.isInstance(candidate))
			return false;
		return level.getEntitiesOfClass(type, AABB.ofSize(target, 8, 8, 8), e -> true).stream().min(Comparator.comparingDouble((T e) -> e.distanceToSqr(target))).orElse(null) == candidate;
	}

	private static boolean sameName(Entity a, Entity b) {
		return a.getDisplayName().getString().equals(b.getDisplayName().getString());
	}

	private static boolean hasGravityGun(Level level, ItemStack stack) {
		return stack.getEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(GRAVITY_GUN)) != 0;
	}

	private static CompoundTag tag(ItemStack stack) {
		return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
	}

	private static boolean isActive(ItemStack stack) {
		return tag(stack).getBoolean(ACTIVE);
	}

	private static void setActive(ItemStack stack, boolean active) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean(ACTIVE, active));
	}
}
