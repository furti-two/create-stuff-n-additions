package net.furtitwo.createstuffadditions.logic.drone;

import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.capabilities.Capabilities;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.component.DataComponents;

import net.furtitwo.createstuffadditions.init.CreateSaModItems;
import net.furtitwo.createstuffadditions.entity.BrassDroneEntity;

import javax.annotation.Nullable;

import java.util.function.Supplier;
import java.util.List;
import java.util.Comparator;

/**
 * Shared behaviour of the modules mounted on a Brass Drone: follow the drone rotation, break off (dropping the
 * component and its whole inventory) when no longer riding a drone, pick up items right below, and drop
 * everything when hurt. Subclasses only provide what the module does while mounted.
 */
public abstract class DroneModule {
	private final Supplier<Item> component;
	private final int inventorySlots;
	private final boolean collectAtBlockCenter;

	/**
	 * @param component            item dropped when the module is destroyed
	 * @param inventorySlots       slots dropped when hurt or broken off, and filled by the item pickup
	 * @param collectAtBlockCenter true to centre the pickup box on the block below, false on the exact position one block lower
	 */
	protected DroneModule(Supplier<Item> component, int inventorySlots, boolean collectAtBlockCenter) {
		this.component = component;
		this.inventorySlots = inventorySlots;
		this.collectAtBlockCenter = collectAtBlockCenter;
	}

	/** Behaviour while riding a Brass Drone. */
	protected abstract void onMountedTick(LevelAccessor world, Entity module, BrassDroneEntity drone);

	public static void onInitialSpawn(Entity module) {
		module.getPersistentData().putBoolean("tagActive", false);
	}

	public final void onTick(LevelAccessor world, Entity module) {
		Entity vehicle = module.getVehicle();
		if (vehicle != null)
			alignWith(module, vehicle);
		if (vehicle instanceof BrassDroneEntity drone) {
			onMountedTick(world, module, drone);
		} else {
			breakOff(world, module);
		}
		collectItems(world, module);
	}

	public final void onHurt(LevelAccessor world, double x, double y, double z, Entity module, @Nullable Entity source) {
		if (source == null)
			return;
		if (module.isPassenger() && source instanceof Player && source != ownerOf(module.getVehicle()))
			return;
		dropContents(world, x, y, z, module, inventorySlots);
		if (!module.level().isClientSide())
			module.discard();
	}

	private static void alignWith(Entity module, Entity vehicle) {
		module.setYRot(vehicle.getYRot());
		module.setXRot(0);
		module.setYBodyRot(module.getYRot());
		module.setYHeadRot(module.getYRot());
		module.yRotO = module.getYRot();
		module.xRotO = module.getXRot();
		if (module instanceof LivingEntity living) {
			living.yBodyRotO = living.getYRot();
			living.yHeadRotO = living.getYRot();
		}
	}

	private void breakOff(LevelAccessor world, Entity module) {
		dropContents(world, module.getX(), module.getY(), module.getZ(), module, inventorySlots);
		if (world instanceof Level level) {
			if (!level.isClientSide()) {
				level.playSound(null, module.blockPosition(), SoundEvents.SHIELD_BREAK, SoundSource.NEUTRAL, 1, 1);
			} else {
				level.playLocalSound(module.getX(), module.getY(), module.getZ(), SoundEvents.SHIELD_BREAK, SoundSource.NEUTRAL, 1, 1, false);
			}
		}
		if (!module.level().isClientSide())
			module.discard();
	}

	/** Drops the component plus a copy of the first {@code slots} inventory slots (the inventory itself is left as is). */
	private void dropContents(LevelAccessor world, double x, double y, double z, Entity module, int slots) {
		if (!(world instanceof ServerLevel level))
			return;
		spawnItem(level, x, y, z, new ItemStack(component.get()));
		IItemHandlerModifiable inventory = inventoryOf(module);
		for (int slot = 0; slot < slots; slot++)
			spawnItem(level, x, y, z, slotStack(inventory, slot).copy());
	}

