
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.furtitwo.createstuffadditions.init;

import net.furtitwo.createstuffadditions.client.model.*;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class CreateSaModModels {
	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(Modelnetherite_jetpack.LAYER_LOCATION, Modelnetherite_jetpack::createBodyLayer);
		event.registerLayerDefinition(Modelbrass_drone.LAYER_LOCATION, Modelbrass_drone::createBodyLayer);
		event.registerLayerDefinition(Modelfan_module.LAYER_LOCATION, Modelfan_module::createBodyLayer);
		event.registerLayerDefinition(Modelbrass_cube.LAYER_LOCATION, Modelbrass_cube::createBodyLayer);
		event.registerLayerDefinition(Modelandesite_exoskeleton.LAYER_LOCATION, Modelandesite_exoskeleton::createBodyLayer);
		event.registerLayerDefinition(Modelcopper_jetpack.LAYER_LOCATION, Modelcopper_jetpack::createBodyLayer);
		event.registerLayerDefinition(Modelbrass_emitter.LAYER_LOCATION, Modelbrass_emitter::createBodyLayer);
		event.registerLayerDefinition(Modelbrass_jetpack.LAYER_LOCATION, Modelbrass_jetpack::createBodyLayer);
		event.registerLayerDefinition(Modelnetherite_exoskeleton.LAYER_LOCATION, Modelnetherite_exoskeleton::createBodyLayer);
		event.registerLayerDefinition(Modelbrass_exoskeleton.LAYER_LOCATION, Modelbrass_exoskeleton::createBodyLayer);
		event.registerLayerDefinition(Modeldrill_module.LAYER_LOCATION, Modeldrill_module::createBodyLayer);
		event.registerLayerDefinition(Modelvault_module.LAYER_LOCATION, Modelvault_module::createBodyLayer);
		event.registerLayerDefinition(Modelbrass_rover.LAYER_LOCATION, Modelbrass_rover::createBodyLayer);
		event.registerLayerDefinition(Modelmagnet_module.LAYER_LOCATION, Modelmagnet_module::createBodyLayer);
		event.registerLayerDefinition(Modelandesite_jetpack.LAYER_LOCATION, Modelandesite_jetpack::createBodyLayer);
		event.registerLayerDefinition(Modelcopper_exoskeleton.LAYER_LOCATION, Modelcopper_exoskeleton::createBodyLayer);
	}
}
