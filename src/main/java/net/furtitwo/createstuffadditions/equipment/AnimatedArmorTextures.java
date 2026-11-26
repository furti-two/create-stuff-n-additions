package net.furtitwo.createstuffadditions.equipment;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Frame-based armour textures (andesite tier). The frame comes from the wearer's tick count, so nothing is written
 * to the ItemStack: the old version stored the frame in the stack every tick.
 */
public final class AnimatedArmorTextures {
	private AnimatedArmorTextures() {
	}

	/** {@code prefix + 1..count + ".png"}. */
	public static ResourceLocation[] frames(String prefix, int count) {
		ResourceLocation[] frames = new ResourceLocation[count];
		for (int i = 0; i < count; i++)
			frames[i] = ResourceLocation.parse(prefix + (i + 1) + ".png");
		return frames;
	}

	/** Cycles 1, 1, 2, ..., count like the old 0..8 counter; first frame when not running. */
	public static ResourceLocation pick(ResourceLocation[] frames, Entity entity, boolean running) {
		if (!running || entity == null)
			return frames[0];
		int step = entity.tickCount % (frames.length + 1);
		return frames[Math.max(0, step - 1)];
	}

	/** 5% chance per tick of a smoke puff above the wearer's back (server side). */
	public static void chimneySmoke(Level level, Player player) {
		if (level instanceof ServerLevel server && server.random.nextFloat() < 0.05f)
			server.sendParticles(ParticleTypes.SMOKE, player.getX(), player.getY() + 1.45, player.getZ(), 1, 0.15, 0.15, 0.15, 0);
	}
}
