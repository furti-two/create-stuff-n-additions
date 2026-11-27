
package net.furtitwo.createstuffadditions.item;

import net.furtitwo.createstuffadditions.fuel.FuelType;
import net.furtitwo.createstuffadditions.fuel.FuelableItem;
import net.furtitwo.createstuffadditions.fuel.FuelStorage;

import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.Minecraft;

import net.furtitwo.createstuffadditions.util.TooltipLines;
import net.furtitwo.createstuffadditions.logic.gadget.BlockPicker;
import net.furtitwo.createstuffadditions.network.CreateSaModVariables;
import net.furtitwo.createstuffadditions.client.renderer.item.BlockPickerItemRenderer;

import javax.annotation.Nullable;

import java.util.function.Consumer;
import java.util.List;

import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.simibubi.create.foundation.item.CustomArmPoseItem;

public class BlockPickerItem extends Item implements CustomArmPoseItem, FuelableItem {
	@Override
	public FuelType getFuelType() {
		return FuelType.HYDRAULIC;
	}


	/** Enchanting table value: without it the table offers nothing, so the item's own enchantment could only come from books. */
	@Override
	public int getEnchantmentValue() {
		return 10;
	}

	public BlockPickerItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public int getBarWidth(ItemStack stack) {
		Entity entity = Minecraft.getInstance().player != null ? Minecraft.getInstance().player : null;
		IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
		if (handler == null)
			return 0;
		int amount = Math.round(handler.getFluidInTank(0).getAmount() * 0.1f);
		float capacity = 1600f;
		if (entity != null)
			capacity = (float) Math.round(CreateSaModVariables.MapVariables.get(entity.level()).gadgetCapacity);
		int width = (int) ((amount / capacity) * 13f);
		return Math.min(width, 13);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return 8361684;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		Level world = context.level();
		IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
		int water = 0;
		if (handler != null) {
			water = Math.round(handler.getFluidInTank(0).getAmount() * 0.1f);
		}
		int max = world != null ? (int) Math.round(CreateSaModVariables.MapVariables.get(world).gadgetCapacity) : 1600;
		if (Screen.hasShiftDown()) {
			list.add(Component.literal(Component.translatable("item_desc.holdshift").getString().replace("\u00A77", "\u00A7f")));
			list.add(Component.literal(Component.translatable("item_desc.water").getString() + " \u00A78" + water + "/" + max));
			list.add(Component.literal(" "));
			list.addAll(TooltipLines.wrap(Component.translatable("item_desc.block_picker.desc").getString(), 0));
			list.add(Component.literal(" "));
			list.add(Component.literal(Component.translatable("item_desc.used").getString()));
			list.addAll(TooltipLines.wrap(Component.translatable("item_desc.block_picker.desc2").getString(), 1));
		} else {
			list.add(Component.translatable("item_desc.holdshift"));
			list.add(Component.literal(Component.translatable("item_desc.water").getString() + " \u00A78" + water + "/" + max));
		}
		super.appendHoverText(stack, context, list, flag);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack localTool, ItemStack potentialFuel, Slot slot, ClickAction action, Player player, SlotAccess access) {
		return refuelFromCursor(localTool, potentialFuel, slot, action, player, access);
	}

	protected void playSound(Player player, SoundEvent soundEvent) {
		player.playSound(soundEvent, 0.8f, 0.8f + player.level().getRandom().nextFloat() * 0.1f);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		BlockPicker.onUse(world, entity.getX(), entity.getY(), entity.getZ(), ar.getObject());
		return ar;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
		BlockPicker.onUseOnBlock(context.getLevel(), context.getClickedPos(), context.getItemInHand());
		return InteractionResult.PASS;
	}

	@Override
	public boolean onEntitySwing(ItemStack itemstack, LivingEntity entity) {
		boolean retval = super.onEntitySwing(itemstack, entity);
		BlockPicker.onSwing(entity, itemstack);
		return retval;
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		if (!world.isClientSide())
			FuelStorage.migrateLegacy(itemstack, getFuelType());
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected)
			BlockPicker.onHeldTick(world, entity, itemstack);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	@Override
	@Nullable
	public ArmPose getArmPose(ItemStack stack, AbstractClientPlayer player, InteractionHand hand) {
		if (player.getUsedItemHand() == hand && stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("tagAction")) {
			return ArmPose.BOW_AND_ARROW;
		}
		return ArmPose.ITEM;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(SimpleCustomRenderer.create(this, new BlockPickerItemRenderer()));
	}
}
