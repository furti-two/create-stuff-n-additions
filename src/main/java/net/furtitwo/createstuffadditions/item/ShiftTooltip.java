package net.furtitwo.createstuffadditions.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import net.furtitwo.createstuffadditions.util.TooltipLines;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * The mod's standard "Hold [Shift]" tooltip: a header line (white while SHIFT is held), lines always shown,
 * then the wrapped description and an optional "When used:" section while SHIFT is held.
 * Client only: only call it from {@code appendHoverText}.
 */
public final class ShiftTooltip {
	private ShiftTooltip() {
	}

	/**
	 * @param always    lines shown under the header in both states (fuel amounts...), may be null
	 * @param descKey   wrapped description key shown with SHIFT
	 * @param usedKey   optional wrapped "When used:" key shown with SHIFT, may be null
	 */
	public static void append(List<Component> list, @Nullable Consumer<List<Component>> always, String descKey, @Nullable String usedKey) {
		append(list, always, descKey, "item_desc.used", usedKey);
	}

	/** Same as above with a custom header for the second section (e.g. "Right click:"). */
	public static void append(List<Component> list, @Nullable Consumer<List<Component>> always, String descKey, String usedHeaderKey, @Nullable String usedKey) {
		boolean shift = Screen.hasShiftDown();
		Component header = Component.translatable("item_desc.holdshift");
		list.add(shift ? Component.literal(header.getString().replace("§7", "§f")) : header);
		if (always != null)
			always.accept(list);
		if (!shift)
			return;
		list.add(Component.literal(" "));
		list.addAll(wrap(descKey, 0));
		if (usedKey != null) {
			list.add(Component.literal(" "));
			list.add(Component.translatable(usedHeaderKey));
			list.addAll(wrap(usedKey, 1));
		}
	}

	public static List<Component> wrap(String key, int indent) {
		return TooltipLines.wrap(Component.translatable(key).getString(), indent);
	}
}
