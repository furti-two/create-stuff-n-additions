
package net.furtitwo.createstuffadditions.item;

import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.Entity;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.client.gui.screens.Screen;

import net.furtitwo.createstuffadditions.logic.tool.PortableDrill;
import net.furtitwo.createstuffadditions.util.TooltipLines;
import net.furtitwo.createstuffadditions.network.CreateSaModVariables;
import net.furtitwo.createstuffadditions.item.renderer.PortableDrillItemRenderer;
import net.furtitwo.createstuffadditions.init.CreateSaModItems;

import java.util.function.Consumer;
import java.util.List;

import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class PortableDrillItem extends TieredItem {
	private static final Tier TOOL_TIER = new Tier() {
		@Override
		public int getUses() {
			return 0;
		}

		@Override
		public float getSpeed() {
			return 20f;
		}

		@Override
		public float getAttackDamageBonus() {
			return 0;
		}

		@Override
		public TagKey<Block> getIncorrectBlocksForDrops() {
			return BlockTags.INCORRECT_FOR_IRON_TOOL;
		}

		@Override
		public int getEnchantmentValue() {
			return 0;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.of(new ItemStack(Blocks.ANDESITE), new ItemStack(Items.IRON_INGOT));
		}
	};


	/** Enchanting table value: without it the table offers nothing, so the item's own enchantment could only come from books. */
	@Override
	public int getEnchantmentValue() {
		return 10;
	}

	public PortableDrillItem() {
		super(TOOL_TIER, new Item.Properties().attributes(ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.8, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build()));
	}

	@SubscribeEvent
	public static void handleToolDamage(ModifyDefaultComponentsEvent event) {
		event.modify(CreateSaModItems.PORTABLE_DRILL.get(), builder -> builder.remove(DataComponents.MAX_DAMAGE));
	}

	@Override
	public boolean isCorrectToolForDrops(ItemStack itemstack, BlockState blockstate) {
		return !blockstate.is(BlockTags.NEEDS_DIAMOND_TOOL);
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ItemAbility toolAction) {
		return ItemAbilities.DEFAULT_AXE_ACTIONS.contains(toolAction) || ItemAbilities.DEFAULT_HOE_ACTIONS.contains(toolAction) || ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(toolAction) || ItemAbilities.DEFAULT_PICKAXE_ACTIONS.contains(toolAction)
				|| ItemAbilities.DEFAULT_SWORD_ACTIONS.contains(toolAction);
	}

	@Override
	public float getDestroySpeed(ItemStack itemstack, BlockState blockstate) {
		return 20f;
	}

	@Override
	public boolean mineBlock(ItemStack itemstack, Level world, BlockState blockstate, BlockPos pos, LivingEntity entity) {
		itemstack.hurtAndBreak(1, entity, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
		PortableDrill.onBlockBroken(world, pos, blockstate, entity, itemstack);
		return true;
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		itemstack.hurtAndBreak(2, entity, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
		return true;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return false;
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return 16777215;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		Level world = context.level();
		IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
		int fuel = 0;
		int water = 0;
		if (handler != null) {
			fuel = Math.round(handler.getFluidInTank(0).getAmount() * 0.1f);
			water = Math.round(handler.getFluidInTank(1).getAmount() * 0.1f);
		}
		int max = world != null ? (int) Math.round(CreateSaModVariables.MapVariables.get(world).gadgetCapacity) : 1600;
		if (Screen.hasShiftDown()) {
			list.add(Component.translatable("item_desc.holdshift"));
			list.add(Component.literal(Component.translatable("item_desc.fuel").getString() + " \u00A78" + fuel + "/" + max));
			list.add(Component.literal(Component.translatable("item_desc.water").getString() + " \u00A78" + water + "/" + max));
			list.add(Component.literal(" "));
			list.addAll(TooltipLines.wrap(Component.translatable("item_desc.portable_drill.desc").getString(), 0));
		} else {
			list.add(Component.translatable("item_desc.holdshift"));
			list.add(Component.literal(Component.translatable("item_desc.fuel").getString() + " \u00A78" + fuel + "/" + max));
			list.add(Component.literal(Component.translatable("item_desc.water").getString() + " \u00A78" + water + "/" + max));
		}
		super.appendHoverText(stack, context, list, flag);
	}

	@Override
	public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(SimpleCustomRenderer.create(this, new PortableDrillItemRenderer()));
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected)
			PortableDrill.onSelectedTick(entity, itemstack);
	}
}
