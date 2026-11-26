package net.furtitwo.createstuffadditions.world.inventory;

import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.capabilities.Capabilities;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;

import net.furtitwo.createstuffadditions.init.CreateSaModMenus;
import net.furtitwo.createstuffadditions.entity.VaultModuleEntity;
import net.furtitwo.createstuffadditions.entity.MagnetModuleEntity;
import net.furtitwo.createstuffadditions.entity.FanModuleEntity;
import net.furtitwo.createstuffadditions.entity.DrillModuleEntity;

import java.util.function.Supplier;
import java.util.Map;
import java.util.HashMap;

public class BrassDroneGUIMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
	public static final HashMap<String, Object> guistate = new HashMap<>();
	public final Level world;
	public final Player entity;
	public int entityId;
	private IItemHandler internal;
	private final Map<Integer, Slot> customSlots = new HashMap<>();
	private Entity boundEntity = null;
	private boolean bound = false;

	public BrassDroneGUIMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		super(CreateSaModMenus.BRASS_DRONE_GUI.get(), id);
		this.entity = inv.player;
		this.world = inv.player.level();
		this.internal = new ItemStackHandler(3);
		if (extraData != null) {
			// The module entities write: player BlockPos, a hand byte, then the entity id (see *ModuleEntity.mobInteract)
			extraData.readBlockPos();
			extraData.readByte();
			this.entityId = extraData.readVarInt();
			this.boundEntity = this.world.getEntity(this.entityId);
			if (this.boundEntity != null) {
				IItemHandler cap = this.boundEntity.getCapability(Capabilities.ItemHandler.ENTITY);
				if (cap != null) {
					this.internal = cap;
					this.bound = true;
				}
			}
		}
		this.customSlots.put(0, this.addSlot(new SlotItemHandler(internal, 0, 62, 18)));
		this.customSlots.put(1, this.addSlot(new SlotItemHandler(internal, 1, 80, 18)));
		this.customSlots.put(2, this.addSlot(new SlotItemHandler(internal, 2, 98, 18)));
		// Player inventory
		for (int row = 0; row < 3; ++row) {
			for (int column = 0; column < 9; ++column) {
				this.addSlot(new Slot(inv, column + (row + 1) * 9, 8 + column * 18, 50 + row * 18));
			}
		}
		// Hotbar
		for (int slot = 0; slot < 9; ++slot) {
			this.addSlot(new Slot(inv, slot, 8 + slot * 18, 108));
		}
	}

	@Override
	public boolean stillValid(Player player) {
		Entity entity = this.world.getEntity(this.entityId);
		return (entity instanceof FanModuleEntity || entity instanceof VaultModuleEntity || entity instanceof MagnetModuleEntity || entity instanceof DrillModuleEntity) && entity.isAlive();
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			itemstack = slotStack.copy();
			// Custom inventory -> player
			if (index < 3) {
				if (!this.moveItemStackTo(slotStack, 3, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
				slot.onQuickCraft(slotStack, itemstack);
			} else {
				// Player -> custom inventory
				if (!this.moveItemStackTo(slotStack, 0, 3, false)) {
					return ItemStack.EMPTY;
				}
			}
			if (slotStack.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
			if (slotStack.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(player, slotStack);
		}
		return itemstack;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		if (!bound && player instanceof ServerPlayer serverPlayer) {
			if (!serverPlayer.isAlive() || serverPlayer.hasDisconnected()) {
				for (int i = 0; i < internal.getSlots(); ++i) {
					player.drop(internal.getStackInSlot(i), false);
					if (internal instanceof IItemHandlerModifiable modifiable) {
						modifiable.setStackInSlot(i, ItemStack.EMPTY);
					}
				}
			} else {
				for (int i = 0; i < internal.getSlots(); ++i) {
					player.getInventory().placeItemBackInInventory(internal.getStackInSlot(i));
					if (internal instanceof IItemHandlerModifiable modifiable) {
						modifiable.setStackInSlot(i, ItemStack.EMPTY);
					}
				}
			}
		}
	}

	public Map<Integer, Slot> get() {
		return this.customSlots;
	}
}
