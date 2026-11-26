package net.furtitwo.createstuffadditions.logic.gadget;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Heap of Experience: right-click releases 2 experience orbs (6 xp each) per item; sneaking uses only one item. */
public final class HeapOfExperience {
	private static final int ORBS_PER_ITEM = 2;
	private static final int XP_PER_ORB = 6;

	private HeapOfExperience() {
	}

	public static void onUse(Level level, Player player, ItemStack stack) {
		double x = player.getX();
		double y = player.getY();
		double z = player.getZ();
		GadgetSounds.play(level, x, y, z, SoundEvents.AMETHYST_BLOCK_BREAK, 1, 1);
		if (player.isShiftKeyDown()) {
			stack.shrink(1);
			dropOrbs(level, x, y, z);
			return;
		}
		for (int i = 0; i < stack.getCount(); i++)
			dropOrbs(level, x, y, z);
		if (player.getMainHandItem().getItem() == stack.getItem())
			clearHand(player, InteractionHand.MAIN_HAND);
		else if (player.getOffhandItem().getItem() == stack.getItem())
			clearHand(player, InteractionHand.OFF_HAND);
	}

	private static void dropOrbs(Level level, double x, double y, double z) {
		if (level instanceof ServerLevel serverLevel)
			for (int i = 0; i < ORBS_PER_ITEM; i++)
				serverLevel.addFreshEntity(new ExperienceOrb(serverLevel, x, y, z, XP_PER_ORB));
	}

	private static void clearHand(Player player, InteractionHand hand) {
		player.setItemInHand(hand, ItemStack.EMPTY);
		player.getInventory().setChanged();
	}
}
