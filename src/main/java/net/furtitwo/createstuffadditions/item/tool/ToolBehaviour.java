package net.furtitwo.createstuffadditions.item.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.furtitwo.createstuffadditions.item.ShiftTooltip;
import net.furtitwo.createstuffadditions.logic.tool.BlazingTools;
import net.furtitwo.createstuffadditions.logic.tool.ExperienceTools;

import java.util.List;

/** Extra behaviour plugged into the generic tool classes (mining/hitting hooks, tooltip, glint). */
public interface ToolBehaviour {
	ToolBehaviour NONE = new ToolBehaviour() {
	};

	default void onMineBlock(ItemStack stack, Level level, BlockPos pos, LivingEntity miner) {
	}

	default void onHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
	}

	/** Called client side only. */
	default void appendTooltip(ItemStack stack, List<Component> list) {
	}

	default boolean isFoil(ItemStack stack) {
		return false;
	}

	// ---- Blazing tools (behaviour lives in logic.tool.BlazingTools) ----

	/** Shared hit + tooltip part of the blazing tools; {@code mine} is the tool-specific block hook. */
	static ToolBehaviour blazing(String usedKey, MineHook mine) {
		return new ToolBehaviour() {
			@Override
			public void onMineBlock(ItemStack stack, Level level, BlockPos pos, LivingEntity miner) {
				mine.onMine(stack, level, pos, miner);
			}

			@Override
			public void onHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
				BlazingTools.onHit(stack, target);
			}

			@Override
			public void appendTooltip(ItemStack stack, List<Component> list) {
				ShiftTooltip.append(list, null, "item_desc.blazing_tools.desc", usedKey);
			}
		};
	}

	ToolBehaviour BLAZING_PICKAXE = blazing("item_desc.blazing_tools.desc2", (stack, level, pos, miner) -> BlazingTools.onBlockBroken(stack, level, pos, miner, BlockTags.MINEABLE_WITH_PICKAXE, true));
	ToolBehaviour BLAZING_AXE = blazing("item_desc.blazing_tools.desc2", (stack, level, pos, miner) -> BlazingTools.onBlockBroken(stack, level, pos, miner, BlockTags.MINEABLE_WITH_AXE, false));
	ToolBehaviour BLAZING_SHOVEL = blazing("item_desc.blazing_tools.desc2", (stack, level, pos, miner) -> BlazingTools.onBlockBroken(stack, level, pos, miner, BlockTags.MINEABLE_WITH_SHOVEL, false));
	ToolBehaviour BLAZING_CLEAVER = blazing("item_desc.blazing_cleaver.desc2", (stack, level, pos, miner) -> BlazingTools.repairInNether(miner, stack));

	// ---- Experience tools (behaviour lives in logic.tool.ExperienceTools), always glint ----

	ToolBehaviour EXPERIENCE = new ToolBehaviour() {
		@Override
		public void onMineBlock(ItemStack stack, Level level, BlockPos pos, LivingEntity miner) {
			ExperienceTools.onBlockBroken(level, pos);
		}

		@Override
		public void onHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
			ExperienceTools.onHit(target);
		}

		@Override
		public void appendTooltip(ItemStack stack, List<Component> list) {
			ShiftTooltip.append(list, null, "item_desc.xp_tools.desc", null);
		}

		@Override
		public boolean isFoil(ItemStack stack) {
			return true;
		}
	};

	@FunctionalInterface
	interface MineHook {
		void onMine(ItemStack stack, Level level, BlockPos pos, LivingEntity miner);
	}
}
