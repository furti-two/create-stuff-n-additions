// Made with Blockbench 4.3.1
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports

public class Modelbrass_drone<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "brass_drone"), "main");
	private final ModelPart drone;

	public Modelbrass_drone(ModelPart root) {
		this.drone = root.getChild("drone");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition drone = partdefinition.addOrReplaceChild("drone",
				CubeListBuilder.create().texOffs(0, 0)
						.addBox(-3.0F, -2.5F, -4.0F, 6.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-1.0F, -6.5F, 1.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(4, 3)
						.addBox(-1.0F, -7.5F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(4, 0)
						.addBox(-2.0F, -6.5F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 13)
						.addBox(3.0F, -2.5F, -8.0F, 6.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(0, 22)
						.addBox(3.5F, -2.0F, -7.5F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(0, 13)
						.addBox(3.0F, -2.5F, 2.0F, 6.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(0, 22)
						.addBox(3.5F, -2.0F, 2.5F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(0, 13)
						.addBox(-9.0F, -2.5F, -8.0F, 6.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(0, 22)
						.addBox(-8.5F, -2.0F, -7.5F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(0, 22)
						.addBox(-8.5F, -2.0F, 2.5F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(0, 13)
						.addBox(-9.0F, -2.5F, 2.0F, 6.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 21.5F, 0.0F));

		PartDefinition cube_r1 = drone.addOrReplaceChild("cube_r1",
				CubeListBuilder.create().texOffs(4, 0).addBox(-1.5F, -1.0F, 0.5F, 1.0F, 1.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.5F, -5.5F, 1.5F, 0.0F, -1.5708F, 0.0F));

		PartDefinition cube_r2 = drone.addOrReplaceChild("cube_r2",
				CubeListBuilder.create().texOffs(4, 0).addBox(-1.5F, -1.0F, 0.5F, 1.0F, 1.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.5F, -5.5F, 1.5F, 0.0F, 3.1416F, 0.0F));

		PartDefinition cube_r3 = drone.addOrReplaceChild("cube_r3",
				CubeListBuilder.create().texOffs(4, 0).addBox(-1.5F, -1.0F, 0.5F, 1.0F, 1.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.5F, -5.5F, 1.5F, 0.0F, 1.5708F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		drone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {
		this.drone.yRot = netHeadYaw / (180F / (float) Math.PI);
		this.drone.xRot = headPitch / (180F / (float) Math.PI);
	}
}