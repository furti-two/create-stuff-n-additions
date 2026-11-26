package net.furtitwo.createstuffadditions.util;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/** Word-wraps translated tooltip text to lines of at most {@value #LINE_LENGTH} visible characters. */
public final class TooltipLines {
	private static final int LINE_LENGTH = 38;
	private static final String PURPLE = "§5";

	private TooltipLines() {
	}

	/**
	 * @param style 0 = purple, 1 = purple indented by one space, 2 = no prefix
	 */
	public static List<Component> wrap(String text, int style) {
		String prefix = switch (style) {
			case 0 -> PURPLE;
			case 1 -> " " + PURPLE;
			default -> "";
		};
		List<Component> lines = new ArrayList<>();
		StringBuilder line = new StringBuilder();
		for (String word : text.split(" ")) {
			int wordLength = visibleLength(word);
			if (wordLength > LINE_LENGTH) {
				if (!line.isEmpty()) {
					lines.add(Component.literal(prefix + line.toString().trim()));
					line.setLength(0);
				}
				lines.add(Component.literal(prefix + word));
			} else if (visibleLength(line.toString().trim()) + wordLength + 1 <= LINE_LENGTH) {
				if (!line.isEmpty())
					line.append(' ');
				line.append(word);
			} else {
				lines.add(Component.literal(prefix + line.toString().trim()));
				line.setLength(0);
				line.append(word);
			}
		}
		if (!line.isEmpty())
			lines.add(Component.literal(prefix + line.toString().trim()));
		return lines;
	}

	/** Length without the colour codes used in the mod's descriptions (§d, §5, §7). */
	private static int visibleLength(String s) {
		return s.replace("§d", "").replace("§5", "").replace("§7", "").length();
	}
}
