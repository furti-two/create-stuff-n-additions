package net.furtitwo.createstuffadditions.logic.armor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.component.DataComponents;

import net.furtitwo.createstuffadditions.init.CreateSaModItems;

import com.simibubi.create.content.equipment.armor.DivingHelmetItem;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.AllTags.AllItemTags;

/**
 * Full netherite set (netherite diving helmet, netherite backtank with air or netherite jetpack, netherite leggings and boots)
 * makes the wearer fire immune. Each worn piece sets one bit in the persistent data; {@code NetheriteFireImuneMixin} reads the flag.
 */
@EventBusSubscriber
public final class NetheriteFireImmunity {
	public static final String NETHERITE_DIVING_BITS_KEY = "CreateStuffNetheriteDivingBits";
	public static final String FIRE_IMMUNE_KEY = "CreateStuffFireImmune";

	@SubscribeEvent
	public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
		EquipmentSlot slot = event.getSlot();
		if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) {
			return;
		}
		LivingEntity entity = event.getEntity();
		ItemStack to = event.getTo();
		if (slot == EquipmentSlot.HEAD) {
			if (isNetheriteDivingHelmet(to)) {
				setBit(entity, slot);
			} else {
				clearBit(entity, slot);
			}
		} else if (slot == EquipmentSlot.CHEST) {
			if (isNetheriteBacktank(to) && BacktankUtil.hasAirRemaining(to)) {
				setBit(entity, slot);
			} else if (entity.getItemBySlot(EquipmentSlot.CHEST).is(ItemTags.create(ResourceLocation.parse("create_sa:netherite_chest_substitute")))) {
				setBit(entity, slot);
			} else {
				clearBit(entity, slot);
			}
		} else if (slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET) {
			if (isNetheriteArmor(to)) {
				setBit(entity, slot);
			} else {
				clearBit(entity, slot);
			}
		}
	}

	public static boolean isNetheriteDivingHelmet(ItemStack stack) {
		return stack.getItem() instanceof DivingHelmetItem && isNetheriteArmor(stack);
	}

	public static boolean isNetheriteBacktank(ItemStack stack) {
		return stack.is(AllItemTags.PRESSURIZED_AIR_SOURCES.tag) && isNetheriteArmor(stack);
	}

	public static boolean isNetheriteArmor(ItemStack stack) {
		return stack.getItem() instanceof ArmorItem && stack.has(DataComponents.FIRE_RESISTANT);
	}

	public static void setBit(LivingEntity entity, EquipmentSlot slot) {
		CompoundTag nbt = entity.getPersistentData();
		byte bits = nbt.getByte(NETHERITE_DIVING_BITS_KEY);
		if ((bits & 0b1111) == 0b1111) {
			return;
		}
		bits |= 1 << slot.getIndex();
		nbt.putByte(NETHERITE_DIVING_BITS_KEY, bits);
		if ((bits & 0b1111) == 0b1111) {
			setFireImmune(entity, true);
		}
	}

	public static void clearBit(LivingEntity entity, EquipmentSlot slot) {
		CompoundTag nbt = entity.getPersistentData();
		if (!nbt.contains(NETHERITE_DIVING_BITS_KEY)) {
			return;
		}
		byte bits = nbt.getByte(NETHERITE_DIVING_BITS_KEY);
		boolean prevFullSet = (bits & 0b1111) == 0b1111;
		bits &= ~(1 << slot.getIndex());
		nbt.putByte(NETHERITE_DIVING_BITS_KEY, bits);
		if (prevFullSet) {
			setFireImmune(entity, false);
		}
	}

	// TODO: sync to the client
	// The feature works without syncing because health and burning are calculated server-side and synced through vanilla code.
	// This method will not be called when the entity is wearing a full diving set on creation because the NBT values are persistent.
	public static void setFireImmune(LivingEntity entity, boolean fireImmune) {
		entity.getPersistentData().putBoolean(FIRE_IMMUNE_KEY, fireImmune);
	}
}
