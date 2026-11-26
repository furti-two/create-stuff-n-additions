package net.furtitwo.createstuffadditions.item.tool;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import net.furtitwo.createstuffadditions.init.CreateSaModItems;

import com.simibubi.create.AllItems;

import java.util.function.Supplier;

/** Tool materials. Values are those of the former per-item MCreator tiers. */
public enum CSAToolTier implements Tier {
	BRASS(450, 8f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 14, () -> Ingredient.of(tag("c:ingots/brass"))),
	COPPER(131, 5f, BlockTags.INCORRECT_FOR_IRON_TOOL, 11, () -> Ingredient.of(tag("c:ingots/copper"))),
	/** The copper hoe always had its own, faster tier. */
	COPPER_HOE(131, 8f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 11, () -> Ingredient.of(tag("c:ingots/copper"))),
	ZINC(250, 7f, BlockTags.INCORRECT_FOR_IRON_TOOL, 11, () -> Ingredient.of(tag("c:ingots/zinc"))),
	ROSE_QUARTZ(1644, 9f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 14, () -> Ingredient.of(new net.minecraft.world.item.ItemStack(Items.GOLD_INGOT), AllItems.POLISHED_ROSE_QUARTZ.asStack(1))),
	BLAZING(450, 12f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 2, () -> Ingredient.of(Items.GOLD_INGOT, Items.BLAZE_POWDER)),
	EXPERIENCE(270, 7f, BlockTags.INCORRECT_FOR_IRON_TOOL, 800, () -> Ingredient.of(CreateSaModItems.HEAP_OF_EXPERIENCE.get()));

	private final int uses;
	private final float speed;
	private final TagKey<Block> incorrectBlocks;
	private final int enchantmentValue;
	private final Supplier<Ingredient> repair;

	CSAToolTier(int uses, float speed, TagKey<Block> incorrectBlocks, int enchantmentValue, Supplier<Ingredient> repair) {
		this.uses = uses;
		this.speed = speed;
		this.incorrectBlocks = incorrectBlocks;
		this.enchantmentValue = enchantmentValue;
		this.repair = repair;
	}

	private static TagKey<net.minecraft.world.item.Item> tag(String id) {
		return ItemTags.create(ResourceLocation.parse(id));
	}

	@Override
	public int getUses() {
		return uses;
	}

	@Override
	public float getSpeed() {
		return speed;
	}

	@Override
	public float getAttackDamageBonus() {
		return 0;
	}

	@Override
	public TagKey<Block> getIncorrectBlocksForDrops() {
		return incorrectBlocks;
	}

	@Override
	public int getEnchantmentValue() {
		return enchantmentValue;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return repair.get();
	}
}
