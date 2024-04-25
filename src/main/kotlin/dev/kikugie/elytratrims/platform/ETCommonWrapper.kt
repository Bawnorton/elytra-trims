package dev.kikugie.elytratrims.platform

import dev.kikugie.elytratrims.client.ETClient
import dev.kikugie.elytratrims.client.config.lib.ConfigScreenProvider
import dev.kikugie.elytratrims.client.resource.ETAtlasHolder
import dev.kikugie.elytratrims.common.ETCommon
import dev.kikugie.elytratrims.common.ETReference
import dev.kikugie.elytratrims.common.recipe.ETGlowRecipe
import dev.kikugie.elytratrims.common.recipe.ETPatternRecipe
import net.minecraft.recipe.RecipeSerializer

/*? if fabric {*/
object ETCommonWrapper : net.fabricmc.api.ModInitializer {
    override fun onInitialize() {
        ETCommon.init()

        if (ETCommon.config.addPatterns) RecipeSerializer.register(
            "crafting_special_elytrapatterns",
            ETPatternRecipe.SERIALIZER
        )
        if (ETCommon.config.addGlow) RecipeSerializer.register(
            "crafting_special_elytraglow",
            ETGlowRecipe.SERIALIZER
        )
    }
}
/*?} elif forge {*//*
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.runWhenOn
import java.util.function.Supplier

@Mod(ETReference.MOD_ID)
object ETCommonWrapper {
    init {
        ETCommon.init()
        runWhenOn(Dist.CLIENT) {
            ETClient.init()
            MOD_BUS.addListener<RegisterClientReloadListenersEvent> {
                it.registerReloadListener(ETAtlasHolder)
            }

            val provider = ConfigScreenProvider.screen
            if (provider != null) ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenFactory::class.java,
            ) { ConfigScreenFactory { _, parent -> provider(parent) } }
        }
        val registry = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "minecraft")
        if (ETCommon.config.addPatterns) registry.register(
            "crafting_special_elytrapatterns"
        ) { ETPatternRecipe.SERIALIZER }
        if (ETCommon.config.addGlow) registry.register(
            "crafting_special_elytraglow"
        ) { ETGlowRecipe.SERIALIZER }
        registry.register(MOD_BUS)
    }

}
*//*?} else {*//*
import net.minecraft.registry.Registries
import net.neoforged.fml.common.Mod
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.client.ConfigScreenHandler.ConfigScreenFactory
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runWhenOn
import java.util.function.Supplier

@Mod(ETReference.MOD_ID)
object ETCommonWrapper {
    init {
        ETCommon.init()
        runWhenOn(Dist.CLIENT) {
            ETClient.init()
            MOD_BUS.addListener<RegisterClientReloadListenersEvent> {
                it.registerReloadListener(ETAtlasHolder)
            }

            val provider = ConfigScreenProvider.screen
            if (provider != null) ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenFactory::class.java,
            ) { ConfigScreenFactory { _, parent -> provider(parent) } }
        }

        val registry = DeferredRegister.create(Registries.RECIPE_SERIALIZER, "minecraft")
        if (ETCommon.config.addPatterns) registry.register(
            "crafting_special_elytrapatterns",
            Supplier { ETPatternRecipe.SERIALIZER }
        )
        if (ETCommon.config.addGlow) registry.register(
            "crafting_special_elytraglow",
            Supplier { ETGlowRecipe.SERIALIZER }
        )
        registry.register(MOD_BUS)
    }
}
*//*?}} */