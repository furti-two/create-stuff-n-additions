package net.furtitwo.createstuffadditions.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.Entity;

import net.furtitwo.createstuffadditions.logic.armor.NetheriteFireImmunity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

@Mixin(value = Entity.class, priority = 1501)
public class NetheriteFireImuneMixin {
	@ModifyReturnValue(method = "fireImmune()Z", at = @At("RETURN"))
	public boolean create$onFireImmune(boolean original) {
		return ((Entity) (Object) this).getPersistentData().getBoolean(NetheriteFireImmunity.FIRE_IMMUNE_KEY) || original;
	}
}
