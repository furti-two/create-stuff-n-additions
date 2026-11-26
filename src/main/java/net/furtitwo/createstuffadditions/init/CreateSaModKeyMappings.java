
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.furtitwo.createstuffadditions.init;

import org.lwjgl.glfw.GLFW;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;

import net.furtitwo.createstuffadditions.network.IncreaseReachMessage;
import net.furtitwo.createstuffadditions.network.FlyingMessage;
import net.furtitwo.createstuffadditions.network.DecreaseReachMessage;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class CreateSaModKeyMappings {
	public static final KeyMapping FLYING = new KeyMapping("key.create_sa.flying", GLFW.GLFW_KEY_SPACE, "key.categories.movement") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				PacketDistributor.sendToServer(new FlyingMessage(0, 0));
				FlyingMessage.pressAction(Minecraft.getInstance().player, 0, 0);
				FLYING_LASTPRESS = System.currentTimeMillis();
			} else if (isDownOld != isDown && !isDown) {
				int dt = (int) (System.currentTimeMillis() - FLYING_LASTPRESS);
				PacketDistributor.sendToServer(new FlyingMessage(1, dt));
				FlyingMessage.pressAction(Minecraft.getInstance().player, 1, dt);
			}
			isDownOld = isDown;
		}
	};
	public static final KeyMapping INCREASE_REACH = new KeyMapping("key.create_sa.increase_reach", GLFW.GLFW_KEY_UP, "key.categories.gameplay") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				PacketDistributor.sendToServer(new IncreaseReachMessage(0, 0));
				IncreaseReachMessage.pressAction(Minecraft.getInstance().player, 0, 0);
			}
			isDownOld = isDown;
		}
	};
	public static final KeyMapping DECREASE_REACH = new KeyMapping("key.create_sa.decrease_reach", GLFW.GLFW_KEY_DOWN, "key.categories.gameplay") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				PacketDistributor.sendToServer(new DecreaseReachMessage(0, 0));
				DecreaseReachMessage.pressAction(Minecraft.getInstance().player, 0, 0);
			}
			isDownOld = isDown;
		}
	};
	private static long FLYING_LASTPRESS = 0;

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(FLYING);
		event.register(INCREASE_REACH);
		event.register(DECREASE_REACH);
	}

	@EventBusSubscriber({Dist.CLIENT})
	public static class KeyEventListener {
		@SubscribeEvent
		public static void onClientTick(ClientTickEvent.Post event) {
			if (Minecraft.getInstance().screen == null) {
				FLYING.consumeClick();
				INCREASE_REACH.consumeClick();
				DECREASE_REACH.consumeClick();
			}
		}
	}
}
