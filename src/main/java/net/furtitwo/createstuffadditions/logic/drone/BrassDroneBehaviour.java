package net.furtitwo.createstuffadditions.logic.drone;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.furtitwo.createstuffadditions.entity.BrassDroneEntity;
import net.furtitwo.createstuffadditions.entity.DrillModuleEntity;
import net.furtitwo.createstuffadditions.entity.FanModuleEntity;
import net.furtitwo.createstuffadditions.entity.MagnetModuleEntity;
import net.furtitwo.createstuffadditions.entity.VaultModuleEntity;
import net.furtitwo.createstuffadditions.init.CreateSaModEntities;
import net.furtitwo.createstuffadditions.init.CreateSaModItems;
import net.furtitwo.createstuffadditions.network.CreateSaModVariables;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Brass drone: placement from its item, owner following / aiming with the drone controller,
 * attachments added by right click, and recovery as an item when hit.
 */
public final class BrassDroneBehaviour {
	private static final String STATIC = "tagStatic";
	private static final String ROTATE = "tagRotate";
	private static final String REACH = "tagReach";

	/** Half size of the box (128 wide) in which the owner is searched. */
	private static final double OWNER_SEARCH_RADIUS = 128 / 2d;

	private BrassDroneBehaviour() {
	}

	/** Items that can be clicked onto a drone, in the order they are checked. */
	private enum Attachment {
		DRILL(CreateSaModItems.BRASS_DRILL_HEAD::get, CreateSaModEntities.DRILL_MODULE::get, DrillModuleEntity.class, vars -> vars.enableDrillDroneModule),
		MAGNET(CreateSaModItems.COPPER_MAGNET::get, CreateSaModEntities.MAGNET_MODULE::get, MagnetModuleEntity.class, vars -> vars.enableMagnetDroneModule),
		FAN(CreateSaModItems.FAN_COMPONENT::get, CreateSaModEntities.FAN_MODULE::get, FanModuleEntity.class, vars -> vars.enableFanDroneModule),
		VAULT(CreateSaModItems.VAULT_COMPONENT::get, CreateSaModEntities.VAULT_MODULE::get, VaultModuleEntity.class, vars -> vars.enableVaultDroneModule);

		final Supplier<Item> item;
		final Supplier<? extends EntityType<?>> entityType;
		final Class<? extends Entity> entityClass;
		final Predicate<CreateSaModVariables.MapVariables> enabled;

		Attachment(Supplier<Item> item, Supplier<? extends EntityType<?>> entityType, Class<? extends Entity> entityClass, Predicate<CreateSaModVariables.MapVariables> enabled) {
			this.item = item;
			this.entityType = entityType;
			this.entityClass = entityClass;
			this.enabled = enabled;
		}

		@Nullable
		static Attachment find(Level level, ItemStack stack) {
			for (Attachment attachment : values()) {
				if (stack.getItem() == attachment.item.get() && attachment.enabled.test(CreateSaModVariables.MapVariables.get(level)))
					return attachment;
			}
			return null;
		}
	}

	public static void onSpawn(Entity drone) {
		drone.setShiftKeyDown(true);
		drone.getPersistentData().putDouble(ROTATE, 1);
	}

	/** Hit by the owner or by any non-player entity: drops back as an item. */
	public static void onHurt(TamableAnimal drone, @Nullable Entity source) {
		if (source == null)
			return;
		if (!(source instanceof Player) || source == drone.getOwner())
			DroneCommon.dropAndDiscard(drone, CreateSaModItems.BRASS_DRONE_ITEM.get());
	}

	/** Drone item used on a block face: spawns a drone next to it, tamed by the player. */
	public static void placeFromItem(Level level, BlockPos clicked, Direction face, @Nullable Entity user, ItemStack stack) {
		if (face == null || user == null)
			return;
		double x = clicked.getX();
		double y = clicked.getY();
		double z = clicked.getZ();
		Vec3 pos = switch (face) {
			case UP -> new Vec3(x + 0.5, y + 1, z + 0.5);
			case DOWN -> new Vec3(x + 0.5, y - 1, z + 0.5);
			case NORTH -> new Vec3(x + 0.5, y, z - 1);
			case SOUTH -> new Vec3(x + 0.5, y, z + 1.5);
			case WEST -> new Vec3(x - 1, y, z + 0.5);
			case EAST -> new Vec3(x + 1.5, y, z + 0.5);
		};
		if (DroneCommon.mainHand(user).getItem() != stack.getItem())
			return;

		if (user instanceof LivingEntity living)
			living.swing(InteractionHand.MAIN_HAND, true);
		DroneCommon.playSound(level, pos.x, pos.y, pos.z, SoundEvents.METAL_PLACE, 1, 1);
		DroneCommon.spawnFacing(level, CreateSaModEntities.BRASS_DRONE.get(), BlockPos.containing(pos), user.getYRot() + 180);

		BrassDroneEntity placed = level.getEntitiesOfClass(BrassDroneEntity.class, AABB.ofSize(pos, 1, 1, 1), e -> true).stream()
				.min(Comparator.comparingDouble(e -> e.distanceToSqr(pos))).orElse(null);
		if (placed != null && user instanceof Player owner)
			placed.tame(owner);

		if (!DroneCommon.isCreative(user) && user instanceof LivingEntity living) {
			living.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
			if (living instanceof Player player)
				player.getInventory().setChanged();
		}
	}

