package net.furtitwo.createstuffadditions.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import net.furtitwo.createstuffadditions.network.CreateSaModVariables.MapVariables;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Shows the mod's enchantments that the server config disables as a red "Name [Disabled]" line,
 * on enchanted items and on enchanted books. Uses the server values synced through the map variables.
 */
@EventBusSubscriber(Dist.CLIENT)
public final class DisabledEnchantmentTooltip {
	/** Enchantment -> "is enabled" flag. */
	private static final Map<ResourceKey<Enchantment>, Predicate<MapVariables>> TOGGLES = Map.of(
			key("above_the_clouds"), v -> v.enableAboveCloudEnchant,
			key("digging"), v -> v.enableDiggingEnchant,
			key("chunk_eater"), v -> v.enableChunkEaterEnchant,
			key("impact"), v -> v.enableImpactEnchant,
			key("gravity_gun"), v -> v.enableGravityGunEnchant,
			key("hellfire"), v -> v.enableHellfireEnchant);

	private DisabledEnchantmentTooltip() {
	}

	private static ResourceKey<Enchantment> key(String name) {
		return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("create_sa", name));
	}

	@SubscribeEvent
	public static void onTooltip(ItemTooltipEvent event) {
		var level = Minecraft.getInstance().level;
		if (level == null)
			return;
		ItemStack stack = event.getItemStack();
		ItemEnchantments enchants = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
		ItemEnchantments stored = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
		if (enchants.isEmpty() && stored.isEmpty())
			return;
		MapVariables vars = MapVariables.get(level);
		List<Component> lines = event.getToolTip();
		mark(enchants, vars, lines);
		mark(stored, vars, lines);
	}

	private static void mark(ItemEnchantments enchants, MapVariables vars, List<Component> lines) {
		for (var entry : enchants.entrySet()) {
			Holder<Enchantment> holder = entry.getKey();
			Predicate<MapVariables> enabled = holder.unwrapKey().map(TOGGLES::get).orElse(null);
			if (enabled == null || enabled.test(vars))
				continue;
			Component original = Enchantment.getFullname(holder, entry.getIntValue());
			int index = lines.indexOf(original);
			if (index >= 0)
				lines.set(index, original.copy().withStyle(ChatFormatting.RED).append(" ")
						.append(Component.translatableWithFallback("tooltip.create_sa.enchantment_disabled", "[Disabled]")));
		}
	}
}
