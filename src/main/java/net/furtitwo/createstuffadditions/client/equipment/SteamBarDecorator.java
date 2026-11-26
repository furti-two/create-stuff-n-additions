package net.furtitwo.createstuffadditions.client.equipment;

import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;

import net.furtitwo.createstuffadditions.fuel.FuelStorage;
import net.furtitwo.createstuffadditions.fuel.FuelType;
import net.furtitwo.createstuffadditions.fuel.FuelableItem;

/**
 * Draws two ✨semi hardcoded✨ vanilla-style bar per fluid in inventory slots: fuel on top, water at the bottom
 * Works with plain {@code item/generated} models, unlike a custom item renderer.
 */
public final class SteamBarDecorator implements IItemDecorator {
	public static final SteamBarDecorator INSTANCE = new SteamBarDecorator();
	private static final int WIDTH = 13;

	private SteamBarDecorator() {
	}

	@Override
	public boolean render(GuiGraphics graphics, Font font, ItemStack stack, int x, int y) {
		if (!(stack.getItem() instanceof FuelableItem fuelable))
			return false;
		IFluidHandlerItem handler = FuelStorage.handler(stack);
		if (handler == null) return false;
		FuelType type = fuelable.getFuelType();
		int count = Math.min(type.tankCount(), handler.getTanks());
		for (int i = 0; i < count; i++) {
			int capacity = handler.getTankCapacity(i);
			float ratio = capacity <= 0 ? 0 : Math.min(1f, handler.getFluidInTank(i).getAmount() / (float) capacity);
			int barY = y + 13 - 2 * (count - 1 - i);
			int left = x + 2;
            /// Draw the background and then the progress bar
            graphics.fill(RenderType.guiOverlay(), left, barY, left + WIDTH, barY + 2, 0xFF000000);
			graphics.fill(RenderType.guiOverlay(), left, barY, left + Math.round(WIDTH * ratio), barY + 1, 0xFF000000 | type.tank(i).barColor);
		}
		return false;
	}
}
