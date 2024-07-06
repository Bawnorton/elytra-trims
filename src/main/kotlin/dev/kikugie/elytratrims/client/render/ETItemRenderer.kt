package dev.kikugie.elytratrims.client.render

import dev.kikugie.elytratrims.api.ElytraTrimsAPI
import dev.kikugie.elytratrims.client.CLIENT
import dev.kikugie.elytratrims.client.ETClient
import dev.kikugie.elytratrims.common.compat.FirstPersonModelCompat
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList

object ETItemRenderer {
    private var dummy: ArmorStandEntity? = null

    @JvmStatic
    fun render(stack: ItemStack, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int): Boolean {
        if (CLIENT.world == null) return false
        if (!ETClient.config.texture.useElytraModel) return false
        if (!ElytraTrimsAPI.isProbablyElytra(stack)) return false
        if (dummy == null || dummy?.world != CLIENT.world)
            dummy = ArmorStandEntity(CLIENT.world, 0.0, 0.0, 0.0).apply { isInvisible = true }
        matrices.push()
        matrices.scale(0.65F, 0.65F, -1F)
        val items = dummy!!.armorItems as DefaultedList<ItemStack>
        val slot = EquipmentSlot.CHEST.entitySlotId
        items[slot] = stack
        FirstPersonModelCompat.runWithFirstPerson {
            CLIENT.entityRenderDispatcher.render(
                dummy,
                0.775,
                -0.125,
                -0.2,
                0F,
                0F,
                matrices,
                vertexConsumers,
                light
            )
        }
        items[slot] = ItemStack.EMPTY
        matrices.pop()
        return true
    }
}