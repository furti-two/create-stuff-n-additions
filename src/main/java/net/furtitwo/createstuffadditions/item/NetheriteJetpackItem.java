package net.furtitwo.createstuffadditions.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import net.furtitwo.createstuffadditions.equipment.CSAArmorMaterials;
import net.furtitwo.createstuffadditions.equipment.JetpackItem;
import net.furtitwo.createstuffadditions.fuel.FuelType;

/** Steam jetpack built for the Nether: boosted and able to glide in lava, fire resistant. */
public class NetheriteJetpackItem extends JetpackItem {
	public static final Stats STATS = new Stats(v -> v.netheriteJetpackSpeed, v -> v.netheriteJetpackMaxHeight, 30, 5, 20, 2, 3, 0.1, 0.3, 0.15, 0.1, ResourceLocation.parse("create_sa:steam_noise"), 0.1f, 2f);
	private static final ResourceLocation TEXTURE = ResourceLocation.parse("create_sa:textures/entities/netherite_jetpack.png");

	public NetheriteJetpackItem() {
		super(CSAArmorMaterials.NETHERITE_JETPACK, new Item.Properties().fireResistant(), FuelType.STEAM, "brass_jetpack", true, STATS); // shares the brass jetpack description text
	}

	@Override
	protected double glideHorizontalFactor(Player player) {
		return player.isInLava() ? 1.5 : 0.6;
	}

	@Override
	protected double glideVerticalFactor(Player player) {
		return player.isInLava() ? 0.25 : 0.1;
	}

	@Override
	protected void sprint(Player player, double speed) {
		Vec3 look = player.getLookAngle();
		double boost = player.isShiftKeyDown() ? speed * 1.25 : speed;
		Vec3 v = player.getDeltaMovement().scale(1.001);
		player.setDeltaMovement(v.x + look.x * boost, v.y, v.z + look.z * boost);
	}

	@Override
	public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
		return entity.isInLava() && hasFuel(stack);
	}

	@Override
	public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
		if (!entity.level().isClientSide() && (flightTicks + 1) % 10 == 0)
			entity.gameEvent(GameEvent.ELYTRA_GLIDE);
		return true;
	}

	@Override
	public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
		return TEXTURE;
	}
}
