package net.furtitwo.createstuffadditions.client.equipment;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import net.createmod.catnip.render.SuperByteBuffer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Everything related to the armor that the client render, the body pieces, cogwheels, rotating fan ...etc.
 *
 * @param armorParts  bakes the body/arm parts used as the armor model
 * @param firstPerson optional arm overlay/replacement in 1st person
 * @param back        partial models rendered on "the back" of the armor
 */
public record ChestplateVisual(Function<EntityModelSet, Parts> armorParts, @Nullable FirstPerson firstPerson, PartialRenderer back) {
	/** Body and arm parts, already mapped left/right. */
	public record Parts(ModelPart body, ModelPart leftArm, ModelPart rightArm) {}

	/** First-person arm, parts to bake and associated texture */
	public record FirstPerson(Function<EntityModelSet, Parts> parts, ResourceLocation texture) {}

	/** Per-frame values shared by every part in ONE chestplate. */
	public record Frame(LivingEntity entity, ItemStack stack, HumanoidModel<?> model, PoseStack ms, VertexConsumer vc, int light, float time, boolean flying, float spin) {
		/** Renders a partial with local transform, placed on the wearer's chest. */
		public void part(dev.engine_room.flywheel.lib.model.baked.PartialModel partial, Function<SuperByteBuffer, SuperByteBuffer> transform) {
			transform.apply(PartialBuffers.get(partial)).disableDiffuse().light(light).renderInto(ms, vc);
		}

		/** Rotation for {@code degreesPerTick} at the current render time. */
		public float angle(float degreesPerTick) {
			return (float) Math.toRadians(degreesPerTick * time % 360);
		}
	}

	@FunctionalInterface
	public interface PartialRenderer {
		void render(Frame frame);
	}
}
