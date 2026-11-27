
package net.furtitwo.createstuffadditions.client.renderer.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.furtitwo.createstuffadditions.entity.MagnetModuleEntity;
import net.furtitwo.createstuffadditions.client.model.Modelmagnet_module;

public class MagnetModuleRenderer extends MobRenderer<MagnetModuleEntity, Modelmagnet_module<MagnetModuleEntity>> {
	public MagnetModuleRenderer(EntityRendererProvider.Context context) {
		super(context, new Modelmagnet_module<MagnetModuleEntity>(context.bakeLayer(Modelmagnet_module.LAYER_LOCATION)), 0f);
	}

	@Override
	public ResourceLocation getTextureLocation(MagnetModuleEntity entity) {
		return ResourceLocation.parse("create_sa:textures/entities/magnet_module.png");
	}
}
