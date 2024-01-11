package dev.kikugie.elytratrims.mixin.common;

import com.google.common.collect.Iterables;
import dev.kikugie.elytratrims.common.recipe.ElytraGlowRecipe;
import dev.kikugie.elytratrims.common.recipe.ElytraPatternRecipe;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Hides recipes from clients, because they use custom serializers and would cause registry desync.
 * The recipes are still available, but not present in the recipe book.
 */
@Mixin(SynchronizeRecipesS2CPacket.class)
public abstract class SynchronizeRecipesS2CPacketMixin {
    @ModifyArg(method = "<init>(Ljava/util/Collection;)V", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList(Ljava/lang/Iterable;)Ljava/util/ArrayList;"))
    private Iterable<Recipe<?>> removeElytraPatternRecipe(Iterable<Recipe<?>> elements) {
        return Iterables.filter(elements, recipe -> !(recipe instanceof ElytraPatternRecipe) && !(recipe instanceof ElytraGlowRecipe));
    }
}
