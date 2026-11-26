package net.furtitwo.createstuffadditions.logic.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import net.minecraft.world.level.LevelAccessor;

import net.furtitwo.createstuffadditions.configuration.CreateSaConfigConfiguration;
import net.furtitwo.createstuffadditions.network.CreateSaModVariables.MapVariables;

/** Copies the server config into the synced map variables whenever a player logs in, then sends them to every player. */
@EventBusSubscriber
public final class ConfigSync {
	private ConfigSync() {
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		LevelAccessor level = event.getEntity().level();
		MapVariables vars = MapVariables.get(level);
		vars.brassJetpackMaxHeight = (double) CreateSaConfigConfiguration.BRASSJETPACKMAXHEIGHT.get();
		vars.andesiteJetpackMaxHeight = (double) CreateSaConfigConfiguration.ANDESITEJETPACKMAXHEIGHT.get();
		vars.copperJetpackMaxHeight = (double) CreateSaConfigConfiguration.COPPERJETPACKMAXHEIGHT.get();
		vars.netheriteJetpackMaxHeight = (double) CreateSaConfigConfiguration.NETHERITEJETPACKMAXHEIGHT.get();
		vars.enableAboveCloudEnchant = CreateSaConfigConfiguration.ENABLEABOVECLOUDENCHANT.get();
		vars.brassJetpackSpeed = (double) CreateSaConfigConfiguration.BRASSJETPACKSPEED.get();
		vars.andesiteJetpackSpeed = (double) CreateSaConfigConfiguration.ANDESITEJETPACKSPEED.get();
		vars.copperJetpackSpeed = (double) CreateSaConfigConfiguration.COPPERJETPACKSPEED.get();
		vars.netheriteJetpackSpeed = (double) CreateSaConfigConfiguration.NETHERITEJETPACKSPEED.get();
		vars.grapplinWhiskSpeed = (double) CreateSaConfigConfiguration.GRAPPLINWHISKSPEED.get();
		vars.gadgetCapacity = (double) CreateSaConfigConfiguration.GADGETCAPACITY.get();
		vars.smallTankCapacity = (double) CreateSaConfigConfiguration.SMALLTANKCAPACITY.get();
		vars.mediumTankCapacity = (double) CreateSaConfigConfiguration.MEDIUMTANKCAPACITY.get();
		vars.largeTankCapacity = (double) CreateSaConfigConfiguration.LARGETANKCAPACITY.get();
		vars.enableDiggingEnchant = CreateSaConfigConfiguration.ENABLEDIGGINGENCHANT.get();
		vars.enableImpactEnchant = CreateSaConfigConfiguration.ENABLEIMPACTENCHANT.get();
		vars.enableGravityGunEnchant = CreateSaConfigConfiguration.ENABLEGRAVITYGUNENCHANT.get();
		vars.enableHellfireEnchant = CreateSaConfigConfiguration.ENABLEHELLFIREENCHANT.get();
		vars.enableChunkEaterEnchant = CreateSaConfigConfiguration.ENABLECHUNKEATERENCHANT.get();
		vars.enableDrillDroneModule = CreateSaConfigConfiguration.ENABLEDRILLDRONEMODULE.get();
		vars.enableFanDroneModule = CreateSaConfigConfiguration.ENABLEFANDRONEMODULE.get();
		vars.enableMagnetDroneModule = CreateSaConfigConfiguration.ENABLEMAGNETDRONEMODULE.get();
		vars.enableVaultDroneModule = CreateSaConfigConfiguration.ENABLEVAULTDRONEMODULE.get();
		vars.MagnetModuleBlockLimitation = CreateSaConfigConfiguration.MAGNETMODULEBLOCKLIMITATION.get();
		vars.syncData(level);
	}
}
