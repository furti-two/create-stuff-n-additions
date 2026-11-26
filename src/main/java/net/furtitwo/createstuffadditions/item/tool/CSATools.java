package net.furtitwo.createstuffadditions.item.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Generic tools: one class per vanilla tool type, configured by a {@link Tier}, attack stats and a {@link ToolBehaviour}.
 * Replaces the 27 MCreator tool classes that only differed by these values.
 */
public final class CSATools {
	/** Same ids as {@code Item.BASE_ATTACK_DAMAGE_ID / BASE_ATTACK_SPEED_ID} (protected there). */
	private static final ResourceLocation BASE_ATTACK_DAMAGE = ResourceLocation.withDefaultNamespace("base_attack_damage");
	private static final ResourceLocation BASE_ATTACK_SPEED = ResourceLocation.withDefaultNamespace("base_attack_speed");

	private CSATools() {
	}

	public enum Kind {
		PICKAXE, AXE, SHOVEL, HOE, SWORD
	}

	/** Creates a tool. {@code reachBonus} adds block interaction range (rose quartz tools). */
	public static Item create(Kind kind, Tier tier, float damage, float attackSpeed, double reachBonus, ToolBehaviour behaviour) {
		Item.Properties props = new Item.Properties().attributes(attributes(tier, damage, attackSpeed, reachBonus));
		return switch (kind) {
			case PICKAXE -> new Pickaxe(tier, props, behaviour);
			case AXE -> new Axe(tier, props, behaviour);
			case SHOVEL -> new Shovel(tier, props, behaviour);
			case HOE -> new Hoe(tier, props, behaviour);
			case SWORD -> new Sword(tier, props, behaviour);
		};
	}

	public static Item create(Kind kind, Tier tier, float damage, float attackSpeed) {
		return create(kind, tier, damage, attackSpeed, 0, ToolBehaviour.NONE);
	}

	/** Same modifiers as {@code DiggerItem/SwordItem.createAttributes}, plus the optional reach bonus first. */
	private static ItemAttributeModifiers attributes(Tier tier, float damage, float attackSpeed, double reachBonus) {
		ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
		if (reachBonus != 0)
			builder.add(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(ResourceLocation.withDefaultNamespace("base_block_interaction_range"), reachBonus, AttributeModifier.Operation.ADD_VALUE),
					EquipmentSlotGroup.MAINHAND);
		return builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE, damage + tier.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED, attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build();
	}

	// ---- one subclass per vanilla tool type, all delegating to the behaviour ----

	public static class Pickaxe extends PickaxeItem {
		private final ToolBehaviour behaviour;

		Pickaxe(Tier tier, Item.Properties props, ToolBehaviour behaviour) {
			super(tier, props);
			this.behaviour = behaviour;
		}

		@Override
		public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
			boolean result = super.mineBlock(stack, level, state, pos, miner);
			behaviour.onMineBlock(stack, level, pos, miner);
			return result;
		}

		@Override
		public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
			boolean result = super.hurtEnemy(stack, target, attacker);
			behaviour.onHurtEnemy(stack, target, attacker);
			return result;
		}

		@Override
		public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
			super.appendHoverText(stack, context, list, flag);
			behaviour.appendTooltip(stack, list);
		}

		@Override
		public boolean isFoil(ItemStack stack) {
			return behaviour.isFoil(stack) || super.isFoil(stack);
		}
	}

	public static class Axe extends AxeItem {
		private final ToolBehaviour behaviour;

		Axe(Tier tier, Item.Properties props, ToolBehaviour behaviour) {
			super(tier, props);
			this.behaviour = behaviour;
		}

		@Override
		public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
			boolean result = super.mineBlock(stack, level, state, pos, miner);
			behaviour.onMineBlock(stack, level, pos, miner);
			return result;
		}

		@Override
		public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
			boolean result = super.hurtEnemy(stack, target, attacker);
			behaviour.onHurtEnemy(stack, target, attacker);
			return result;
		}

		@Override
		public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
			super.appendHoverText(stack, context, list, flag);
			behaviour.appendTooltip(stack, list);
		}

		@Override
		public boolean isFoil(ItemStack stack) {
			return behaviour.isFoil(stack) || super.isFoil(stack);
		}
	}

	public static class Shovel extends ShovelItem {
		private final ToolBehaviour behaviour;

		Shovel(Tier tier, Item.Properties props, ToolBehaviour behaviour) {
			super(tier, props);
			this.behaviour = behaviour;
		}

		@Override
		public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
			boolean result = super.mineBlock(stack, level, state, pos, miner);
			behaviour.onMineBlock(stack, level, pos, miner);
			return result;
		}

		@Override
		public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
			boolean result = super.hurtEnemy(stack, target, attacker);
			behaviour.onHurtEnemy(stack, target, attacker);
			return result;
		}

		@Override
		public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
			super.appendHoverText(stack, context, list, flag);
			behaviour.appendTooltip(stack, list);
		}

		@Override
		public boolean isFoil(ItemStack stack) {
			return behaviour.isFoil(stack) || super.isFoil(stack);
		}
	}

	public static class Hoe extends HoeItem {
		private final ToolBehaviour behaviour;

		Hoe(Tier tier, Item.Properties props, ToolBehaviour behaviour) {
			super(tier, props);
			this.behaviour = behaviour;
		}

		@Override
		public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
			boolean result = super.mineBlock(stack, level, state, pos, miner);
			behaviour.onMineBlock(stack, level, pos, miner);
			return result;
		}

		@Override
		public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
			boolean result = super.hurtEnemy(stack, target, attacker);
			behaviour.onHurtEnemy(stack, target, attacker);
			return result;
		}

		@Override
		public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
			super.appendHoverText(stack, context, list, flag);
			behaviour.appendTooltip(stack, list);
		}

		@Override
		public boolean isFoil(ItemStack stack) {
			return behaviour.isFoil(stack) || super.isFoil(stack);
		}
	}

	public static class Sword extends SwordItem {
		private final ToolBehaviour behaviour;

		Sword(Tier tier, Item.Properties props, ToolBehaviour behaviour) {
			super(tier, props);
			this.behaviour = behaviour;
		}

		@Override
		public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
			boolean result = super.mineBlock(stack, level, state, pos, miner);
			behaviour.onMineBlock(stack, level, pos, miner);
			return result;
		}

		@Override
		public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
			boolean result = super.hurtEnemy(stack, target, attacker);
			behaviour.onHurtEnemy(stack, target, attacker);
			return result;
		}

		@Override
		public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
			super.appendHoverText(stack, context, list, flag);
			behaviour.appendTooltip(stack, list);
		}

		@Override
		public boolean isFoil(ItemStack stack) {
			return behaviour.isFoil(stack) || super.isFoil(stack);
		}
	}
}
