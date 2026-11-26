
package net.furtitwo.createstuffadditions.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.furtitwo.createstuffadditions.entity.BrassDroneEntity;
import net.furtitwo.createstuffadditions.client.model.Modelbrass_drone;

public class BrassDroneRenderer extends MobRenderer<BrassDroneEntity, Modelbrass_drone<BrassDroneEntity>> {
	public BrassDroneRenderer(EntityRendererProvider.Context context) {
		super(context, new Modelbrass_drone<BrassDroneEntity>(context.bakeLayer(Modelbrass_drone.LAYER_LOCATION)), 0.4f);
	}

	@Override
	public ResourceLocation getTextureLocation(BrassDroneEntity entity) {
		if (!entity.getPersistentData().getBoolean("tagStatic")) {
            if (entity.getPersistentData().getDouble("tagRotate") == 1)
                return ResourceLocation.parse("create_sa:textures/entities/brass_drone/active_drone_2.png");
            return ResourceLocation.parse("create_sa:textures/entities/brass_drone/active_drone_1.png");
        }

		return ResourceLocation.parse("create_sa:textures/entities/brass_drone/static_drone.png");
	}
}
