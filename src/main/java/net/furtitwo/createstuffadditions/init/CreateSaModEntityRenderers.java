
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.furtitwo.createstuffadditions.init;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;

import net.furtitwo.createstuffadditions.client.renderer.entity.VaultModuleRenderer;
import net.furtitwo.createstuffadditions.client.renderer.entity.MagnetModuleRenderer;
import net.furtitwo.createstuffadditions.client.renderer.entity.LiftedBlockRenderer;
import net.furtitwo.createstuffadditions.client.renderer.entity.FanModuleRenderer;
import net.furtitwo.createstuffadditions.client.renderer.entity.DrillModuleRenderer;
import net.furtitwo.createstuffadditions.client.renderer.entity.BrassDroneRenderer;
import net.furtitwo.createstuffadditions.client.renderer.entity.BrassCubeRRenderer;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CreateSaModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(CreateSaModEntities.BRASS_DRONE.get(), BrassDroneRenderer::new);
		event.registerEntityRenderer(CreateSaModEntities.DRILL_MODULE.get(), DrillModuleRenderer::new);
		event.registerEntityRenderer(CreateSaModEntities.MAGNET_MODULE.get(), MagnetModuleRenderer::new);
		event.registerEntityRenderer(CreateSaModEntities.FAN_MODULE.get(), FanModuleRenderer::new);
		event.registerEntityRenderer(CreateSaModEntities.VAULT_MODULE.get(), VaultModuleRenderer::new);
		event.registerEntityRenderer(CreateSaModEntities.BRASS_CUBE_R.get(), BrassCubeRRenderer::new);
		event.registerEntityRenderer(CreateSaModEntities.FLAMETHROWER_PR.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(CreateSaModEntities.FLAMETHROWER_PR_2.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(CreateSaModEntities.LIFTED_BLOCK.get(), LiftedBlockRenderer::new);
	}
}
