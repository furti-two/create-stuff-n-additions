package net.furtitwo.createstuffadditions.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import net.furtitwo.createstuffadditions.CreateSaMod;
import net.furtitwo.createstuffadditions.client.equipment.CreateSAPartialModels;

/** Client-only entry point: runs before the first resource reload, like Create's {@code CreateClient}. */
@Mod(value = CreateSaMod.MODID, dist = Dist.CLIENT)
public class CreateSaClient {
	public CreateSaClient(IEventBus modEventBus) {
		CreateSAPartialModels.init();
	}
}
