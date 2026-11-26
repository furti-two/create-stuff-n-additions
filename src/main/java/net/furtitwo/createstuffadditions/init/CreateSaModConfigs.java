package net.furtitwo.createstuffadditions.init;

import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.ModList;
import net.neoforged.bus.api.SubscribeEvent;

import net.furtitwo.createstuffadditions.configuration.CreateSaConfigConfiguration;
import net.furtitwo.createstuffadditions.CreateSaMod;

@EventBusSubscriber(modid = CreateSaMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CreateSaModConfigs {
	@SubscribeEvent
	public static void register(FMLConstructModEvent event) {
		event.enqueueWork(() -> {
			ModList.get().getModContainerById("create_sa").get().registerConfig(ModConfig.Type.COMMON, CreateSaConfigConfiguration.SPEC, "create-stuff-additions.toml");
		});
	}
}
