package net.furtitwo.createstuffadditions.logic.gadget;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Flame projectiles fired by the Flamethrower: {@link #FLAME} (normal) and {@link #SOUL} (Hellfire). Both smelt the
 * dropped items they fly through and set fire; the soul flame burns longer and hits harder.
 */
public enum FlameProjectile {
	FLAME(ParticleTypes.FLAME, ParticleTypes.SMALL_FLAME, 1, 20, 4, true),
	SOUL(ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.SMOKE, 10, 40, 8, false);

	private static final TagKey<Block> SOUL_FIRE_BASE = BlockTags.create(ResourceLocation.parse("minecraft:soul_fire_base_blocks"));

	private final SimpleParticleType coreParticle;
	private final SimpleParticleType trailParticle;
	private final double burnMinSeconds;
	private final double burnMaxSeconds;
	private final float damage;
	private final boolean discardOnHit;

	FlameProjectile(SimpleParticleType coreParticle, SimpleParticleType trailParticle, double burnMinSeconds, double burnMaxSeconds, float damage, boolean discardOnHit) {
		this.coreParticle = coreParticle;
		this.trailParticle = trailParticle;
		this.burnMinSeconds = burnMinSeconds;
		this.burnMaxSeconds = burnMaxSeconds;
		this.damage = damage;
		this.discardOnHit = discardOnHit;
	}

	/** Every tick in flight (both sides): particles, and smelting of the dropped items within 1 block, unless in water. */
	public void onTick(Level level, Entity projectile) {
		if (level.getFluidState(BlockPos.containing(projectile.getX(), projectile.getY(), projectile.getZ())).createLegacyBlock().getBlock() == Blocks.WATER)
			return;
		if (level instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(coreParticle, projectile.getX(), projectile.getY(), projectile.getZ(), 2, 0.1, 0.1, 0.1, 0);
			serverLevel.sendParticles(trailParticle, projectile.getX(), projectile.getY(), projectile.getZ(), 5, 0.2, 0.2, 0.2, 0);
		}
		Vec3 center = projectile.position();
		List<Entity> nearby = level.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(1), e -> true).stream().sorted(Comparator.comparingDouble(e -> e.distanceToSqr(center))).toList();
		for (Entity entity : nearby) {
			if (entity instanceof ItemEntity item)
				smelt(level, item);
		}
	}

	private static void smelt(Level level, ItemEntity item) {
		ItemStack input = item.getItem();
		Optional<RecipeHolder<SmeltingRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input), level);
		if (recipe.isEmpty())
			return;
		ItemStack result = recipe.get().value().getResultItem(level.registryAccess()).copy();
		result.setCount(input.getCount());
		if (level instanceof ServerLevel serverLevel) {
			ItemEntity smelted = new ItemEntity(serverLevel, item.getX(), item.getY(), item.getZ(), result);
			smelted.setPickUpDelay(10);
			serverLevel.addFreshEntity(smelted);
			serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, item.getX(), item.getY(), item.getZ(), 5, 0.2, 0.2, 0.2, 0);
		}
		GadgetSounds.play(level, item.getX(), item.getY(), item.getZ(), SoundEvents.GENERIC_EXTINGUISH_FIRE, 0.2f, 3);
		if (!level.isClientSide())
			item.discard();
	}

	/** Hit on an entity: sets it on fire and deals lava damage (the normal flame is consumed). */
	public void onHitEntity(Level level, Entity target, Entity projectile) {
		if (discardOnHit && !projectile.level().isClientSide())
			projectile.discard();
		target.igniteForSeconds((int) Mth.nextDouble(RandomSource.create(), burnMinSeconds, burnMaxSeconds));
		target.hurt(level.damageSources().lava(), damage);
	}

	/** Hit on a block: lights a fire above it. */
	public void onHitBlock(Level level, BlockPos pos) {
		BlockPos above = pos.above();
		if (this == FLAME) {
			// Normal flames only ignite netherrack, regardless of mob griefing.
			if (level.isEmptyBlock(above) && level.getBlockState(pos).getBlock() == Blocks.NETHERRACK)
				level.setBlock(above, Blocks.FIRE.defaultBlockState(), 3);
		} else if (level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && level.isEmptyBlock(above) && !level.isClientSide()) {
			level.setBlock(above, (level.getBlockState(pos).is(SOUL_FIRE_BASE) ? Blocks.SOUL_FIRE : Blocks.FIRE).defaultBlockState(), 3);
		}
	}
}