	/** Right click with an attachment item mounts it, any other item toggles the static mode. */
	public static void onInteract(Entity drone, Player player, ItemStack stack) {
		Level level = drone.level();
		Attachment attachment = Attachment.find(level, stack);
		if (attachment == null) {
			drone.setShiftKeyDown(!drone.isShiftKeyDown());
			DroneCommon.playSound(level, drone.getX(), drone.getY(), drone.getZ(), SoundEvents.NOTE_BLOCK_BIT.value(), 0.5f, 3);
			return;
		}

		DroneCommon.playSound(level, drone.getX(), drone.getY(), drone.getZ(), SoundEvents.METAL_PLACE, 1, 1);
		Vec3 center = drone.position();
		DroneCommon.spawnFacing(level, attachment.entityType.get(), BlockPos.containing(center), drone.getYRot() + 180);
		level.getEntitiesOfClass(attachment.entityClass, new AABB(center, center).inflate(1 / 2d), e -> true).stream()
				.sorted(Comparator.<Entity>comparingDouble(e -> e.distanceToSqr(center)))
				.forEach(e -> e.startRiding(drone));

		if (DroneCommon.isCreative(player))
			return;
		if (stack.getItem() == player.getMainHandItem().getItem()) {
			shrinkHeld(player, InteractionHand.MAIN_HAND);
		} else if (stack.getItem() == player.getOffhandItem().getItem()) {
			shrinkHeld(player, InteractionHand.OFF_HAND);
		}
	}

	private static void shrinkHeld(Player player, InteractionHand hand) {
		ItemStack held = player.getItemInHand(hand);
		player.setItemInHand(hand, held.copyWithCount(held.getCount() - 1));
		player.getInventory().setChanged();
	}

	public static void onTick(TamableAnimal drone) {
		drone.fallDistance = 0;
		Vec3 motion = drone.getDeltaMovement();
		if (motion.y() <= -0.2)
			drone.setDeltaMovement(new Vec3(motion.x(), motion.y() + 0.1, motion.z()));

		CompoundTag data = drone.getPersistentData();
		if (drone.isShiftKeyDown()) {
			data.putBoolean(STATIC, true);
			return;
		}
		data.putBoolean(STATIC, false);

		LivingEntity owner = drone.getOwner();
		if (owner != null && isOwnerNearby(drone, owner))
			steerTowardsOwner(drone, owner);

		if (drone.onGround()) {
			data.putDouble(ROTATE, 0);
		} else {
			data.putDouble(ROTATE, (data.getDouble(ROTATE) + 1) % 2);
		}

	}

	private static boolean isOwnerNearby(Entity drone, Entity owner) {
		Vec3 center = drone.position();
		return !drone.level().getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(OWNER_SEARCH_RADIUS), e -> e == owner).isEmpty();
	}

	/** Controller held: follow the owner or aim along the owner's look. Controller only in the inventory: follow. */
	private static void steerTowardsOwner(Entity drone, LivingEntity owner) {
		ItemStack controller = owner.getMainHandItem();
		if (controller.getItem() != CreateSaModItems.DRONE_CONTROLLER.get())
			controller = owner.getOffhandItem();
		if (controller.getItem() == CreateSaModItems.DRONE_CONTROLLER.get()) {
			double reach = controller.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getDouble(REACH);
			if (reach == 0) {
				follow(drone, owner);
			} else {
				aim(drone, owner, 4 + reach);
			}
			return;
		}
		if (owner.getCapability(Capabilities.ItemHandler.ENTITY, null) instanceof IItemHandlerModifiable inventory) {
			for (int slot = 0; slot < inventory.getSlots(); slot++) {
				if (inventory.getStackInSlot(slot).getItem() == CreateSaModItems.DRONE_CONTROLLER.get()) {
					follow(drone, owner);
					break;
				}
			}
		}
	}

	/** Accelerates towards a point 2.8 blocks above the owner. */
	private static void follow(Entity drone, Entity owner) {
		double dx = owner.getX() - drone.getX();
		double dy = (owner.getY() + 2.8) - drone.getY();
		double dz = owner.getZ() - drone.getZ();
		double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
		Vec3 motion = drone.getDeltaMovement();
		drone.setDeltaMovement(new Vec3(motion.x() + (dx / distance) * 0.1, motion.y() + (dy / distance) * 0.1, motion.z() + (dz / distance) * 0.1));
	}

	/** Moves towards the point `reach` blocks along the owner's look direction. */
	private static void aim(Entity drone, Entity owner, double reach) {
		double pitch = negRadians(owner.getXRot());
		double yaw = negRadians(owner.getYRot());
		double targetX = owner.getX() + reach * Math.cos(pitch) * Math.sin(yaw);
		double targetY = owner.getY() + owner.getBbHeight() + reach * Math.sin(pitch);
		double targetZ = owner.getZ() + reach * Math.cos(pitch) * Math.cos(yaw);
		double dx = targetX - drone.getX();
		double dy = targetY - drone.getY();
		double dz = targetZ - drone.getZ();
		double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
		drone.setDeltaMovement(new Vec3((dx / distance) * 0.6, drone.getDeltaMovement().y() + (dy / distance) * 0.1, (dz / distance) * 0.6));
	}

	private static double negRadians(float degrees) {
		return ((-1) * degrees * Math.PI) / 180;
	}
}
