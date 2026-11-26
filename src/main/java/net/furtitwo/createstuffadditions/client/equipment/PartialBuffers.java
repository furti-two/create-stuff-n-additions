package net.furtitwo.createstuffadditions.client.equipment;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;

/** Cached {@link SuperByteBuffer}s of our partials (Create's buffer cache, invalidated by Create on resource reload). */
final class PartialBuffers {
	private static final BlockState REFERENCE = Blocks.AIR.defaultBlockState();

	private PartialBuffers() {
	}

	static SuperByteBuffer get(PartialModel partial) {
		return CachedBuffers.partial(partial, REFERENCE);
	}
}
