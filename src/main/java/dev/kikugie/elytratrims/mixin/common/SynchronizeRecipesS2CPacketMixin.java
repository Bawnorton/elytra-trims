package dev.kikugie.elytratrims.mixin.common;

import com.google.common.collect.Iterables;
import dev.kikugie.elytratrims.common.config.ConfigTesters.RequireClientSide;
import dev.kikugie.elytratrims.common.plugin.MixinConfigurable;
import dev.kikugie.elytratrims.common.plugin.RequireTest;
import dev.kikugie.elytratrims.common.recipe.ETGlowRecipe;
import dev.kikugie.elytratrims.common.recipe.ETPatternRecipe;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Hides recipes from clients, because they use custom serializers and would cause registry desync.
 * The recipes are still available, but not present in the recipe book.
 */
@RequireTest(RequireClientSide.class)
@MixinConfigurable
@Mixin(value = SynchronizeRecipesS2CPacket.class, remap = false)
public abstract class SynchronizeRecipesS2CPacketMixin {
    @ModifyArg(method = "<init>(Ljava/util/Collection;)V", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList(Ljava/lang/Iterable;)Ljava/util/ArrayList;"))
    /*? if <1.20.2 {*/
    private Iterable<Recipe<?>> removeElytraPatternRecipe(Iterable<Recipe<?>> elements) {
        return Iterables.filter(elements, recipe -> !(recipe instanceof ETPatternRecipe) && !(recipe instanceof ETGlowRecipe));
    }
    /*?} else {*//*
    private Iterable<net.minecraft.recipe.RecipeEntry<? extends Recipe<?>>> removeElytraPatternRecipe(Iterable<net.minecraft.recipe.RecipeEntry<? extends Recipe<?>>> elements) {
        return Iterables.filter(elements, entry -> {
            Recipe<?> recipe = entry.value();
            return !(recipe instanceof ElytraPatternRecipe) && !(recipe instanceof ElytraGlowRecipe);
        });
    }
    *//*?} */
}
