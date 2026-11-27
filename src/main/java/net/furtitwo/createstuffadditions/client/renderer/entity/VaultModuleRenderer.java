
package net.furtitwo.createstuffadditions.client.renderer.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.furtitwo.createstuffadditions.entity.VaultModuleEntity;
import net.furtitwo.createstuffadditions.client.model.Modelvault_module;

public class VaultModuleRenderer extends MobRenderer<VaultModuleEntity, Modelvault_module<VaultModuleEntity>> {
	public VaultModuleRenderer(EntityRendererProvider.Context context) {
		super(context, new Modelvault_module<VaultModuleEntity>(context.bakeLayer(Modelvault_module.LAYER_LOCATION)), 0f);
	}

	@Override
	public ResourceLocation getTextureLocation(VaultModuleEntity entity) {
		return ResourceLocation.parse("create_sa:textures/entities/vault_module.png");
	}
}
