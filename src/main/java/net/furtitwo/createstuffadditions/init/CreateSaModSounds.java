
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.furtitwo.createstuffadditions.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

import net.furtitwo.createstuffadditions.CreateSaMod;

public class CreateSaModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, CreateSaMod.MODID);
	public static final DeferredHolder<SoundEvent, SoundEvent> STEAM_NOISE = REGISTRY.register("steam_noise", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("create_sa", "steam_noise")));
	public static final DeferredHolder<SoundEvent, SoundEvent> COGS_SOUND = REGISTRY.register("cogs_sound", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("create_sa", "cogs_sound")));
}
