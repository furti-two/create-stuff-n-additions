// Made with Blockbench 4.3.1
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports

public class Modelbrass_emitter<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "brass_emitter"), "main");
	private final ModelPart head;

	public Modelbrass_emitter(ModelPart root) {
		this.head = root.getChild("head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(
				-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head2 = head.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(32, 0).mirror()
				.addBox(-2.0F, -8.0F, 0.25F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.125F)).mirror(false).texOffs(36, 3)
				.mirror().addBox(-2.0F, -9.05F, 0.25F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.125F)).mirror(false)
				.texOffs(36, 0).mirror().addBox(-0.75F, -8.0F, 1.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.125F))
				.mirror(false), PartPose.offset(-1.65F, -5.125F, 0.0F));

		PartDefinition head6_r1 = head2.addOrReplaceChild("head6_r1",
				CubeListBuilder.create().texOffs(36, 0).mirror()
						.addBox(0.75F, -1.5F, 0.75F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.125F)).mirror(false),
				PartPose.offsetAndRotation(-1.5F, -6.5F, 0.75F, 0.0F, 1.5708F, 0.0F));

		PartDefinition head5_r1 = head2.addOrReplaceChild("head5_r1",
				CubeListBuilder.create().texOffs(36, 0).mirror()
						.addBox(0.75F, -1.5F, 0.75F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.125F)).mirror(false),
				PartPose.offsetAndRotation(-1.5F, -6.5F, 0.75F, 0.0F, 3.1416F, 0.0F));

		PartDefinition head4_r1 = head2.addOrReplaceChild("head4_r1",
				CubeListBuilder.create().texOffs(36, 0).mirror()
						.addBox(0.75F, -1.5F, 0.75F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.125F)).mirror(false),
				PartPose.offsetAndRotation(-1.5F, -6.5F, 0.75F, 0.0F, -1.5708F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {
		this.head.yRot = netHeadYaw / (180F / (float) Math.PI);
		this.head.xRot = headPitch / (180F / (float) Math.PI);
	}
}