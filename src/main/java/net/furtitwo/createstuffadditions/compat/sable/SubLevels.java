package net.furtitwo.createstuffadditions.compat.sable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;

/**
 * Optional compatibility with Sable (moving block structures called sub-levels), through the bundled Sable Companion API.
 * <p>
 * A sub-level's blocks are stored in the same {@link Level} but far away, in a "plot", and drawn at their moving world pose.
 * Sable already makes {@code Level#clip} hit them (the hit is returned in plot coordinates) and patches entity distance and
 * reach checks. What the mod must handle itself: converting a plot position to where it currently is in the world, and
 * finding blocks of a structure that is at a given world position.
 * <p>
 * Without Sable installed, the companion's default implementation returns positions unchanged and only looks at the world,
 * so every method here behaves exactly like the plain vanilla code it replaces.
 */
public final class SubLevels {
	private static final SableCompanion API = SableCompanion.INSTANCE;

	private SubLevels() {
	}

	/** Current world position of {@code pos}, which may be inside a sub-level plot. */
	public static Vec3 toWorld(Level level, Vec3 pos) {
		return API.projectOutOfSubLevel(level, pos);
	}

	/** True when a non-air block occupies the world point, in the world itself or in a sub-level currently there. */
	public static boolean hasBlockAt(Level level, Vec3 worldPoint) {
		return API.findIncludingSubLevels(level, worldPoint, true, (SubLevelAccess) null, (subLevel, pos) -> !level.isEmptyBlock(pos));
	}

	/**
	 * Block position under {@code entity}'s feet in the space it stands in: inside the plot of the sub-level it stands on,
	 * otherwise the plain world block position.
	 */
	public static BlockPos feetBlockPos(Entity entity) {
		SubLevelAccess subLevel = API.getTrackingSubLevel(entity);
		Vec3 pos = entity.position();
		return BlockPos.containing(subLevel != null ? subLevel.logicalPose().transformPositionInverse(pos) : pos);
	}
}
