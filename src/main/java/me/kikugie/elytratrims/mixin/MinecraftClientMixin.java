package me.kikugie.elytratrims.mixin;

import me.kikugie.elytratrims.ElytraTrimsMod;
import me.kikugie.elytratrims.texture.ElytraColorOverlayAtlasSource;
import me.kikugie.elytratrims.texture.ElytraPatternsAtlasSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.texture.atlas.AtlasSourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resource/DefaultClientResourcePackProvider;getResourcePack()Lnet/minecraft/resource/DefaultResourcePack;"))
    private void initTrimsManager(RunArgs args, CallbackInfo ci) {
//        ElytraTrimsMod.ELYTRA_TRIMS = AtlasSourceManager.register("elytra_trims", ElytraTrimsAtlasSource.CODEC);
        ElytraTrimsMod.ELYTRA_PATTERNS = AtlasSourceManager.register("elytra_patterns", ElytraPatternsAtlasSource.CODEC);
        ElytraTrimsMod.ELYTRA_OVERLAY = AtlasSourceManager.register("elytra_overlay", ElytraColorOverlayAtlasSource.CODEC);
    }
}
