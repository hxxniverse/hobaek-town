package io.github.hxxniverse.hobeaktown.util.serializer

import io.github.hxxniverse.hobeaktown.util.database.toBytes
import io.github.hxxniverse.hobeaktown.util.database.toItemStack
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.inventory.ItemStack

object ItemStackSerializer : KSerializer<ItemStack> {

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("org.bukkit.ItemStack", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ItemStack {
        return decoder.decodeString().encodeToByteArray().toItemStack()
    }

    override fun serialize(encoder: Encoder, value: ItemStack) {
        encoder.encodeString(value.toBytes().decodeToString())
    }
}
