
package net.furtitwo.createstuffadditions.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.furtitwo.createstuffadditions.entity.FanModuleEntity;
import net.furtitwo.createstuffadditions.client.model.Modelfan_module;

public class FanModuleRenderer extends MobRenderer<FanModuleEntity, Modelfan_module<FanModuleEntity>> {
	public FanModuleRenderer(EntityRendererProvider.Context context) {
		super(context, new Modelfan_module<FanModuleEntity>(context.bakeLayer(Modelfan_module.LAYER_LOCATION)), 0.1f);
	}

	@Override
	public ResourceLocation getTextureLocation(FanModuleEntity entity) {
		if (entity.getPersistentData().getBoolean("tagActive")) {
			return ResourceLocation.parse("create_sa:textures/entities/fan_module_on.png");
		}
		return ResourceLocation.parse("create_sa:textures/entities/fan_module_off.png");
	}
}
