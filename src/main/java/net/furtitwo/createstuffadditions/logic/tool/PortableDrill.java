package net.furtitwo.createstuffadditions.logic.tool;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

import net.furtitwo.createstuffadditions.item.CustomFluidHandlerItemStack;
import net.furtitwo.createstuffadditions.network.CreateSaModVariables;

import java.util.List;

/**
 * Portable drill: burns 10 mB of lava and water per block, Chunk Eater vein mining, Digging 3x3 mining, and mining
 * fatigue while a tank is empty. Tank 0 holds the fuel, tank 1 the water.
 */
public final class PortableDrill {
	private static final ResourceKey<Enchantment> CHUNK_EATER = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("create_sa:chunk_eater"));
	private static final ResourceKey<Enchantment> DIGGING = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("create_sa:digging"));
	private static final int FLUID_PER_BLOCK = 10;

	/** Digging: blocks around the mined one, by axis of the face the player looks at. */
	private static final List<Cut> CUTS_Y = List.of(Cut.of(1, 0, 0), Cut.of(-1, 0, 0), Cut.of(1, 0, 1), Cut.of(1, 0, -1), Cut.of(-1, 0, -1), Cut.of(-1, 0, 1), Cut.of(0, 0, 1), Cut.of(0, 0, -1));
	private static final List<Cut> CUTS_Z = List.of(Cut.of(1, 0, 0), Cut.of(-1, 0, 0), Cut.of(1, 1, 0), Cut.of(1, -1, 0), Cut.of(-1, -1, 0), Cut.of(-1, 1, 0), Cut.of(0, 1, 0), Cut.of(0, -1, 0));
	// Two entries of the X axis check the hardness of a different block than the one they test and break (kept as is).
	private static final List<Cut> CUTS_X = List.of(Cut.of(0, 0, 1), Cut.of(0, 0, -1), new Cut(new BlockPos(0, 1, 1), new BlockPos(0, 0, 1), new BlockPos(0, 1, 1)), Cut.of(0, -1, 1), Cut.of(0, -1, -1), Cut.of(0, 1, -1),
			new Cut(new BlockPos(0, 1, 0), new BlockPos(0, 1, 1), new BlockPos(0, 1, 0)), Cut.of(0, -1, 0));

	private PortableDrill() {
	}

	/** Called from {@code Item.mineBlock}; {@code minedState} is the state of the block that was broken. */
	public static void onBlockBroken(LevelAccessor world, BlockPos pos, BlockState minedState, Entity entity, ItemStack drill) {
		IFluidHandlerItem tanks = drill.getCapability(Capabilities.FluidHandler.ITEM);
		if (tanks.getFluidInTank(0).getAmount() <= 0 || tanks.getFluidInTank(1).getAmount() <= 0)
			return;
		consumeFluids(drill, FLUID_PER_BLOCK);

		if (enchantLevel(world, drill, CHUNK_EATER) != 0 && CreateSaModVariables.MapVariables.get(world).enableChunkEaterEnchant && !entity.isShiftKeyDown() && isBreakable(world, pos)) {
			double maxBlocks = 2 + Math.round(Math.min(enchantLevel(world, drill, CHUNK_EATER), 10) * 1.5);
			double mined = mineVein(world, maxBlocks, pos, minedState);
			consumeFluids(drill, (int) (FLUID_PER_BLOCK * mined));
		}

		if (enchantLevel(world, drill, DIGGING) != 0 && CreateSaModVariables.MapVariables.get(world).enableDiggingEnchant && !entity.isShiftKeyDown() && world.getBlockState(pos).is(BlockTags.MINEABLE_WITH_PICKAXE)) {
			Vec3 eye = entity.getEyePosition(1f);
			Direction face = entity.level().clip(new ClipContext(eye, eye.add(entity.getViewVector(1f).scale(5)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getDirection();
			List<Cut> cuts = switch (face.getAxis()) {
				case Y -> CUTS_Y;
				case Z -> CUTS_Z;
				case X -> CUTS_X;
			};
			for (Cut cut : cuts)
				dig(world, pos, cut, drill);
		}
	}

	/** Called every tick while the drill is the selected hotbar item: mining fatigue III while a tank is empty. */
	public static void onSelectedTick(Entity entity, ItemStack drill) {
		IFluidHandlerItem tanks = drill.getCapability(Capabilities.FluidHandler.ITEM);
		if (tanks.getFluidInTank(0).getAmount() <= 0 || tanks.getFluidInTank(1).getAmount() <= 0) {
			if (entity instanceof LivingEntity living && !living.level().isClientSide())
				living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 2, 2, false, false));
		}
	}

	private static void dig(LevelAccessor world, BlockPos center, Cut cut, ItemStack drill) {
		if (!world.getBlockState(center.offset(cut.tagged)).is(BlockTags.MINEABLE_WITH_PICKAXE) || !isBreakable(world, center.offset(cut.hardness)))
			return;
		BlockPos target = center.offset(cut.target);
		// Drops spawn at the mined block, not at the broken neighbour.
		Block.dropResources(world.getBlockState(target), world, center, null);
		world.destroyBlock(target, false);
		consumeFluids(drill, FLUID_PER_BLOCK);
	}

	/**
	 * Breaks {@code pos} (dropping its loot) and recursively every adjacent block of the same type as {@code vein}. Each
	 * neighbour branch gets the same remaining budget, so the total can exceed {@code maxBlocks}.
	 *
	 * @return the number of neighbours broken (the starting block is not counted)
	 */
	private static double mineVein(LevelAccessor world, double maxBlocks, BlockPos pos, BlockState vein) {
		if (maxBlocks <= 0 || !isBreakable(world, pos))
			return 0;
		BlockState state = world.getBlockState(pos);
		if (world instanceof ServerLevel server)
			Block.dropResources(state, server, pos, null);
		world.destroyBlock(pos, false);
		double remaining = maxBlocks - 1;
		if (remaining <= 0)
			return 0;
		double total = 0;
		for (BlockPos next : List.of(pos.east(), pos.west(), pos.south(), pos.north(), pos.above(), pos.below())) {
			if (world.getBlockState(next).getBlock() == vein.getBlock())
				total += mineVein(world, remaining, next, vein) + 1;
		}
		return total;
	}

	/** Not unbreakable (bedrock...) and softer than obsidian. */
	private static boolean isBreakable(LevelAccessor world, BlockPos pos) {
		float hardness = world.getBlockState(pos).getDestroySpeed(world, pos);
		return hardness >= 0 && hardness < 50;
	}

	private static void consumeFluids(ItemStack drill, int amount) {
		CustomFluidHandlerItemStack.drainTank(drill, new FluidStack(Fluids.WATER, amount), false);
		CustomFluidHandlerItemStack.drainTank(drill, new FluidStack(Fluids.LAVA, amount), false);
	}

	private static int enchantLevel(LevelAccessor world, ItemStack stack, ResourceKey<Enchantment> key) {
		return stack.getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key));
	}

	/** One block of the Digging pattern: offsets of the block whose tag is tested, whose hardness is tested, and that is broken. */
	private record Cut(BlockPos tagged, BlockPos hardness, BlockPos target) {
		static Cut of(int x, int y, int z) {
			BlockPos offset = new BlockPos(x, y, z);
			return new Cut(offset, offset, offset);
		}
	}
}
