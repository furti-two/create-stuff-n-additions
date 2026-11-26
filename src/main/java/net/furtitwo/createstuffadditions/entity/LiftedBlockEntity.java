package net.furtitwo.createstuffadditions.entity;

import net.furtitwo.createstuffadditions.compat.sable.SubLevels;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.common.NeoForgeMod;

import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

import java.util.Optional;

public class LiftedBlockEntity extends Mob {
	public static final EntityDataAccessor<BlockPos> SPAWN_POS = SynchedEntityData.defineId(LiftedBlockEntity.class, EntityDataSerializers.BLOCK_POS);
	public static final EntityDataAccessor<Integer> GROUND_TIME = SynchedEntityData.defineId(LiftedBlockEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Boolean> GRABBED = SynchedEntityData.defineId(LiftedBlockEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Optional<BlockState>> DATA_BLOCK = SynchedEntityData.defineId(LiftedBlockEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE);
	public static final EntityDataAccessor<CompoundTag> DATA_BLOCK_ENTITY = SynchedEntityData.defineId(LiftedBlockEntity.class, EntityDataSerializers.COMPOUND_TAG);

	public LiftedBlockEntity(EntityType<LiftedBlockEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setPersistenceRequired();
		this.setPathfindingMalus(PathType.WATER, 0);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(SPAWN_POS, BlockPos.ZERO);
		builder.define(GROUND_TIME, 0);
		builder.define(GRABBED, false);
		builder.define(DATA_BLOCK, Optional.empty());
		builder.define(DATA_BLOCK_ENTITY, new CompoundTag());
	}

	public BlockPos getStartPos() {
		return this.entityData.get(SPAWN_POS);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.FELL_OUT_OF_WORLD) || source.is(DamageTypes.GENERIC_KILL)) {
			return super.hurt(source, amount);
		}
		return false;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected void doPush(Entity entity) {
	}

	@Override
	protected void pushEntities() {
	}

	@Override
	public boolean fireImmune() {
		return true;
	}

	@Override
	public boolean canDrownInFluidType(net.neoforged.neoforge.fluids.FluidType type) {
		return false;
	}

	@Override
	public boolean canCollideWith(Entity entity) {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public boolean checkSpawnObstruction(LevelReader world) {
		return world.isUnobstructed(this);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		BlockPos pos = this.getStartPos();
		compound.putInt("SpawnX", pos.getX());
		compound.putInt("SpawnY", pos.getY());
		compound.putInt("SpawnZ", pos.getZ());
		compound.putInt("GroundTime", this.entityData.get(GROUND_TIME));
		compound.putBoolean("Grabbed", this.entityData.get(GRABBED));
		compound.put("StoredBlock", NbtUtils.writeBlockState(this.entityData.get(DATA_BLOCK).orElse(Blocks.AIR.defaultBlockState())));
		compound.put("StoredBlockEntity", this.entityData.get(DATA_BLOCK_ENTITY));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		BlockPos pos = new BlockPos(compound.getInt("SpawnX"), compound.getInt("SpawnY"), compound.getInt("SpawnZ"));
		this.entityData.set(SPAWN_POS, pos);
		if (compound.contains("GroundTime")) {
			this.entityData.set(GROUND_TIME, compound.getInt("GroundTime"));
		}
		if (compound.contains("Grabbed")) {
			this.entityData.set(GRABBED, compound.getBoolean("Grabbed"));
		}
		if (compound.contains("StoredBlock")) {
			this.entityData.set(DATA_BLOCK, Optional.of(NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), compound.getCompound("StoredBlock"))));
		}
		if (compound.contains("StoredBlockEntity")) {
			this.entityData.set(DATA_BLOCK_ENTITY, compound.getCompound("StoredBlockEntity"));
		}
	}

	@Override
	public void baseTick() {
		super.baseTick();
		if (this.onGround() && !this.entityData.get(GRABBED)) {
			this.entityData.set(GROUND_TIME, this.entityData.get(GROUND_TIME) + 1);
			if (this.entityData.get(GROUND_TIME) > 10 && !this.level().isClientSide()) {
				// Landed on a Sable structure: place the block into it (its plot), otherwise in the world
				BlockPos pos = SubLevels.feetBlockPos(this);
				if (this.getY() >= this.level().getMinBuildHeight() && FallingBlock.isFree(this.level().getBlockState(pos))) {
					BlockState state = this.entityData.get(DATA_BLOCK).orElse(Blocks.AIR.defaultBlockState());
					this.level().setBlock(pos, state, 3);
					CompoundTag tag = this.entityData.get(DATA_BLOCK_ENTITY);
					if (!tag.isEmpty()) {
						BlockEntity be = this.level().getBlockEntity(pos);
						if (be != null) {
							be.loadWithComponents(tag, this.level().registryAccess());
						}
					}
				} else {
					Block.dropResources(this.entityData.get(DATA_BLOCK).orElse(Blocks.AIR.defaultBlockState()), this.level(), this.blockPosition(), null);
				}
				this.discard();
			}
		} else {
			this.entityData.set(GROUND_TIME, 0);
		}
	}

	public void setStoredBlock(BlockPos pos) {
		BlockState state = level().getBlockState(pos);
		this.entityData.set(DATA_BLOCK, Optional.of(state));
		if (state.hasBlockEntity()) {
			BlockEntity be = level().getBlockEntity(pos);
			if (be != null) {
				CompoundTag tag = be.saveWithFullMetadata(level().registryAccess());
				this.entityData.set(DATA_BLOCK_ENTITY, tag);
			}
		} else {
			this.entityData.set(DATA_BLOCK_ENTITY, new CompoundTag());
		}
		level().removeBlock(pos, false);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData) {
		SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData);
		this.setYRot(0.0F);
		this.setXRot(0.0F);
		this.setYHeadRot(0.0F);
		this.setYBodyRot(0.0F);
		this.entityData.set(SPAWN_POS, this.blockPosition());
		return data;
	}

	public static void init(RegisterSpawnPlacementsEvent event) {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Monster.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0);
		builder = builder.add(Attributes.MAX_HEALTH, 10);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 0);
		builder = builder.add(Attributes.FOLLOW_RANGE, 0);
		builder = builder.add(Attributes.STEP_HEIGHT, 0.6);
		builder = builder.add(NeoForgeMod.SWIM_SPEED, 0);
		return builder;
	}
}
