// Made with Blockbench 4.3.1
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports

public class Modeldrill_module<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "drill_module"), "main");
	private final ModelPart body;
	private final ModelPart static_drill;
	private final ModelPart moving_drill;

	public Modeldrill_module(ModelPart root) {
		this.body = root.getChild("body");
		this.static_drill = root.getChild("static_drill");
		this.moving_drill = root.getChild("moving_drill");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body",
				CubeListBuilder.create().texOffs(0, 0)
						.addBox(-4.0F, -6.0F, -1.0F, 8.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 9)
						.addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition static_drill = partdefinition.addOrReplaceChild("static_drill",
				CubeListBuilder.create().texOffs(14, 26)
						.addBox(-1.0F, 6.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 19)
						.addBox(-2.5F, 0.0F, -2.5F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(20, 21)
						.addBox(-1.5F, 4.0F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(22, 27)
						.addBox(-1.5F, 0.0F, -4.5F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 26)
						.addBox(-2.0F, 2.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 26.0F, 0.0F));

		PartDefinition cube_r1 = static_drill
				.addOrReplaceChild("cube_r1",
						CubeListBuilder.create().texOffs(22, 27).addBox(-1.5F, -4.0F, -4.5F, 3.0F, 3.0F, 2.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition cube_r2 = static_drill
				.addOrReplaceChild("cube_r2",
						CubeListBuilder.create().texOffs(22, 27).addBox(-1.5F, -4.0F, -4.5F, 3.0F, 3.0F, 2.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition cube_r3 = static_drill
				.addOrReplaceChild("cube_r3",
						CubeListBuilder.create().texOffs(22, 27).addBox(-1.5F, -4.0F, -4.5F, 3.0F, 3.0F, 2.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition moving_drill = partdefinition.addOrReplaceChild("moving_drill",
				CubeListBuilder.create().texOffs(46, 26)
						.addBox(-1.0F, 6.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(32, 19)
						.addBox(-2.5F, 0.0F, -2.5F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(52, 21)
						.addBox(-1.5F, 4.0F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(54, 27)
						.addBox(-1.5F, 0.0F, -4.5F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(32, 26)
						.addBox(-2.0F, 2.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 26.0F, 0.0F));

		PartDefinition cube_r4 = moving_drill
				.addOrReplaceChild("cube_r4",
						CubeListBuilder.create().texOffs(54, 27).addBox(-1.5F, -4.0F, -4.5F, 3.0F, 3.0F, 2.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition cube_r5 = moving_drill
				.addOrReplaceChild("cube_r5",
						CubeListBuilder.create().texOffs(54, 27).addBox(-1.5F, -4.0F, -4.5F, 3.0F, 3.0F, 2.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition cube_r6 = moving_drill
				.addOrReplaceChild("cube_r6",
						CubeListBuilder.create().texOffs(54, 27).addBox(-1.5F, -4.0F, -4.5F, 3.0F, 3.0F, 2.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		static_drill.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		moving_drill.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {
		this.moving_drill.yRot = ageInTicks;
	}
}