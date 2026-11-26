package net.furtitwo.createstuffadditions.client.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;

import net.furtitwo.createstuffadditions.world.inventory.VaultModuleGUIMenu;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class VaultModuleGUIScreen extends AbstractContainerScreen<VaultModuleGUIMenu> {
	private static final HashMap<String, Object> guistate = VaultModuleGUIMenu.guistate;
	private static final ResourceLocation TEXTURE = ResourceLocation.parse("create_sa:textures/screens/vault_module_gui.png");

	public VaultModuleGUIScreen(VaultModuleGUIMenu menu, Inventory inventory, Component text) {
		super(menu, inventory, text);
		this.imageWidth = 176;
		this.imageHeight = 132;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(graphics, mouseX, mouseY, partialTicks);
		super.render(graphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(graphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		RenderSystem.disableBlend();
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		return super.keyPressed(key, scanCode, modifiers);
	}

	@Override
	public void containerTick() {
		super.containerTick();
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		graphics.drawString(this.font, Component.translatable("gui.create_sa.vault_module_gui.label_inventory"), 8, 40, -12632257, false);
		graphics.drawString(this.font, Component.translatable("gui.create_sa.vault_module_gui.label_drone_module"), 8, 4, -15000805, false);
	}

	@Override
	public void onClose() {
		super.onClose();
	}

	@Override
	public void init() {
		super.init();
	}
}