	private static void spawnItem(ServerLevel level, double x, double y, double z, ItemStack stack) {
		ItemEntity item = new ItemEntity(level, x, y, z, stack);
		item.setPickUpDelay(10);
		level.addFreshEntity(item);
	}

	/** Absorbs the item entities in the block below into the first slot that is empty or holds the same item with less than 64. */
	private void collectItems(LevelAccessor world, Entity module) {
		Vec3 center = collectAtBlockCenter
				? new Vec3(Math.floor(module.getX()) + 0.5, Math.floor(module.getY()) - 1 + 0.5, Math.floor(module.getZ()) + 0.5)
				: new Vec3(module.getX(), module.getY() - 1, module.getZ());
		IItemHandlerModifiable inventory = inventoryOf(module);
		for (Entity found : entitiesAround(world, center, 0.5)) {
			if (!(found instanceof ItemEntity itemEntity))
				continue;
			ItemStack picked = itemEntity.getItem();
			for (int slot = 0; slot < inventorySlots; slot++) {
				ItemStack current = slotStack(inventory, slot);
				if ((current.isEmpty() || current.getItem() == picked.getItem()) && current.getCount() < 64) {
					if (inventory != null) {
						ItemStack merged = picked.copy();
						merged.setCount(current.getCount() + picked.getCount());
						inventory.setStackInSlot(slot, merged);
					}
					if (!found.level().isClientSide())
						found.discard();
					break;
				}
			}
		}
	}

	@Nullable
	private static IItemHandlerModifiable inventoryOf(Entity module) {
		return module.getCapability(Capabilities.ItemHandler.ENTITY, null) instanceof IItemHandlerModifiable handler ? handler : null;
	}

	private static ItemStack slotStack(@Nullable IItemHandlerModifiable inventory, int slot) {
		return inventory != null ? inventory.getStackInSlot(slot) : ItemStack.EMPTY;
	}

	/** Entities whose bounding box touches a cube of half-size {@code radius} around {@code center}, nearest first. */
	protected static List<Entity> entitiesAround(LevelAccessor world, Vec3 center, double radius) {
		return world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(radius), e -> true).stream()
				.sorted(Comparator.comparingDouble(e -> e.distanceToSqr(center))).toList();
	}

	@Nullable
	private static LivingEntity ownerOf(@Nullable Entity vehicle) {
		return vehicle instanceof TamableAnimal tamable ? tamable.getOwner() : null;
	}

	/** True when {@code target} shows the same display name as {@code owner} (used to spare the drone's owner). */
	protected static boolean hasOwnerName(Entity target, LivingEntity owner) {
		return target.getDisplayName().getString().equals(owner.getDisplayName().getString());
	}

	protected static void cooldown(LivingEntity owner, ItemStack controller) {
		if (owner instanceof Player player)
			player.getCooldowns().addCooldown(controller.getItem(), 20);
	}

	protected static boolean hasFlag(ItemStack controller, String key) {
		return controller.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean(key);
	}

	/**
	 * The owner's Drone Controller currently ordering an action ("tagAction"), main hand first, or {@code null} when
	 * the drone is static or no hand holds an active controller.
	 */
	@Nullable
	protected static Controller activeController(BrassDroneEntity drone) {
		if (drone.getPersistentData().getBoolean("tagStatic"))
			return null;
		LivingEntity owner = drone.getOwner();
		if (owner == null)
			return null;
		for (InteractionHand hand : InteractionHand.values()) {
			ItemStack stack = owner.getItemInHand(hand);
			if (stack.getItem() == CreateSaModItems.DRONE_CONTROLLER.get() && hasFlag(stack, "tagAction"))
				return new Controller(owner, stack, hand);
		}
		return null;
	}

	protected record Controller(LivingEntity owner, ItemStack stack, InteractionHand hand) {
	}
}
