
package net.furtitwo.createstuffadditions.item;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;

import net.furtitwo.createstuffadditions.util.TooltipLines;
import net.furtitwo.createstuffadditions.logic.gadget.CopperMagnet;

import java.util.List;

public class CopperMagnetItem extends Item {
	public CopperMagnetItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.EAT;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		if (Screen.hasShiftDown()) {
			list.add(Component.literal(Component.translatable("item_desc.holdshift").getString().replace("\u00A77", "\u00A7f") + ""));
			list.add(Component.literal(" "));
			list.addAll(TooltipLines.wrap(Component.translatable("item_desc.copper_magnet.desc").getString(), 0));
			list.add(Component.literal(" "));
			list.add(Component.literal(Component.translatable("item_desc.used").getString()));
			list.addAll(TooltipLines.wrap(Component.translatable("item_desc.copper_magnet.desc2").getString(), 1));
		} else {
			list.add(Component.literal(Component.translatable("item_desc.holdshift").getString() + ""));
		}
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected)
			CopperMagnet.onHeldTick(world, entity);
	}
}
