package net.furtitwo.createstuffadditions.logic.gadget;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

/** Sound helper shared by the gadgets. */
final class GadgetSounds {
	private GadgetSounds() {
	}

	/**
	 * Server: broadcasts the sound centred on the block containing (x, y, z). Client: plays it locally at the exact
	 * coordinates. Always in the NEUTRAL category.
	 */
	static void play(Level level, double x, double y, double z, SoundEvent sound, float volume, float pitch) {
		if (!level.isClientSide())
			level.playSound(null, BlockPos.containing(x, y, z), sound, SoundSource.NEUTRAL, volume, pitch);
		else
			level.playLocalSound(x, y, z, sound, SoundSource.NEUTRAL, volume, pitch, false);
	}
}
