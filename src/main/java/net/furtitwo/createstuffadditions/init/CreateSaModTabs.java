
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.furtitwo.createstuffadditions.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

import net.furtitwo.createstuffadditions.CreateSaMod;

public class CreateSaModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateSaMod.MODID);
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATE_STUFF_ADDITIONS_TAB = REGISTRY.register("create_stuff_additions_tab",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.create_sa.create_stuff_additions_tab")).icon(() -> new ItemStack(CreateSaModItems.COPPER_MAGNET.get())).displayItems((parameters, tabData) -> {
				tabData.accept(CreateSaModItems.STEAM_ENGINE.get());
				tabData.accept(CreateSaModItems.HEAT_ENGINE.get());
				tabData.accept(CreateSaModItems.HYDRAULIC_ENGINE.get());
				tabData.accept(CreateSaModItems.BRASS_JETPACK_CHESTPLATE.get());
				tabData.accept(CreateSaModItems.ANDESITE_JETPACK_CHESTPLATE.get());
				tabData.accept(CreateSaModItems.COPPER_JETPACK_CHESTPLATE.get());
				tabData.accept(CreateSaModItems.BRASS_EXOSKELETON_CHESTPLATE.get());
				tabData.accept(CreateSaModItems.ANDESITE_EXOSKELETON_CHESTPLATE.get());
				tabData.accept(CreateSaModItems.COPPER_EXOSKELETON_CHESTPLATE.get());
				tabData.accept(CreateSaModItems.SMALL_FILLING_TANK.get());
				tabData.accept(CreateSaModItems.MEDIUM_FILLING_TANK.get());
				tabData.accept(CreateSaModItems.LARGE_FILLING_TANK.get());
				tabData.accept(CreateSaModItems.DRONE_CONTROLLER.get());
				tabData.accept(CreateSaModItems.BLAZING_PICKAXE.get());
				tabData.accept(CreateSaModItems.BLAZING_SHOVEL.get());
				tabData.accept(CreateSaModItems.BLAZING_AXE.get());
				tabData.accept(CreateSaModItems.BLAZING_CLEAVER.get());
				tabData.accept(CreateSaModItems.BRASS_DRONE_ITEM.get());
				tabData.accept(CreateSaModItems.COPPER_MAGNET.get());
				tabData.accept(CreateSaModItems.BRASS_DRILL_HEAD.get());
				tabData.accept(CreateSaModItems.FAN_COMPONENT.get());
				tabData.accept(CreateSaModItems.VAULT_COMPONENT.get());
				tabData.accept(CreateSaModItems.ROSE_QUARTZ_PICKAXE.get());
				tabData.accept(CreateSaModItems.ROSE_QUARTZ_SHOVEL.get());
				tabData.accept(CreateSaModItems.ROSE_QUARTZ_AXE.get());
				tabData.accept(CreateSaModItems.ROSE_QUARTZ_SWORD.get());
				tabData.accept(CreateSaModItems.ZINC_HANDLE.get());
				tabData.accept(CreateSaModItems.BRASS_HELMET.get());
				tabData.accept(CreateSaModItems.BRASS_CHESTPLATE.get());
				tabData.accept(CreateSaModItems.BRASS_LEGGINGS.get());
				tabData.accept(CreateSaModItems.BRASS_BOOTS.get());
				tabData.accept(CreateSaModItems.BRASS_PICKAXE.get());
				tabData.accept(CreateSaModItems.BRASS_AXE.get());
				tabData.accept(CreateSaModItems.BRASS_SWORD.get());
				tabData.accept(CreateSaModItems.BRASS_SHOVEL.get());
				tabData.accept(CreateSaModItems.BRASS_HOE.get());
				tabData.accept(CreateSaModItems.ZINC_HELMET.get());
				tabData.accept(CreateSaModItems.ZINC_CHESTPLATE.get());
				tabData.accept(CreateSaModItems.ZINC_LEGGINGS.get());
				tabData.accept(CreateSaModItems.ZINC_BOOTS.get());
				tabData.accept(CreateSaModItems.ZINC_PICKAXE.get());
				tabData.accept(CreateSaModItems.ZINC_AXE.get());
				tabData.accept(CreateSaModItems.ZINC_SWORD.get());
				tabData.accept(CreateSaModItems.ZINC_SHOVEL.get());
				tabData.accept(CreateSaModItems.ZINC_HOE.get());
				tabData.accept(CreateSaModItems.COPPER_HELMET.get());
				tabData.accept(CreateSaModItems.COPPER_CHESTPLATE.get());
				tabData.accept(CreateSaModItems.COPPER_LEGGINGS.get());
				tabData.accept(CreateSaModItems.COPPER_BOOTS.get());
				tabData.accept(CreateSaModItems.COPPER_PICKAXE.get());
				tabData.accept(CreateSaModItems.COPPER_AXE.get());
				tabData.accept(CreateSaModItems.COPPER_SWORD.get());
				tabData.accept(CreateSaModItems.COPPER_SHOVEL.get());
				tabData.accept(CreateSaModItems.COPPER_HOE.get());
				tabData.accept(CreateSaModItems.SMALL_FUELING_TANK.get());
				tabData.accept(CreateSaModItems.MEDIUM_FUELING_TANK.get());
				tabData.accept(CreateSaModItems.LARGE_FUELING_TANK.get());
				tabData.accept(CreateSaModItems.FLAMETHROWER.get());
				tabData.accept(CreateSaModItems.EXPERIENCE_PICKAXE.get());
				tabData.accept(CreateSaModItems.EXPERIENCE_SHOVEL.get());
				tabData.accept(CreateSaModItems.EXPERIENCE_AXE.get());
				tabData.accept(CreateSaModItems.EXPERIENCE_SWORD.get());
				tabData.accept(CreateSaModItems.HEAP_OF_EXPERIENCE.get());
				tabData.accept(CreateSaModItems.SLIME_HELMET.get());
				tabData.accept(CreateSaModItems.SLIME_BOOTS.get());
				tabData.accept(CreateSaModItems.CREATIVE_FILLING_TANK.get());
				tabData.accept(CreateSaModItems.PORTABLE_DRILL.get());
				tabData.accept(CreateSaModItems.GRAPPLIN_WHISK.get());
				tabData.accept(CreateSaModItems.BLOCK_PICKER.get());
				tabData.accept(CreateSaModBlocks.BRASS_CUBE.get().asItem());
				tabData.accept(CreateSaModItems.NETHERITE_JETPACK_CHESTPLATE.get());
				tabData.accept(CreateSaModItems.NETHERITE_EXOSKELETON_CHESTPLATE.get());
			}).build());
}
