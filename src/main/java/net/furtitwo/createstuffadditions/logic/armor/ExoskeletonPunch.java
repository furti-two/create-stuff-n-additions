package net.furtitwo.createstuffadditions.logic.armor;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import net.furtitwo.createstuffadditions.item.armor.ExoskeletonItem;

/** Bare-handed hits from a player wearing a fuelled exoskeleton push the target along the player's look direction. */
@EventBusSubscriber
public final class ExoskeletonPunch {
	private ExoskeletonPunch() {
	}

	@SubscribeEvent
	public static void onPlayerCriticalHit(CriticalHitEvent event) {
		Player attacker = event.getEntity();
		Entity target = event.getTarget();
		if (!attacker.getMainHandItem().isEmpty() || ExoskeletonItem.getPoweredWornBy(attacker).isEmpty())
			return;
		if (!attacker.level().isClientSide())
			attacker.level().playSound(null, attacker.blockPosition(), SoundEvents.ANVIL_PLACE, SoundSource.NEUTRAL, 0.25f, 3f);
		Vec3 push = attacker.getLookAngle().scale(0.5);
		target.setDeltaMovement(target.getDeltaMovement().add(push));
	}
}
