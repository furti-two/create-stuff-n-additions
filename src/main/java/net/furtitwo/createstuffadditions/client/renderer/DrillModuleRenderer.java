
package net.furtitwo.createstuffadditions.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.furtitwo.createstuffadditions.entity.DrillModuleEntity;
import net.furtitwo.createstuffadditions.client.model.Modeldrill_module;

public class DrillModuleRenderer extends MobRenderer<DrillModuleEntity, Modeldrill_module<DrillModuleEntity>> {
	public DrillModuleRenderer(EntityRendererProvider.Context context) {
		super(context, new Modeldrill_module<DrillModuleEntity>(context.bakeLayer(Modeldrill_module.LAYER_LOCATION)), 0f);
	}

	@Override
	public ResourceLocation getTextureLocation(DrillModuleEntity entity) {
		if (entity.getPersistentData().getBoolean("tagActive")) {
			return ResourceLocation.parse("create_sa:textures/entities/moving_drill_module.png");
		}
		return ResourceLocation.parse("create_sa:textures/entities/static_drill_module.png");
	}
}
