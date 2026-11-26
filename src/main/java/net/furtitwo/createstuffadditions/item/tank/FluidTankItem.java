package net.furtitwo.createstuffadditions.item.tank;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import net.furtitwo.createstuffadditions.fuel.FuelStorage;
import net.furtitwo.createstuffadditions.fuel.FuelType;
import net.furtitwo.createstuffadditions.item.ShiftTooltip;
import net.furtitwo.createstuffadditions.network.CreateSaModVariables.MapVariables;
import net.furtitwo.createstuffadditions.logic.tank.TankFilling;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.ToDoubleFunction;

/**
 * Filling (water) and fueling (lava) tanks in three sizes (before there were six classes)
 * Storage is the fluid capability registered in {@code ModCapabilities}; this class only shows it; the transfers live in {@link TankFilling}.
 */
public class FluidTankItem extends Item {
	public enum Size {
		SMALL(v -> v.smallTankCapacity, 800), MEDIUM(v -> v.mediumTankCapacity, 1600), LARGE(v -> v.largeTankCapacity, 3200);

		/** Synced capacity, in display units (mB / 10). */
		final ToDoubleFunction<MapVariables> capacity;
		final int fallbackCapacity;

		Size(ToDoubleFunction<MapVariables> capacity, int fallbackCapacity) {
			this.capacity = capacity;
			this.fallbackCapacity = fallbackCapacity;
		}
	}

	private final FuelType.Resource resource;
	private final Size size;
	/** {@code filling_tank} or {@code fueling_tank}, root of the tooltip keys. */
	private final String descRoot;

	public FluidTankItem(FuelType.Resource resource, Size size) {
		super(new Item.Properties().stacksTo(1));
		this.resource = resource;
		this.size = size;
		this.descRoot = resource == FuelType.Resource.WATER ? "filling_tank" : "fueling_tank";
	}

	private int displayCapacity(@Nullable LevelAccessor level) {
		return level == null ? size.fallbackCapacity : (int) Math.round(size.capacity.applyAsDouble(MapVariables.get(level)));
	}

	private static int stored(ItemStack stack) {
		return Math.round(FuelStorage.amount(stack, 0) / (float) FuelStorage.MB_PER_UNIT);
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return true;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		var handler = FuelStorage.handler(stack);
		if (handler == null || handler.getTankCapacity(0) <= 0)
			return 0;
		return Math.min(13, (int) (13f * handler.getFluidInTank(0).getAmount() / handler.getTankCapacity(0)));
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return resource.barColor;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		int max = displayCapacity(context.level());
		ShiftTooltip.append(list, l -> l.add(Component.literal(Component.translatable(resource.tooltipKey).getString() + " §8" + stored(stack) + "/" + max)),
				"item_desc." + descRoot + ".desc", "item_desc." + descRoot + ".right_click", "item_desc.filling_tank.desc2");
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
		TankFilling.onTankUse(level, player, result.getObject(), resource);
		return result;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean selected) {
		super.inventoryTick(stack, level, entity, slotId, selected);
		TankFilling.onTankTick(level, entity, stack, resource);
	}
}
