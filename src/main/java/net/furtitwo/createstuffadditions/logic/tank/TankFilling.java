package net.furtitwo.createstuffadditions.logic.tank;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.furtitwo.createstuffadditions.fuel.FuelStorage;
import net.furtitwo.createstuffadditions.fuel.FuelType;
import net.furtitwo.createstuffadditions.item.CustomFluidHandlerItemStack;

import org.jetbrains.annotations.Nullable;

/**
 * Fluid transfers done by the tank items. Filling tanks carry water, fueling tanks carry lava:
 * <ul>
 * <li>while in an inventory, a tank tops up the held / worn items that accept its fluid;</li>
 * <li>on use, a tank scoops up the source block the player looks at;</li>
 * <li>the creative tank fills every held / worn item with both fluids.</li>
 * </ul>
 * Items accepting water are tagged {@code create_sa:fillable}, items accepting lava {@code create_sa:fuelable}.
 * Steam items carry both tags and keep lava in tank 0 and water in tank 1.
 */
public final class TankFilling {
	public static final TagKey<Item> FILLABLE = ItemTags.create(ResourceLocation.parse("create_sa:fillable"));
	public static final TagKey<Item> FUELABLE = ItemTags.create(ResourceLocation.parse("create_sa:fuelable"));

	/** mB moved per slot and per pass by a tank sitting in an inventory. */
	private static final int TICK_TRANSFER_MB = 10;
	/** Passes over the slots done every tick by a tank sitting in an inventory. */
	private static final int TICK_PASSES = 4;
	private static final int CREATIVE_TRANSFER_MB = 500;
	/** A scooped source block is worth a bucket: 1000 mB = 100 fuel/water units. */
	private static final int SCOOP_MB = 1000;
	private static final double SCOOP_REACH = 5;

	private TankFilling() {
	}

	public static TagKey<Item> acceptingTag(FuelType.Resource resource) {
		return resource == FuelType.Resource.WATER ? FILLABLE : FUELABLE;
	}

	/** Tank of {@code target} receiving {@code resource}: water goes to tank 1 on steam (also fuelable) items, everything else to tank 0. */
	public static int targetTank(ItemStack target, FuelType.Resource resource) {
		return resource == FuelType.Resource.WATER && target.is(FUELABLE) ? 1 : 0;
	}

	/** True when the tank of {@code target} receiving {@code resource} is not full. */
	static boolean hasSpace(IFluidHandlerItem handler, ItemStack target, FuelType.Resource resource) {
		int tank = targetTank(target, resource);
		return handler.getFluidInTank(tank).getAmount() < handler.getTankCapacity(tank);
	}

	/** Small, medium and large tanks in an inventory: pour {@link #TICK_TRANSFER_MB} into the hands and chest slot, {@link #TICK_PASSES} times. */
	public static void onTankTick(Level level, Entity entity, ItemStack tank, FuelType.Resource resource) {
		if (!(entity instanceof LivingEntity living) || FuelStorage.amount(tank, 0) <= 0)
			return;
		Transfer transfer = new Transfer(level, entity, TICK_TRANSFER_MB, tank);
		for (int pass = 0; pass < TICK_PASSES; pass++)
			transfer.offerToEquipment(living, resource);
	}

	/** Creative tank in an inventory: lava then water into the hands and chest slot, from an endless supply. */
	public static void onCreativeTankTick(Level level, Entity entity) {
		if (!(entity instanceof LivingEntity living))
			return;
		Transfer transfer = new Transfer(level, entity, CREATIVE_TRANSFER_MB, null);
		transfer.offerToEquipment(living, FuelType.Resource.LAVA);
		transfer.offerToEquipment(living, FuelType.Resource.WATER);
	}

	/** Tank used in hand: picks up the water / lava source block the player looks at (water sources inside waterlogged blocks are not removed). */
	public static void onTankUse(Level level, Player player, ItemStack tank, FuelType.Resource resource) {
		IFluidHandlerItem handler = FuelStorage.handler(tank);
		if (handler == null || handler.getFluidInTank(0).getAmount() >= handler.getTankCapacity(0))
			return;
		Vec3 eye = player.getEyePosition(1f);
		BlockHitResult hit = player.level().clip(new ClipContext(eye, eye.add(player.getViewVector(1f).scale(SCOOP_REACH)), ClipContext.Block.OUTLINE, ClipContext.Fluid.SOURCE_ONLY, player));
		if (hit.getType() != HitResult.Type.BLOCK)
			return;
		BlockPos pos = hit.getBlockPos();
		Block sourceBlock = resource == FuelType.Resource.WATER ? Blocks.WATER : Blocks.LAVA;
		if (level.getFluidState(pos).createLegacyBlock().getBlock() != sourceBlock)
			return;
		if (resource == FuelType.Resource.LAVA || level.getBlockState(pos).getBlock() == Blocks.WATER)
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
		FuelStorage.fill(tank, resource, SCOOP_MB, false);
		player.swing(InteractionHand.MAIN_HAND, true);
		SoundEvent sound = resource == FuelType.Resource.WATER ? SoundEvents.BOTTLE_FILL : SoundEvents.BUCKET_EMPTY_LAVA;
		playSound(level, player.getX(), player.getY(), player.getZ(), sound, 1f, 0.5f);
	}

	/** Plays a neutral sound for everyone on the server, or locally on the client. */
	static void playSound(Level level, double x, double y, double z, SoundEvent sound, float volume, float pitch) {
		if (!level.isClientSide())
			level.playSound(null, BlockPos.containing(x, y, z), sound, SoundSource.NEUTRAL, volume, pitch);
		else
			level.playLocalSound(x, y, z, sound, SoundSource.NEUTRAL, volume, pitch, false);
	}

	/**
	 * One series of transfers from a tank (or from the creative tank when {@code source} is null).
	 * The "has space" flag is shared by every slot of the series, as in the original code: a target without fluid
	 * capability reuses the answer computed for the previous slot.
	 */
	private static final class Transfer {
		private final Level level;
		private final Entity entity;
		private final int mb;
		@Nullable
		private final ItemStack source;
		private boolean hasSpace;

		Transfer(Level level, Entity entity, int mb, @Nullable ItemStack source) {
			this.level = level;
			this.entity = entity;
			this.mb = mb;
			this.source = source;
		}

		void offerToEquipment(LivingEntity living, FuelType.Resource resource) {
			offer(living.getMainHandItem(), resource);
			offer(living.getOffhandItem(), resource);
			offer(living.getItemBySlot(EquipmentSlot.CHEST), resource);
		}

		private void offer(ItemStack target, FuelType.Resource resource) {
			if (!target.is(acceptingTag(resource)) || (source != null && FuelStorage.amount(source, 0) <= 0))
				return;
			IFluidHandlerItem handler = FuelStorage.handler(target);
			if (handler != null)
				hasSpace = hasSpace(handler, target, resource);
			if (!hasSpace)
				return;
			FuelStorage.fill(target, resource, mb, false);
			if (source != null)
				CustomFluidHandlerItemStack.drainTank(source, new FluidStack(resource.fluid, mb), false);
			playSound(level, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BUCKET_EMPTY, 0.01f, 2f);
		}
	}
}
