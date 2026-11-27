package net.furtitwo.createstuffadditions.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.EntityModel;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.HumanoidArm;

// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports
public class Modelnetherite_exoskeleton<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("create_sa", "modelnetherite_exoskeleton"), "main");
	public final ModelPart body;
	public final ModelPart bone3;
	public final ModelPart leftArm;
	public final ModelPart rightArm;

	public Modelnetherite_exoskeleton(ModelPart root) {
		this.body = root.getChild("body");
		this.bone3 = this.body.getChild("bone3");
		this.leftArm = root.getChild("leftArm");
		this.rightArm = root.getChild("rightArm");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition body = partdefinition.addOrReplaceChild("body",
				CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)).texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.505F)),
				PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition cube_r6_r1 = body.addOrReplaceChild("cube_r6_r1", CubeListBuilder.create().texOffs(0, 42).addBox(-2.5F, -4.5F, -6.0F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.5F)),
				PartPose.offsetAndRotation(0.0F, 5.0F, 2.5F, 0.0F, 3.1416F, 0.0F));
		PartDefinition bone3 = body.addOrReplaceChild("bone3", CubeListBuilder.create(), PartPose.offset(0.0F, -0.1F, -0.95F));
		PartDefinition body_r1 = bone3.addOrReplaceChild("body_r1",
				CubeListBuilder.create().texOffs(0, 32).addBox(-5.4142F, 11.0F, -2.5858F, 8.0F, 2.0F, 8.0F, new CubeDeformation(1.0F)).texOffs(32, 17).addBox(-5.4142F, 10.25F, -2.5858F, 8.0F, 12.0F, 8.0F, new CubeDeformation(1.0F)),
				PartPose.offsetAndRotation(0.0F, -9.25F, -2.0F, 0.0F, 0.7854F, 0.0F));
		PartDefinition leftArm = partdefinition.addOrReplaceChild("leftArm",
				CubeListBuilder.create().texOffs(24, 0).mirror().addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.505F)).mirror(false).texOffs(40, 0).addBox(-4.0F, -3.0F, -2.0F, 6.0F, 13.0F, 4.0F, new CubeDeformation(1.0F)),
				PartPose.offset(-5.0F, 2.0F, 0.0F));
		PartDefinition rightArm = partdefinition.addOrReplaceChild("rightArm",
				CubeListBuilder.create().texOffs(40, 0).mirror().addBox(-2.0F, -3.0F, -2.0F, 6.0F, 13.0F, 4.0F, new CubeDeformation(1.0F)).mirror(false).texOffs(24, 0).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.505F)),
				PartPose.offset(5.0F, 2.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int rgb) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
		leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
		rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
	}

	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount;
		this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount;
	}
}
