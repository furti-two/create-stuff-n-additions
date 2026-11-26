
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.furtitwo.createstuffadditions.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;

import net.minecraft.world.level.block.Block;

import net.furtitwo.createstuffadditions.block.BrassCubeBlock;
import net.furtitwo.createstuffadditions.CreateSaMod;

public class CreateSaModBlocks {
	public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(CreateSaMod.MODID);
	public static final DeferredBlock<Block> BRASS_CUBE = REGISTRY.register("brass_cube", BrassCubeBlock::new);
	// Start of user code block custom blocks
	// End of user code block custom blocks
}
