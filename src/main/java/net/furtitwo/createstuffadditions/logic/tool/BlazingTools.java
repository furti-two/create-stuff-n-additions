package net.furtitwo.createstuffadditions.logic.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/** Blazing tools: auto-smelt the drops of the blocks they are made for, set hit targets on fire, self-repair in the Nether. */
public final class BlazingTools {
	private BlazingTools() {
	}

	/**
	 * Block hook of the blazing pickaxe/axe/shovel. When the block at {@code pos} is in {@code mineable} (and, if
	 * {@code requireCorrectTool}, the tool can harvest it) and its first drop can be smelted, every drop is replaced by its
	 * smelting result and the block is removed with flame particles. Then the tool repairs itself in the Nether.
	 */
	public static void onBlockBroken(ItemStack tool, Level level, BlockPos pos, Entity miner, TagKey<Block> mineable, boolean requireCorrectTool) {
		BlockState state = level.getBlockState(pos);
		if (state.is(mineable) && (!requireCorrectTool || tool.isCorrectToolForDrops(state)) && level instanceof ServerLevel server) {
			if (!smelt(server, Block.getDrops(state, server, pos, null, null, tool).get(0)).is(Items.AIR)) {
				// The drops are rolled a second time here, as the original implementation did.
				List<ItemStack> drops = Block.getDrops(state, server, pos, null, null, tool);
				for (ItemStack drop : drops) {
					ItemEntity item = new ItemEntity(server, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, smelt(server, drop));
					item.setPickUpDelay(10);
					server.addFreshEntity(item);
				}
				server.sendParticles(ParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.25, 0.25, 0.25, 0);
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
			}
		}
		repairInNether(miner, tool);
	}

	/** Hit hook shared by every blazing tool: sets the target on fire for 15 s, then the Nether repair. */
	public static void onHit(ItemStack tool, Entity target) {
		target.igniteForSeconds(15);
		repairInNether(target, tool);
	}

	/** Repairs 1 durability point when {@code entity} is in the Nether. */
	public static void repairInNether(Entity entity, ItemStack tool) {
		if (entity.level().dimension() == Level.NETHER)
			tool.setDamageValue(tool.getDamageValue() - 1);
	}

	private static ItemStack smelt(Level level, ItemStack input) {
		return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input), level).map(recipe -> recipe.value().getResultItem(level.registryAccess()).copy()).orElse(ItemStack.EMPTY);
	}
}
