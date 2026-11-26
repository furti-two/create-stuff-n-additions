package net.furtitwo.createstuffadditions.logic.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;

/** Experience tools: 10% chance to drop a 1 XP orb when breaking a block or hitting an entity. */
public final class ExperienceTools {
	private static final double ORB_CHANCE = 0.1;

	private ExperienceTools() {
	}

	public static void onBlockBroken(Level level, BlockPos pos) {
		if (Math.random() < ORB_CHANCE && level instanceof ServerLevel server)
			server.addFreshEntity(new ExperienceOrb(server, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1));
	}

	public static void onHit(Entity target) {
		if (Math.random() < ORB_CHANCE && target.level() instanceof ServerLevel server)
			server.addFreshEntity(new ExperienceOrb(server, target.getX(), target.getY(), target.getZ(), 1));
	}
}
