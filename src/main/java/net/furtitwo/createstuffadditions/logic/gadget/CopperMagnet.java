package net.furtitwo.createstuffadditions.logic.gadget;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Copper Magnet: while held, pulls dropped items (strongly) and every other entity (weakly) within 4 blocks. */
public final class CopperMagnet {
	private static final double ITEM_PULL = 0.2;
	private static final double ENTITY_PULL = 0.005;

	private CopperMagnet() {
	}

	public static void onHeldTick(Level level, Entity holder) {
		Vec3 center = new Vec3(holder.getX(), holder.getY() + 1, holder.getZ());
		for (Entity other : level.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(4), e -> true)) {
			Vec3 toCenter = center.subtract(other.position());
			if (other instanceof ItemEntity)
				other.setDeltaMovement(toCenter.scale(ITEM_PULL));
			else if (other != holder)
				other.setDeltaMovement(other.getDeltaMovement().add(toCenter.scale(ENTITY_PULL)));
		}
	}
}
