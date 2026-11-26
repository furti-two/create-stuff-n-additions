package net.furtitwo.createstuffadditions.network;

import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;

import net.furtitwo.createstuffadditions.CreateSaMod;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class CreateSaModVariables {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, CreateSaMod.MODID);
	public static double db_y = 0;
	public static double db_z = 0;
	public static double db_x = 0;

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		CreateSaMod.addNetworkMessage(SavedDataSyncMessage.TYPE, SavedDataSyncMessage.STREAM_CODEC, SavedDataSyncMessage::handleData);
	}

	@EventBusSubscriber
	public static class EventBusVariableHandlers {
		@SubscribeEvent
		public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
			if (event.getEntity() instanceof ServerPlayer player) {
				SavedData mapdata = MapVariables.get(event.getEntity().level());
				SavedData worlddata = WorldVariables.get(event.getEntity().level());
				if (mapdata != null)
					PacketDistributor.sendToPlayer(player, new SavedDataSyncMessage(0, mapdata));
				if (worlddata != null)
					PacketDistributor.sendToPlayer(player, new SavedDataSyncMessage(1, worlddata));
			}
		}

		@SubscribeEvent
		public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (event.getEntity() instanceof ServerPlayer player) {
				SavedData worlddata = WorldVariables.get(event.getEntity().level());
				if (worlddata != null)
					PacketDistributor.sendToPlayer(player, new SavedDataSyncMessage(1, worlddata));
			}
		}
	}

	public static class WorldVariables extends SavedData {
		public static final String DATA_NAME = "create_sa_worldvars";

		public static WorldVariables load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
			WorldVariables data = new WorldVariables();
			data.read(tag, lookupProvider);
			return data;
		}

		public void read(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
		}

		@Override
		public CompoundTag save(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
			return nbt;
		}

		public void syncData(LevelAccessor world) {
			this.setDirty();
			if (world instanceof ServerLevel level)
				PacketDistributor.sendToPlayersInDimension(level, new SavedDataSyncMessage(1, this));
		}

		static WorldVariables clientSide = new WorldVariables();

		public static WorldVariables get(LevelAccessor world) {
			if (world instanceof ServerLevel level) {
				return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(WorldVariables::new, WorldVariables::load), DATA_NAME);
			} else {
				return clientSide;
			}
		}
	}

	public static class MapVariables extends SavedData {
		public static final String DATA_NAME = "create_sa_mapvars";
		public boolean CsaFlying = false;
		public double brassJetpackMaxHeight = 28.0;
		public double andesiteJetpackMaxHeight = 18.0;
		public double copperJetpackMaxHeight = 18.0;
		public double netheriteJetpackMaxHeight = 28.0;
		public boolean enableAboveCloudEnchant = false;
		public double brassJetpackSpeed = 0.06;
		public double andesiteJetpackSpeed = 0.08;
		public double copperJetpackSpeed = 0.02;
		public double netheriteJetpackSpeed = 0.08;
		public double grapplinWhiskSpeed = 0.2;
		public double gadgetCapacity = 1600.0;
		public double smallTankCapacity = 800.0;
		public double mediumTankCapacity = 1600.0;
		public double largeTankCapacity = 3200.0;
		public boolean enableDiggingEnchant = true;
		public boolean enableImpactEnchant = true;
		public boolean enableGravityGunEnchant = true;
		public boolean enableHellfireEnchant = true;
		public boolean enableDrillDroneModule = true;
		public boolean enableFanDroneModule = true;
		public boolean enableMagnetDroneModule = true;
		public boolean enableVaultDroneModule = true;
		public boolean MagnetModuleBlockLimitation = false;
		public boolean enableChunkEaterEnchant = false;

		public static MapVariables load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
			MapVariables data = new MapVariables();
			data.read(tag, lookupProvider);
			return data;
		}

		public void read(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
			CsaFlying = nbt.getBoolean("CsaFlying");
			brassJetpackMaxHeight = nbt.getDouble("brassJetpackMaxHeight");
			andesiteJetpackMaxHeight = nbt.getDouble("andesiteJetpackMaxHeight");
			copperJetpackMaxHeight = nbt.getDouble("copperJetpackMaxHeight");
			netheriteJetpackMaxHeight = nbt.getDouble("netheriteJetpackMaxHeight");
			enableAboveCloudEnchant = nbt.getBoolean("enableAboveCloudEnchant");
			brassJetpackSpeed = nbt.getDouble("brassJetpackSpeed");
			andesiteJetpackSpeed = nbt.getDouble("andesiteJetpackSpeed");
			copperJetpackSpeed = nbt.getDouble("copperJetpackSpeed");
			netheriteJetpackSpeed = nbt.getDouble("netheriteJetpackSpeed");
			grapplinWhiskSpeed = nbt.getDouble("grapplinWhiskSpeed");
			gadgetCapacity = nbt.getDouble("gadgetCapacity");
			smallTankCapacity = nbt.getDouble("smallTankCapacity");
			mediumTankCapacity = nbt.getDouble("mediumTankCapacity");
			largeTankCapacity = nbt.getDouble("largeTankCapacity");
			enableDiggingEnchant = nbt.getBoolean("enableDiggingEnchant");
			enableImpactEnchant = nbt.getBoolean("enableImpactEnchant");
			enableGravityGunEnchant = nbt.getBoolean("enableGravityGunEnchant");
			enableHellfireEnchant = nbt.getBoolean("enableHellfireEnchant");
			enableDrillDroneModule = nbt.getBoolean("enableDrillDroneModule");
			enableFanDroneModule = nbt.getBoolean("enableFanDroneModule");
			enableMagnetDroneModule = nbt.getBoolean("enableMagnetDroneModule");
			enableVaultDroneModule = nbt.getBoolean("enableVaultDroneModule");
			MagnetModuleBlockLimitation = nbt.getBoolean("MagnetModuleBlockLimitation");
			enableChunkEaterEnchant = nbt.getBoolean("enableChunkEaterEnchant");
		}

		@Override
		public CompoundTag save(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
			nbt.putBoolean("CsaFlying", CsaFlying);
			nbt.putDouble("brassJetpackMaxHeight", brassJetpackMaxHeight);
			nbt.putDouble("andesiteJetpackMaxHeight", andesiteJetpackMaxHeight);
			nbt.putDouble("copperJetpackMaxHeight", copperJetpackMaxHeight);
			nbt.putDouble("netheriteJetpackMaxHeight", netheriteJetpackMaxHeight);
			nbt.putBoolean("enableAboveCloudEnchant", enableAboveCloudEnchant);
			nbt.putDouble("brassJetpackSpeed", brassJetpackSpeed);
			nbt.putDouble("andesiteJetpackSpeed", andesiteJetpackSpeed);
			nbt.putDouble("copperJetpackSpeed", copperJetpackSpeed);
			nbt.putDouble("netheriteJetpackSpeed", netheriteJetpackSpeed);
			nbt.putDouble("grapplinWhiskSpeed", grapplinWhiskSpeed);
			nbt.putDouble("gadgetCapacity", gadgetCapacity);
			nbt.putDouble("smallTankCapacity", smallTankCapacity);
			nbt.putDouble("mediumTankCapacity", mediumTankCapacity);
			nbt.putDouble("largeTankCapacity", largeTankCapacity);
			nbt.putBoolean("enableDiggingEnchant", enableDiggingEnchant);
			nbt.putBoolean("enableImpactEnchant", enableImpactEnchant);
			nbt.putBoolean("enableGravityGunEnchant", enableGravityGunEnchant);
			nbt.putBoolean("enableHellfireEnchant", enableHellfireEnchant);
			nbt.putBoolean("enableDrillDroneModule", enableDrillDroneModule);
			nbt.putBoolean("enableFanDroneModule", enableFanDroneModule);
			nbt.putBoolean("enableMagnetDroneModule", enableMagnetDroneModule);
			nbt.putBoolean("enableVaultDroneModule", enableVaultDroneModule);
			nbt.putBoolean("MagnetModuleBlockLimitation", MagnetModuleBlockLimitation);
			nbt.putBoolean("enableChunkEaterEnchant", enableChunkEaterEnchant);
			return nbt;
		}

		public void syncData(LevelAccessor world) {
			this.setDirty();
			if (world instanceof Level && !world.isClientSide())
				PacketDistributor.sendToAllPlayers(new SavedDataSyncMessage(0, this));
		}

		static MapVariables clientSide = new MapVariables();

		public static MapVariables get(LevelAccessor world) {
			if (world instanceof ServerLevelAccessor serverLevelAcc) {
				return serverLevelAcc.getLevel().getServer().getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(new SavedData.Factory<>(MapVariables::new, MapVariables::load), DATA_NAME);
			} else {
				return clientSide;
			}
		}
	}

	public record SavedDataSyncMessage(int dataType, SavedData data) implements CustomPacketPayload {
		public static final Type<SavedDataSyncMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CreateSaMod.MODID, "saved_data_sync"));
		public static final StreamCodec<RegistryFriendlyByteBuf, SavedDataSyncMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, SavedDataSyncMessage message) -> {
			buffer.writeInt(message.dataType);
			if (message.data != null)
				buffer.writeNbt(message.data.save(new CompoundTag(), buffer.registryAccess()));
		}, (RegistryFriendlyByteBuf buffer) -> {
			int dataType = buffer.readInt();
			CompoundTag nbt = buffer.readNbt();
			SavedData data = null;
			if (nbt != null) {
				data = dataType == 0 ? new MapVariables() : new WorldVariables();
				if (data instanceof MapVariables mapVariables)
					mapVariables.read(nbt, buffer.registryAccess());
				else if (data instanceof WorldVariables worldVariables)
					worldVariables.read(nbt, buffer.registryAccess());
			}
			return new SavedDataSyncMessage(dataType, data);
		});

		@Override
		public Type<SavedDataSyncMessage> type() {
			return TYPE;
		}

		public static void handleData(final SavedDataSyncMessage message, final IPayloadContext context) {
			if (context.flow() == PacketFlow.CLIENTBOUND && message.data != null) {
				context.enqueueWork(() -> {
					if (message.dataType == 0)
						MapVariables.clientSide.read(message.data.save(new CompoundTag(), context.player().registryAccess()), context.player().registryAccess());
					else
						WorldVariables.clientSide.read(message.data.save(new CompoundTag(), context.player().registryAccess()), context.player().registryAccess());
				}).exceptionally(e -> {
					context.connection().disconnect(Component.literal(e.getMessage()));
					return null;
				});
			}
		}
	}
}
