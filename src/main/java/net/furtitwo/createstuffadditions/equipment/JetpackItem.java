package net.furtitwo.createstuffadditions.equipment;

import net.furtitwo.createstuffadditions.compat.sable.SubLevels;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import net.furtitwo.createstuffadditions.fuel.FuelStorage;
import net.furtitwo.createstuffadditions.fuel.FuelType;
import net.furtitwo.createstuffadditions.init.CreateSaModParticleTypes;
import net.furtitwo.createstuffadditions.network.CreateSaModVariables.MapVariables;
import net.furtitwo.createstuffadditions.util.TooltipLines;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.ToDoubleFunction;

/**
 * Jetpack chestplate. One flight model for every tier; tiers only change {@link Stats} and, when needed, override a hook.
 * Flight is toggled by the SPACE key, stored as {@code CsaFlying} in the player's persistent data on both sides.
 */
public abstract class JetpackItem extends FuelableChestplateItem {
	public static final String FLYING_KEY = "CsaFlying";
	private static final ResourceKey<Enchantment> ABOVE_THE_CLOUDS = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("create_sa:above_the_clouds"));

	/**
	 * Per-tier constants. Intervals are in ticks between two fuel drains for each flight mode.
	 * Glide caps stop adding speed past that velocity on the x/z and y axes.
	 */
	public record Stats(ToDoubleFunction<MapVariables> speed, ToDoubleFunction<MapVariables> maxHeight, int swimInterval, int glideInterval, int hoverInterval, double glideCapHorizontal,
			double glideCapVertical, double glideLookY, double hoverLiftLimit, double hoverLift, double fallBrake, ResourceLocation sound, float soundVolume, float soundPitch) {
	}

	protected final Stats stats;

	protected JetpackItem(Holder<ArmorMaterial> material, Item.Properties properties, FuelType fuelType, String descKey, boolean piglinNeutral, Stats stats) {
		super(material, properties, fuelType, descKey, piglinNeutral);
		this.stats = stats;
	}

	public static boolean wantsToFly(Player player) {
		return player.getPersistentData().getBoolean(FLYING_KEY);
	}

	@Override
	protected void tickWorn(ItemStack stack, Level level, Player player) {
		if (player.isSpectator())
			return;
		boolean fueled = hasFuel(stack);
		boolean flying = fueled && wantsToFly(player);
		FuelStorage.setFlying(stack, flying);
		if (!fueled)
			return;
		if (flying) {
			MapVariables vars = MapVariables.get(level);
			double speed = stats.speed().applyAsDouble(vars);
			int interval;
			if (player.isSwimming()) {
				swim(level, player, speed);
				interval = stats.swimInterval();
			} else if (player.isFallFlying()) {
				glide(level, player, speed);
				interval = stats.glideInterval();
			} else {
				hover(stack, level, player, vars, speed);
				interval = stats.hoverInterval();
			}
			if (isWorkTick(player, interval))
				work(stack, level, player, stats.sound(), stats.soundVolume(), stats.soundPitch());
		}
		onFueledTick(stack, level, player, flying);
	}

	/** Extra per-tick behaviour while the jetpack has fuel (after flight was applied). */
	protected void onFueledTick(ItemStack stack, Level level, Player player, boolean flying) {
	}


	protected void swim(Level level, Player player, double speed) {
		if (level instanceof ServerLevel server)
			swimParticles(server, player);
		Vec3 look = player.getLookAngle();
		player.setDeltaMovement(player.getDeltaMovement().add(look.scale(speed * 1.3)));
	}

	protected void glide(Level level, Player player, double speed) {
		if (level instanceof ServerLevel server)
			glideParticles(server, player);
		Vec3 look = player.getLookAngle();
		Vec3 v = player.getDeltaMovement();
		double x = v.x, y = v.y, z = v.z;
		double horizontal = glideHorizontalFactor(player);
		if (Math.abs(x) < stats.glideCapHorizontal())
			x += look.x * speed * horizontal;
		if (Math.abs(y) < stats.glideCapVertical())
			y += look.y * glideVerticalFactor(player);
		if (Math.abs(z) < stats.glideCapHorizontal())
			z += look.z * speed * horizontal;
		player.setDeltaMovement(x, y, z);
	}

	protected double glideHorizontalFactor(Player player) {
		return 0.6;
	}

	protected double glideVerticalFactor(Player player) {
		return stats.glideLookY();
	}

	protected void hover(ItemStack stack, Level level, Player player, MapVariables vars, double speed) {
		boolean grounded = ignoresHeightLimit(stack, level, vars) || isWithinHeight(level, player, (int) stats.maxHeight().applyAsDouble(vars));
		player.fallDistance = 0;
		Vec3 v = player.getDeltaMovement();
		double y = v.y;
		if (grounded) {
			if (player.isShiftKeyDown()) {
				if (y > -0.15 && y < 0.15)
					y = 0;
				else
					y += y > 0 ? -0.1 : 0.1;
			} else if (y <= stats.hoverLiftLimit()) {
				y += stats.hoverLift();
			}
		} else if (y <= -0.2) {
			y += stats.fallBrake();
		}
		player.setDeltaMovement(v.x, y, v.z);
		if (player.isSprinting())
			sprint(player, speed);
		if (level instanceof ServerLevel server)
			hoverParticles(server, player, player.isInWater());
	}

	protected void sprint(Player player, double speed) {
		Vec3 look = player.getLookAngle();
		player.setDeltaMovement(player.getDeltaMovement().add(look.x * speed, 0, look.z * speed));
	}

	private boolean ignoresHeightLimit(ItemStack stack, Level level, MapVariables vars) {
		if (!vars.enableAboveCloudEnchant)
			return false;
		return level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(ABOVE_THE_CLOUDS).map(stack::getEnchantmentLevel).orElse(0) != 0;
	}

	/** True when a block lies at most {@code maxHeight} blocks below the player, in the world or on a Sable structure. */
	private static boolean isWithinHeight(Level level, Player player, int maxHeight) {
		for (int depth = 1; depth <= maxHeight; depth++) {
			if (SubLevels.hasBlockAt(level, new Vec3(player.getX(), player.getY() - depth, player.getZ())))
				return true;
		}
		return false;
	}

    protected ParticleOptions trailParticle() {
		return (ParticleOptions) CreateSaModParticleTypes.STEAM_CLOUD.get();
	}

	protected void swimParticles(ServerLevel level, Player player) {
		sidePair(level, player, ParticleTypes.BUBBLE_COLUMN_UP);
	}

	protected void glideParticles(ServerLevel level, Player player) {
		sidePair(level, player, trailParticle());
	}

	protected void hoverParticles(ServerLevel level, Player player, boolean inWater) {
		ParticleOptions particle = inWater ? ParticleTypes.BUBBLE_COLUMN_UP : trailParticle();
		backPoint(level, player, particle, 0.4, 40, 1, 0, 0);
		backPoint(level, player, particle, 0.4, -40, 1, 0, 0);
	}

	/** Two particles left and right of the player, used while swimming or gliding. */
	protected static void sidePair(ServerLevel level, Player player, ParticleOptions particle) {
		double yaw = Math.toRadians(player.getYRot());
		double dx = 0.3 * Math.cos(yaw), dz = 0.3 * Math.sin(yaw);
		level.sendParticles(particle, player.getX() + dx, player.getY() + 0.6, player.getZ() + dz, 1, 0, 0, 0, 0);
		level.sendParticles(particle, player.getX() - dx, player.getY() + 0.6, player.getZ() - dz, 1, 0, 0, 0, 0);
	}

	/** A particle behind the player's body, {@code angle} degrees off the back direction. */
	protected static void backPoint(ServerLevel level, Player player, ParticleOptions particle, double radius, double angle, int count, double spreadXZ, double spreadY) {
		double a = -(player.yBodyRot + angle) * Math.PI / 180;
		level.sendParticles(particle, player.getX() - radius * Math.sin(a), player.getY() + player.getBbHeight()*0.4, player.getZ() - radius * Math.cos(a), count, spreadXZ, spreadY, spreadXZ, 0);
	}

	@Override
	protected boolean hasControlSection() {
		return true;
	}

	@Override
	protected void appendControlLines(@Nullable Level level, List<Component> list) {
		long height = level == null ? 0 : Math.round(stats.maxHeight().applyAsDouble(MapVariables.get(level)));
		list.add(Component.translatable("item_desc.jetpack.spaceinair"));
		list.addAll(TooltipLines.wrap(Component.translatable("item_desc.jetpack.desc2").getString().replace("%", String.valueOf(height)), 1));
		list.add(Component.translatable("item_desc.jetpack.spacesneak"));
		list.addAll(wrap("item_desc.jetpack.desc3", 1));
		list.add(Component.translatable("item_desc.jetpack.spacewater"));
		list.addAll(wrap("item_desc.jetpack.desc4", 1));
	}
}
