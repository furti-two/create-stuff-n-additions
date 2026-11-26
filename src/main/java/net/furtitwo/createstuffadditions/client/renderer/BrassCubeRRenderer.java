
package net.furtitwo.createstuffadditions.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.furtitwo.createstuffadditions.entity.BrassCubeREntity;
import net.furtitwo.createstuffadditions.client.model.Modelbrass_cube;

public class BrassCubeRRenderer extends MobRenderer<BrassCubeREntity, Modelbrass_cube<BrassCubeREntity>> {
	public BrassCubeRRenderer(EntityRendererProvider.Context context) {
		super(context, new Modelbrass_cube<BrassCubeREntity>(context.bakeLayer(Modelbrass_cube.LAYER_LOCATION)), 0f);
	}

	@Override
	public ResourceLocation getTextureLocation(BrassCubeREntity entity) {
		if (entity.getPersistentData().getBoolean("tagBall")) {
			return ResourceLocation.parse("create_sa:textures/entities/balloon.png");
		}
		return ResourceLocation.parse("create_sa:textures/entities/brass_cube.png");
	}
}
