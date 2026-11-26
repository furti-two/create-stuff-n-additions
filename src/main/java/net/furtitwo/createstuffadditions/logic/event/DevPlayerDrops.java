package net.furtitwo.createstuffadditions.logic.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.Nullable;

/** Easter egg: the mod's developers and friends drop a signature item when they die. */
@EventBusSubscriber
public final class DevPlayerDrops {
	private DevPlayerDrops() {
	}

	@Nullable
	private static ItemLike signatureDrop(String name) {
		return switch (name) {
			case "Furti_Two" -> Items.SNOWBALL;
			case "la_planche" -> Items.OAK_PLANKS;
			case "Doblou" -> Items.SEAGRASS;
			case "To0pa" -> Items.SLIME_BALL;
			case "Sweetygamer2" -> Items.OXEYE_DAISY;
			case "SelinaDoesStuff" -> Items.PINK_WOOL;
			case "MasinTheRock" -> Items.STONE;
			case "Zottum" -> Items.CARVED_PUMPKIN;
			default -> null;
		};
	}

	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		LivingEntity entity = event.getEntity();
		if (!(entity instanceof Player) || !(entity.level() instanceof ServerLevel level))
			return;
		ItemLike drop = signatureDrop(entity.getDisplayName().getString());
		if (drop == null)
			return;
		ItemEntity item = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(drop));
		item.setPickUpDelay(10);
		level.addFreshEntity(item);
	}
}
