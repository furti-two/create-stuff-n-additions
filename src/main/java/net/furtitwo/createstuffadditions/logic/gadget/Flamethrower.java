package net.furtitwo.createstuffadditions.logic.gadget;

import net.neoforged.neoforge.fluids.FluidStack;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

import net.furtitwo.createstuffadditions.entity.FlamethrowerPr2Entity;
import net.furtitwo.createstuffadditions.entity.FlamethrowerPrEntity;
import net.furtitwo.createstuffadditions.fuel.FuelStorage;
import net.furtitwo.createstuffadditions.init.CreateSaModEntities;
import net.furtitwo.createstuffadditions.item.CustomFluidHandlerItemStack;

/**
 * Flamethrower: right-click toggles it; while active and held it fires a flame projectile every tick, burning lava.
 * With Hellfire it fires soul flames (wider spread, 100 mB per tick instead of 10).
 */
public final class Flamethrower {
	private static final String ACTIVE = "tagActivated";
	private static final ResourceKey<Enchantment> HELLFIRE = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("create_sa:hellfire"));
	private static final float DAMAGE = 4;
	private static final byte PIERCE = 2;
	private static final float SPEED = 1.2f;

	private Flamethrower() {
	}

	/** Right-click: toggles the flamethrower and swings the hand holding it. */
	public static void onUse(LivingEntity user, ItemStack stack) {
		boolean active = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean(ACTIVE);
		setActive(stack, !active);
		user.swing(user.getMainHandItem().getItem() == stack.getItem() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, true);
	}

	/** Every tick while selected: fires when active and fuelled, switches off when empty. */
	public static void onHeldTick(Level level, Entity holder, ItemStack stack) {
		if (!stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean(ACTIVE))
			return;
		if (FuelStorage.amount(stack, 0) <= 0) {
			setActive(stack, false);
			return;
		}
		boolean hellfire = stack.getEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(HELLFIRE)) != 0;
		if (!level.isClientSide())
			shoot(level, holder, hellfire ? soulFlame(level) : flame(level), hellfire ? 4 : 2);
		CustomFluidHandlerItemStack.drainTank(stack, new FluidStack(Fluids.LAVA, hellfire ? 100 : 10), false);
		if (!level.isClientSide())
			GadgetSounds.play(level, holder.getX(), holder.getY(), holder.getZ(), SoundEvents.BLAZE_SHOOT, 0.2f, (float) (Mth.nextInt(RandomSource.create(), 15, 25) * 0.1));
	}

	private static void shoot(Level level, Entity shooter, AbstractArrow projectile, float inaccuracy) {
		projectile.setOwner(shooter);
		projectile.setBaseDamage(DAMAGE);
		projectile.setSilent(true);
		projectile.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
		Vec3 look = shooter.getLookAngle();
		projectile.shoot(look.x, look.y, look.z, SPEED, inaccuracy);
		level.addFreshEntity(projectile);
	}

	/** Flame with a pierce level of 2 and no knockback. */
	private static AbstractArrow flame(Level level) {
		return new FlamethrowerPrEntity(CreateSaModEntities.FLAMETHROWER_PR.get(), level) {
			@Override
			public byte getPierceLevel() {
				return PIERCE;
			}

			@Override
			protected void doKnockback(LivingEntity target, DamageSource source) {
			}
		};
	}

	/** Soul flame with a pierce level of 2 and no knockback. */
	private static AbstractArrow soulFlame(Level level) {
		return new FlamethrowerPr2Entity(CreateSaModEntities.FLAMETHROWER_PR_2.get(), level) {
			@Override
			public byte getPierceLevel() {
				return PIERCE;
			}

			@Override
			protected void doKnockback(LivingEntity target, DamageSource source) {
			}
		};
	}

	private static void setActive(ItemStack stack, boolean active) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean(ACTIVE, active));
	}
}
