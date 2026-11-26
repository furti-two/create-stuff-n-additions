
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.furtitwo.createstuffadditions.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.registries.Registries;

import net.furtitwo.createstuffadditions.entity.VaultModuleEntity;
import net.furtitwo.createstuffadditions.entity.MagnetModuleEntity;
import net.furtitwo.createstuffadditions.entity.LiftedBlockEntity;
import net.furtitwo.createstuffadditions.entity.FlamethrowerPrEntity;
import net.furtitwo.createstuffadditions.entity.FlamethrowerPr2Entity;
import net.furtitwo.createstuffadditions.entity.FanModuleEntity;
import net.furtitwo.createstuffadditions.entity.DrillModuleEntity;
import net.furtitwo.createstuffadditions.entity.BrassDroneEntity;
import net.furtitwo.createstuffadditions.entity.BrassCubeREntity;
import net.furtitwo.createstuffadditions.CreateSaMod;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class CreateSaModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Registries.ENTITY_TYPE, CreateSaMod.MODID);
	public static final DeferredHolder<EntityType<?>, EntityType<BrassDroneEntity>> BRASS_DRONE = register("brass_drone",
			EntityType.Builder.<BrassDroneEntity>of(BrassDroneEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(0.8f, 0.5f));
	public static final DeferredHolder<EntityType<?>, EntityType<DrillModuleEntity>> DRILL_MODULE = register("drill_module",
			EntityType.Builder.<DrillModuleEntity>of(DrillModuleEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(0.9f, 0.4f));
	public static final DeferredHolder<EntityType<?>, EntityType<MagnetModuleEntity>> MAGNET_MODULE = register("magnet_module",
			EntityType.Builder.<MagnetModuleEntity>of(MagnetModuleEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(0.9f, 0.4f));
	public static final DeferredHolder<EntityType<?>, EntityType<FanModuleEntity>> FAN_MODULE = register("fan_module",
			EntityType.Builder.<FanModuleEntity>of(FanModuleEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(0.9f, 0.4f));
	public static final DeferredHolder<EntityType<?>, EntityType<VaultModuleEntity>> VAULT_MODULE = register("vault_module",
			EntityType.Builder.<VaultModuleEntity>of(VaultModuleEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(0.9f, 0.4f));
	public static final DeferredHolder<EntityType<?>, EntityType<BrassCubeREntity>> BRASS_CUBE_R = register("brass_cube_r",
			EntityType.Builder.<BrassCubeREntity>of(BrassCubeREntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).fireImmune().sized(1f, 1f));
	public static final DeferredHolder<EntityType<?>, EntityType<FlamethrowerPrEntity>> FLAMETHROWER_PR = register("flamethrower_pr",
			EntityType.Builder.<FlamethrowerPrEntity>of(FlamethrowerPrEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final DeferredHolder<EntityType<?>, EntityType<FlamethrowerPr2Entity>> FLAMETHROWER_PR_2 = register("flamethrower_pr_2",
			EntityType.Builder.<FlamethrowerPr2Entity>of(FlamethrowerPr2Entity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final DeferredHolder<EntityType<?>, EntityType<LiftedBlockEntity>> LIFTED_BLOCK = register("lifted_block",
			EntityType.Builder.<LiftedBlockEntity>of(LiftedBlockEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).fireImmune().sized(0.9f, 0.9f));

	// Start of user code block custom entities
	// End of user code block custom entities
	private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerEntity(Capabilities.ItemHandler.ENTITY, DRILL_MODULE.get(), (living, context) -> living.getCombinedInventory());
		event.registerEntity(Capabilities.ItemHandler.ENTITY, MAGNET_MODULE.get(), (living, context) -> living.getCombinedInventory());
		event.registerEntity(Capabilities.ItemHandler.ENTITY, FAN_MODULE.get(), (living, context) -> living.getCombinedInventory());
		event.registerEntity(Capabilities.ItemHandler.ENTITY, VAULT_MODULE.get(), (living, context) -> living.getCombinedInventory());
	}

	@SubscribeEvent
	public static void init(RegisterSpawnPlacementsEvent event) {
		BrassDroneEntity.init(event);
		DrillModuleEntity.init(event);
		MagnetModuleEntity.init(event);
		FanModuleEntity.init(event);
		VaultModuleEntity.init(event);
		BrassCubeREntity.init(event);
		LiftedBlockEntity.init(event);
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(BRASS_DRONE.get(), BrassDroneEntity.createAttributes().build());
		event.put(DRILL_MODULE.get(), DrillModuleEntity.createAttributes().build());
		event.put(MAGNET_MODULE.get(), MagnetModuleEntity.createAttributes().build());
		event.put(FAN_MODULE.get(), FanModuleEntity.createAttributes().build());
		event.put(VAULT_MODULE.get(), VaultModuleEntity.createAttributes().build());
		event.put(BRASS_CUBE_R.get(), BrassCubeREntity.createAttributes().build());
		event.put(LIFTED_BLOCK.get(), LiftedBlockEntity.createAttributes().build());
	}
}
