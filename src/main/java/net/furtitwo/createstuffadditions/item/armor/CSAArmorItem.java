package net.furtitwo.createstuffadditions.item.armor;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import net.furtitwo.createstuffadditions.item.ShiftTooltip;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Generic armour piece (brass, copper, zinc and slime sets). Replaces the per-set MCreator classes with their
 * Helmet/Chestplate/Leggings/Boots inner classes.
 */
public class CSAArmorItem extends ArmorItem {
	private final boolean piglinNeutral;
	@Nullable
	private final String descKey;
	@Nullable
	private final BiConsumer<Level, Player> wornTick;

	public CSAArmorItem(Supplier<Holder<ArmorMaterial>> material, ArmorItem.Type type, int durabilityMultiplier, boolean piglinNeutral, @Nullable String descKey,
			@Nullable BiConsumer<Level, Player> wornTick) {
		super(material.get(), type, new Item.Properties().durability(type.getDurability(durabilityMultiplier)));
		this.piglinNeutral = piglinNeutral;
		this.descKey = descKey;
		this.wornTick = wornTick;
	}

	public CSAArmorItem(Supplier<Holder<ArmorMaterial>> material, ArmorItem.Type type, int durabilityMultiplier, boolean piglinNeutral) {
		this(material, type, durabilityMultiplier, piglinNeutral, null, null);
	}

	@Override
	public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
		return piglinNeutral;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean selected) {
		super.inventoryTick(stack, level, entity, slotId, selected);
		if (wornTick != null && entity instanceof Player player && player.getItemBySlot(getEquipmentSlot()) == stack)
			wornTick.accept(level, player);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, context, list, flag);
		if (descKey != null)
			ShiftTooltip.append(list, null, descKey, null);
	}
}
