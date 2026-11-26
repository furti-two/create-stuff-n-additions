
package net.furtitwo.createstuffadditions.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;

import net.furtitwo.createstuffadditions.logic.control.KeyActions;
import net.furtitwo.createstuffadditions.CreateSaMod;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record IncreaseReachMessage(int eventType, int pressedms) implements CustomPacketPayload {
	public static final Type<IncreaseReachMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CreateSaMod.MODID, "key_increase_reach"));
	public static final StreamCodec<RegistryFriendlyByteBuf, IncreaseReachMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, IncreaseReachMessage message) -> {
		buffer.writeInt(message.eventType);
		buffer.writeInt(message.pressedms);
	}, (RegistryFriendlyByteBuf buffer) -> new IncreaseReachMessage(buffer.readInt(), buffer.readInt()));

	@Override
	public Type<IncreaseReachMessage> type() {
		return TYPE;
	}

	public static void handleData(final IncreaseReachMessage message, final IPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND) {
			context.enqueueWork(() -> {
				pressAction(context.player(), message.eventType, message.pressedms);
			}).exceptionally(e -> {
				context.connection().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	public static void pressAction(Player entity, int type, int pressedms) {
		Level world = entity.level();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		// security measure to prevent arbitrary chunk generation
		if (!world.hasChunkAt(entity.blockPosition()))
			return;
		if (type == 0) {

			KeyActions.onReachKey(entity, true);
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		CreateSaMod.addNetworkMessage(IncreaseReachMessage.TYPE, IncreaseReachMessage.STREAM_CODEC, IncreaseReachMessage::handleData);
	}
}
