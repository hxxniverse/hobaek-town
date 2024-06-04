package io.github.hxxniverse.hobeaktown.util.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Bukkit
import org.bukkit.Location

object LocationSerializer : KSerializer<Location> {

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("org.bukkit.Location", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Location {
        val input = decoder.decodeString().split(";")
        val world = Bukkit.getWorld(input[0])
        val x = input[1].toDouble()
        val y = input[2].toDouble()
        val z = input[3].toDouble()
        val yaw = input[4].toFloat()
        val pitch = input[5].toFloat()
        return Location(world, x, y, z, yaw, pitch)
    }

    override fun serialize(encoder: Encoder, value: Location) {
        encoder.encodeString("${value.world.name};${value.x};${value.y};${value.z};${value.yaw};${value.pitch}")
    }
}
