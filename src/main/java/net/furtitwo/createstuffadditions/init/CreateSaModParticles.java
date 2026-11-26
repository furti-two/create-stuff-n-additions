
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.furtitwo.createstuffadditions.init;

import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.furtitwo.createstuffadditions.client.particle.WaterCloudParticle;
import net.furtitwo.createstuffadditions.client.particle.SteamCloudParticle;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CreateSaModParticles {
	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(CreateSaModParticleTypes.WATER_CLOUD.get(), WaterCloudParticle::provider);
		event.registerSpriteSet(CreateSaModParticleTypes.STEAM_CLOUD.get(), SteamCloudParticle::provider);
	}
}
