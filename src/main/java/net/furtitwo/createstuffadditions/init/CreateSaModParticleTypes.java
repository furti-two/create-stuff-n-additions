
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.furtitwo.createstuffadditions.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleType;

import net.furtitwo.createstuffadditions.CreateSaMod;

public class CreateSaModParticleTypes {
	public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(Registries.PARTICLE_TYPE, CreateSaMod.MODID);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WATER_CLOUD = REGISTRY.register("water_cloud", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STEAM_CLOUD = REGISTRY.register("steam_cloud", () -> new SimpleParticleType(false));
}
