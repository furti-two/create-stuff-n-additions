package net.furtitwo.createstuffadditions.equipment;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import net.furtitwo.createstuffadditions.fuel.FuelStorage;
import net.furtitwo.createstuffadditions.fuel.FuelType;
import net.furtitwo.createstuffadditions.fuel.FuelableItem;
import net.furtitwo.createstuffadditions.util.TooltipLines;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Base class of every fluid/fuel-powered chestplate (jetpacks, exoskeletons).
 * Armour material / bar/ tooltip / refuelling / update tick only while worn.
 * Per-tick doesn't affect ItemStack, so not resynced every tick.
 */
public abstract class FuelableChestplateItem extends ArmorItem implements FuelableItem {
	/** mB drained from every used tank each time the item works. */
	public static final int DRAIN_MB = 10;

	private final FuelType fuelType;
	private final String descKey;
	private final boolean piglinNeutral;

	protected FuelableChestplateItem(Holder<ArmorMaterial> material, Item.Properties properties, FuelType fuelType, String descKey, boolean piglinNeutral) {
		super(material, ArmorItem.Type.CHESTPLATE, properties.stacksTo(1));
		this.fuelType = fuelType;
		this.descKey = descKey;
		this.piglinNeutral = piglinNeutral;
	}

	@Override
	public FuelType getFuelType() {
		return fuelType;
	}

	/** Translation key root, e.g. {@code brass_jetpack} for {@code item_desc.brass_jetpack.desc}. */
	public String getDescKey() {
		return descKey;
	}

	public boolean hasFuel(ItemStack stack) {
		return FuelStorage.hasFuel(stack, fuelType);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean selected) {
		super.inventoryTick(stack, level, entity, slotId, selected);
		if (!level.isClientSide())
			FuelStorage.migrateLegacy(stack, fuelType);
		if (entity instanceof Player player && player.getItemBySlot(EquipmentSlot.CHEST) == stack)
			tickWorn(stack, level, player);
	}

	protected abstract void tickWorn(ItemStack stack, Level level, Player player);

	/** True on the ticks where the item should spend fuel, every {@code interval + 1} ticks same as the old nbt counter */
	protected static boolean isWorkTick(Player player, int interval) {
		return player.tickCount % (interval + 1) == 0;
	}

	/** Server side only: spends {@link #DRAIN_MB} of each tank and plays the work sound. */
	protected void work(ItemStack stack, Level level, Player player, ResourceLocation sound, float volume, float pitch) {
		if (level.isClientSide())
			return;
		SoundEvent event = BuiltInRegistries.SOUND_EVENT.get(sound);
		if (event != null)
			level.playSound(null, BlockPos.containing(player.getX(), player.getY(), player.getZ()), event, SoundSource.PLAYERS, volume, pitch);
		FuelStorage.consume(stack, fuelType, DRAIN_MB);
	}

	@Override
	public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
		return piglinNeutral;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return fuelBarVisible(stack);
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return fuelBarWidth(stack);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return fuelBarColor(stack);
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack self, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
		return refuelFromCursor(self, other, slot, action, player, access);
	}

	@Override
	public abstract ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel);

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		ChestplateTooltip.append(this, stack, context.level(), list);
		super.appendHoverText(stack, context, list, flag);
	}

	/** Has "hold CTRL" section ({@link #appendControlLines}). */
	protected boolean hasControlSection() {
		return false;
	}

	/** Description lines while SHIFT is held */
	protected void appendShiftLines(@Nullable Level level, List<Component> list) {}

	/** Description lines while CTRL is held */
	protected void appendControlLines(@Nullable Level level, List<Component> list) {}

	protected static List<Component> wrap(String translationKey, int indent) {
		return TooltipLines.wrap(Component.translatable(translationKey).getString(), indent);
	}
}
