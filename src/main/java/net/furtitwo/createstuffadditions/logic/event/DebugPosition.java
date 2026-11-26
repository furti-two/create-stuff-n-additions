package net.furtitwo.createstuffadditions.logic.event;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import net.furtitwo.createstuffadditions.network.CreateSaModVariables;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;

/**
 * Debug leftover: stores the x/y/z arguments of a command in {@code CreateSaModVariables.db_x/db_y/db_z} and echoes them.
 * Nothing calls it any more (no command registers it) and nothing reads those variables.
 */
public final class DebugPosition {
	private DebugPosition() {
	}

	public static void onCommand(CommandContext<CommandSourceStack> arguments, Entity entity) {
		if (entity == null)
			return;
		double x = DoubleArgumentType.getDouble(arguments, "x");
		double y = DoubleArgumentType.getDouble(arguments, "y");
		double z = DoubleArgumentType.getDouble(arguments, "z");
		CreateSaModVariables.db_x = x;
		CreateSaModVariables.db_y = y;
		CreateSaModVariables.db_z = z;
		if (entity instanceof Player player && !player.level().isClientSide())
			player.displayClientMessage(Component.literal(x + " " + y + " " + z), false);
	}
}
