package net.furtitwo.createstuffadditions.configuration;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CreateSaConfigConfiguration {
	public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
	public static final ModConfigSpec SPEC;

	public static final ModConfigSpec.ConfigValue<Double> BRASSJETPACKMAXHEIGHT;
	public static final ModConfigSpec.ConfigValue<Double> ANDESITEJETPACKMAXHEIGHT;
	public static final ModConfigSpec.ConfigValue<Double> COPPERJETPACKMAXHEIGHT;
	public static final ModConfigSpec.ConfigValue<Double> NETHERITEJETPACKMAXHEIGHT;
	public static final ModConfigSpec.ConfigValue<Boolean> ENABLEABOVECLOUDENCHANT;
	public static final ModConfigSpec.ConfigValue<Double> BRASSJETPACKSPEED;
	public static final ModConfigSpec.ConfigValue<Double> ANDESITEJETPACKSPEED;
	public static final ModConfigSpec.ConfigValue<Double> COPPERJETPACKSPEED;
	public static final ModConfigSpec.ConfigValue<Double> NETHERITEJETPACKSPEED;
	public static final ModConfigSpec.ConfigValue<Double> GRAPPLINWHISKSPEED;
	public static final ModConfigSpec.ConfigValue<Double> GADGETCAPACITY;
	public static final ModConfigSpec.ConfigValue<Double> SMALLTANKCAPACITY;
	public static final ModConfigSpec.ConfigValue<Double> MEDIUMTANKCAPACITY;
	public static final ModConfigSpec.ConfigValue<Double> LARGETANKCAPACITY;
	public static final ModConfigSpec.ConfigValue<Boolean> ENABLEDIGGINGENCHANT;
	public static final ModConfigSpec.ConfigValue<Boolean> ENABLEIMPACTENCHANT;
	public static final ModConfigSpec.ConfigValue<Boolean> ENABLEGRAVITYGUNENCHANT;
	public static final ModConfigSpec.ConfigValue<Boolean> ENABLEHELLFIREENCHANT;
	public static final ModConfigSpec.ConfigValue<Boolean> ENABLECHUNKEATERENCHANT;
	public static final ModConfigSpec.ConfigValue<Boolean> ENABLEDRILLDRONEMODULE;
	public static final ModConfigSpec.ConfigValue<Boolean> ENABLEFANDRONEMODULE;
	public static final ModConfigSpec.ConfigValue<Boolean> ENABLEMAGNETDRONEMODULE;
	public static final ModConfigSpec.ConfigValue<Boolean> ENABLEVAULTDRONEMODULE;
	public static final ModConfigSpec.ConfigValue<Boolean> MAGNETMODULEBLOCKLIMITATION;
	static {
		BUILDER.push("Jetpack Height Restriction");
		BRASSJETPACKMAXHEIGHT = BUILDER.define("brassJetpackMaxHeight", (double) 28);
		ANDESITEJETPACKMAXHEIGHT = BUILDER.define("andesiteJetpackMaxHeight", (double) 18);
		COPPERJETPACKMAXHEIGHT = BUILDER.define("copperJetpackMaxHeight", (double) 18);
		NETHERITEJETPACKMAXHEIGHT = BUILDER.define("netheriteJetpackMaxHeight", (double) 28);
		ENABLEABOVECLOUDENCHANT = BUILDER.define("enableAboveCloudEnchant", false);
		BUILDER.pop();
		BUILDER.push("Jetpack/Grapplin Speed");
		BRASSJETPACKSPEED = BUILDER.define("brassJetpackSpeed", (double) 0.06);
		ANDESITEJETPACKSPEED = BUILDER.define("andesiteJetpackSpeed", (double) 0.08);
		COPPERJETPACKSPEED = BUILDER.define("copperJetpackSpeed", (double) 0.02);
		NETHERITEJETPACKSPEED = BUILDER.define("netheriteJetpackSpeed", (double) 0.08);
		GRAPPLINWHISKSPEED = BUILDER.define("grapplinWhiskSpeed", (double) 0.2);
		BUILDER.pop();
		BUILDER.push("Fuel/Water Capacity");
		GADGETCAPACITY = BUILDER.define("gadgetCapacity", (double) 1600);
		SMALLTANKCAPACITY = BUILDER.define("smallTankCapacity", (double) 800);
		MEDIUMTANKCAPACITY = BUILDER.define("mediumTankCapacity", (double) 1600);
		LARGETANKCAPACITY = BUILDER.define("largeTankCapacity", (double) 3200);
		BUILDER.pop();
		BUILDER.push("Enchantments");
		ENABLEDIGGINGENCHANT = BUILDER.define("enableDiggingEnchant", true);
		ENABLEIMPACTENCHANT = BUILDER.define("enableImpactEnchant", true);
		ENABLEGRAVITYGUNENCHANT = BUILDER.define("enableGravityGunEnchant", true);
		ENABLEHELLFIREENCHANT = BUILDER.define("enableHellfireEnchant", true);
		ENABLECHUNKEATERENCHANT = BUILDER.define("enableChunkEaterEnchant", true);
		BUILDER.pop();
		BUILDER.push("Drone Module");
		ENABLEDRILLDRONEMODULE = BUILDER.define("enableDrillDroneModule", true);
		ENABLEFANDRONEMODULE = BUILDER.define("enableFanDroneModule", true);
		ENABLEMAGNETDRONEMODULE = BUILDER.define("enableMagnetDroneModule", true);
		ENABLEVAULTDRONEMODULE = BUILDER.define("enableVaultDroneModule", true);
		MAGNETMODULEBLOCKLIMITATION = BUILDER.define("MagnetModuleBlockLimitation", false);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

}
