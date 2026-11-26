// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class Modelcopper_exoskeleton<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "copper_exoskeleton"), "main");
	private final ModelPart body;
	private final ModelPart rightArm;
	private final ModelPart leftArm;

	public Modelcopper_exoskeleton(ModelPart root) {
		this.body = root.getChild("body");
		this.rightArm = root.getChild("rightArm");
		this.leftArm = root.getChild("leftArm");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body",
				CubeListBuilder.create().texOffs(0, 16).mirror()
						.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.501F)).mirror(false)
						.texOffs(24, 16).addBox(-4.0F, -1.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.025F))
						.texOffs(0, 2).mirror().addBox(2.0F, -2.0F, 3.5F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.5F))
						.mirror(false).texOffs(0, 2).mirror()
						.addBox(-6.0F, -2.0F, 3.5F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r6_r1 = body
				.addOrReplaceChild("cube_r6_r1",
						CubeListBuilder.create().texOffs(42, 0).addBox(-2.5F, -4.5F, -3.0F, 5.0F, 6.0F, 6.0F,
								new CubeDeformation(0.5F)),
						PartPose.offsetAndRotation(0.0F, 5.0F, 5.5F, 0.0F, 3.1416F, 0.0F));

		PartDefinition rightArm = partdefinition.addOrReplaceChild("rightArm",
				CubeListBuilder.create().texOffs(16, 0)
						.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.502F)).texOffs(48, 15)
						.addBox(-3.0F, -3.0F, -2.0F, 4.0F, 13.0F, 4.0F, new CubeDeformation(1.0F)),
				PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition leftArm = partdefinition.addOrReplaceChild("leftArm",
				CubeListBuilder.create().texOffs(16, 0).mirror()
						.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.502F)).mirror(false)
						.texOffs(48, 15).mirror()
						.addBox(-1.0F, -3.0F, -2.0F, 4.0F, 13.0F, 4.0F, new CubeDeformation(1.0F)).mirror(false),
				PartPose.offset(5.0F, 2.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}
	
	public void translateToHand(HumanoidArm arm, PoseStack pose) {
		if (arm == HumanoidArm.RIGHT) {
			this.rightArm.translateAndRotate(pose);
		} else {
			this.leftArm.translateAndRotate(pose);
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {
		this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount;
		this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount;
	}
}