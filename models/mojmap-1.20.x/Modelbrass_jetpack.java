// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class Modelbrass_jetpack<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "brass_jetpack"), "main");
	private final ModelPart body;
	private final ModelPart bone4;
	private final ModelPart bone2;
	private final ModelPart rightarm;
	private final ModelPart leftarm;

	public Modelbrass_jetpack(ModelPart root) {
		this.body = root.getChild("body");
		this.bone4 = this.body.getChild("bone4");
		this.bone2 = this.body.getChild("bone2");
		this.rightarm = root.getChild("rightarm");
		this.leftarm = root.getChild("leftarm");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body",
				CubeListBuilder.create().texOffs(0, 16)
						.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)).texOffs(0, 0)
						.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.505F)).texOffs(40, 0)
						.addBox(-2.0F, 0.5F, 3.5F, 4.0F, 6.0F, 5.0F, new CubeDeformation(0.5F)),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bone4 = body.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(40, 11).mirror()
				.addBox(-4.5F, -3.0F, -0.5F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.5F)).mirror(false).texOffs(49, 32)
				.mirror().addBox(-4.5F, 2.75F, -0.5F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.5F)).mirror(false),
				PartPose.offset(-2.25F, 3.0F, 4.25F));

		PartDefinition bone2 = body.addOrReplaceChild("bone2",
				CubeListBuilder.create().texOffs(40, 11)
						.addBox(-0.5F, -3.0F, -0.5F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.5F)).texOffs(49, 32)
						.addBox(-0.5F, 2.75F, -0.5F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.5F)),
				PartPose.offset(2.25F, 3.0F, 4.25F));

		PartDefinition rightarm = partdefinition.addOrReplaceChild("rightarm",
				CubeListBuilder.create().texOffs(24, 16)
						.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)).texOffs(24, 0)
						.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.505F)),
				PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition leftarm = partdefinition.addOrReplaceChild("leftarm",
				CubeListBuilder.create().texOffs(24, 16).mirror()
						.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)).mirror(false)
						.texOffs(24, 0).mirror()
						.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.505F)).mirror(false),
				PartPose.offset(5.0F, 2.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		rightarm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		leftarm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {
		this.rightarm.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount;
		this.leftarm.xRot = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount;
	}
}