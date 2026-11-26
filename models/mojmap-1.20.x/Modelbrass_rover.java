// Made with Blockbench 4.8.1
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class Modelbrass_rover<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "brass_rover"), "main");
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart belt;
	private final ModelPart belt2;
	private final ModelPart shaft;
	private final ModelPart shaft2;

	public Modelbrass_rover(ModelPart root) {
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.belt = root.getChild("belt");
		this.belt2 = root.getChild("belt2");
		this.shaft = root.getChild("shaft");
		this.shaft2 = root.getChild("shaft2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head",
				CubeListBuilder.create().texOffs(0, 0)
						.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-2.0F, -8.0F, 1.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(4, 0)
						.addBox(-3.0F, -8.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(4, 3)
						.addBox(-2.0F, -9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 19.0F, 0.0F));

		PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1",
				CubeListBuilder.create().texOffs(4, 0).addBox(-1.5F, -0.5F, 0.5F, 1.0F, 1.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.5F, -7.5F, 1.5F, 0.0F, 3.1416F, 0.0F));

		PartDefinition cube_r2 = head.addOrReplaceChild("cube_r2",
				CubeListBuilder.create().texOffs(4, 0).addBox(-1.5F, -0.5F, 0.5F, 1.0F, 1.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.5F, -7.5F, 1.5F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r3 = head.addOrReplaceChild("cube_r3",
				CubeListBuilder.create().texOffs(4, 0).addBox(-1.5F, -0.5F, 0.5F, 1.0F, 1.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.5F, -7.5F, 1.5F, 0.0F, -1.5708F, 0.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(24, 0).addBox(
				-3.0F, -5.0F, -2.0F, 6.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition belt = partdefinition.addOrReplaceChild("belt",
				CubeListBuilder.create().texOffs(0, 12)
						.addBox(3.0F, -4.01F, -4.0F, 3.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(22, 12)
						.addBox(4.0F, -4.01F, -4.0F, 1.0F, 4.0F, 8.0F, new CubeDeformation(-0.25F)).texOffs(38, 8)
						.addBox(4.5F, -3.01F, -3.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(38, 8)
						.addBox(4.5F, -3.01F, 1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition belt2 = partdefinition.addOrReplaceChild("belt2", CubeListBuilder.create().texOffs(0, 12)
				.mirror().addBox(-6.0F, -4.01F, -4.0F, 3.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(22, 12).mirror().addBox(-5.0F, -4.01F, -4.0F, 1.0F, 4.0F, 8.0F, new CubeDeformation(-0.25F))
				.mirror(false).texOffs(38, 8).addBox(-5.5F, -3.01F, -3.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(38, 8).addBox(-5.5F, -3.01F, 1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition shaft = partdefinition.addOrReplaceChild("shaft",
				CubeListBuilder.create().texOffs(32, 8)
						.addBox(0.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(32, 8)
						.addBox(-10.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(4.5F, 21.99F, -2.0F));

		PartDefinition shaft2 = partdefinition.addOrReplaceChild("shaft2",
				CubeListBuilder.create().texOffs(32, 8)
						.addBox(0.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(32, 8)
						.addBox(-10.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(4.5F, 21.99F, 2.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		belt.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		belt2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		shaft.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		shaft2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {
		this.head.yRot = netHeadYaw / (180F / (float) Math.PI);
		this.head.xRot = headPitch / (180F / (float) Math.PI);
		this.shaft2.xRot = ageInTicks;
		this.shaft.xRot = ageInTicks;
	}
}