package net.furtitwo.createstuffadditions.equipment;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.furtitwo.createstuffadditions.fuel.FuelType;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Exoskeleton chestplate: while fuelled, keeps a set of effects active and spends fuel every {@code interval} ticks. */
public abstract class ExoskeletonItem extends FuelableChestplateItem {
	public record Effect(Holder<MobEffect> effect, int duration, int amplifier) {
	}

	private final List<Effect> effects;
	private final int interval;
	private final ResourceLocation sound;
	private final float soundVolume;
	private final float soundPitch;

	protected ExoskeletonItem(Holder<ArmorMaterial> material, Item.Properties properties, FuelType fuelType, String descKey, boolean piglinNeutral, List<Effect> effects, int interval,
			ResourceLocation sound, float soundVolume, float soundPitch) {
		super(material, properties, fuelType, descKey, piglinNeutral);
		this.effects = effects;
		this.interval = interval;
		this.sound = sound;
		this.soundVolume = soundVolume;
		this.soundPitch = soundPitch;
	}

	/** The fuelled exoskeleton worn by this entity, or empty. */
	public static ItemStack getPoweredWornBy(LivingEntity entity) {
		ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
		return chest.getItem() instanceof ExoskeletonItem exo && exo.hasFuel(chest) ? chest : ItemStack.EMPTY;
	}

	@Override
	protected void tickWorn(ItemStack stack, Level level, Player player) {
		if (!hasFuel(stack))
			return;
		if (!level.isClientSide())
			for (Effect e : effects)
				player.addEffect(new MobEffectInstance(e.effect(), e.duration(), e.amplifier(), false, false));
		if (isWorkTick(player, interval))
			work(stack, level, player, sound, soundVolume, soundPitch);
		onFueledTick(stack, level, player);
	}

	protected void onFueledTick(ItemStack stack, Level level, Player player) {
	}

	@Override
	protected void appendShiftLines(@Nullable Level level, List<Component> list) {
		list.add(Component.literal(" "));
		list.add(Component.translatable("item_desc.used"));
		list.addAll(wrap("item_desc." + getDescKey() + ".desc2", 1));
	}
}
