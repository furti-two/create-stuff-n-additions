package net.furtitwo.createstuffadditions.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.multiplayer.PlayerInfo;

import javax.annotation.Nullable;

import java.util.List;
import java.util.Arrays;

import com.mojang.authlib.GameProfile;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {
	private static final List UnusualCapeList = Arrays.asList("Furti_Two", "To0pa", "Sweetygamer2", "Leroidesafk", "Zezer_123", "Doblou");
	private static final List DreamySMPCapeList = Arrays.asList("RaptorR43", "itsluhyall", "Dreamy_s", "_MaitreTis_", "haise800", "natsunaruto56", "gnfail8028", "z122", "FlorianFlan", "hb50120");
	private static final List TranslatorCapeList = Arrays.asList("Robocraft999", "VladisCrafter", "ZipeStudio", "OMEGAU371", "maingvaldsen", "Hardziplay", "MagickSchmagick", "hamster768", "Dev");
	private static final List AlphaTestCapeList = Arrays.asList("DariusZeBaguette", "Ultraman001");

	@Shadow
	@Nullable
	protected abstract net.minecraft.client.multiplayer.PlayerInfo getPlayerInfo();

	@Shadow
	@Nullable
	private PlayerInfo playerInfo;

	public AbstractClientPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
		super(level, pos, yRot, gameProfile);
	}

	@Inject(method = "getSkin", at = @At("TAIL"), cancellable = true)
	public void getSkinMixin(CallbackInfoReturnable<PlayerSkin> cir) {
		PlayerInfo playerInfo = this.getPlayerInfo();
		if (playerInfo != null) {
			String username = playerInfo.getProfile().getName();
			if (UnusualCapeList.contains(username)) {
				PlayerSkin playerSkin = cir.getReturnValue();
				cir.setReturnValue(new PlayerSkin(playerSkin.texture(), playerSkin.textureUrl(), ResourceLocation.parse("create_sa:textures/capes/unusual_squad.png"), playerSkin.elytraTexture(), playerSkin.model(), playerSkin.secure()));
			} else if (AlphaTestCapeList.contains(username)) {
				PlayerSkin playerSkin = cir.getReturnValue();
				cir.setReturnValue(new PlayerSkin(playerSkin.texture(), playerSkin.textureUrl(), ResourceLocation.parse("create_sa:textures/capes/alpha_tester.png"), playerSkin.elytraTexture(), playerSkin.model(), playerSkin.secure()));
			} else if (TranslatorCapeList.contains(username)) {
				PlayerSkin playerSkin = cir.getReturnValue();
				cir.setReturnValue(new PlayerSkin(playerSkin.texture(), playerSkin.textureUrl(), ResourceLocation.parse("create_sa:textures/capes/translator.png"), playerSkin.elytraTexture(), playerSkin.model(), playerSkin.secure()));
			} else if (DreamySMPCapeList.contains(username)) {
				PlayerSkin playerSkin = cir.getReturnValue();
				cir.setReturnValue(new PlayerSkin(playerSkin.texture(), playerSkin.textureUrl(), ResourceLocation.parse("create_sa:textures/capes/dreamy_smp.png"), playerSkin.elytraTexture(), playerSkin.model(), playerSkin.secure()));
			}
		}
	}
}
