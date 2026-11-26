package net.furtitwo.createstuffadditions.logic.drone;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.furtitwo.createstuffadditions.init.CreateSaModItems;
import net.furtitwo.createstuffadditions.entity.BrassDroneEntity;

/** Vault module: a 9 slot storage that only picks up items, with no action of its own. */
public final class VaultModule extends DroneModule {
	public static final VaultModule INSTANCE = new VaultModule();

	private VaultModule() {
		super(() -> CreateSaModItems.VAULT_COMPONENT.get(), 9, false);
	}

	@Override
	protected void onMountedTick(LevelAccessor world, Entity module, BrassDroneEntity drone) {
	}
}
