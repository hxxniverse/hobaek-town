package io.github.hxxniverse.hobeaktown.util.database

import org.bukkit.Bukkit
import org.bukkit.Location
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.vendors.currentDialect

class LocationExposedType : ColumnType<Location>() {
    override fun sqlType(): String {
        return currentDialect.dataTypeProvider.textType()
    }

    override fun valueFromDB(value: Any): Location = when (value) {
        is String -> value.toLocation()
        is Location -> value
        else -> error("$value is not a valid Location on from db value is ${value::class.simpleName}")
    }

    override fun notNullValueToDB(value: Location): Any =
        "${value.world.name};${value.x};${value.y};${value.z};${value.yaw};${value.pitch}"

    companion object {
        internal val INSTANCE = LocationExposedType()
    }
}

private fun String.toLocation(): Location {
    val slices = this.split(";")
    val worldName = slices[0]
    val world = Bukkit.getWorld(worldName) ?: throw Exception("World not found")

    return Location(
        world,
        slices[1].toDouble(),
        slices[2].toDouble(),
        slices[3].toDouble(),
        slices.getOrNull(4)?.toFloat() ?: 0f,
        slices.getOrNull(5)?.toFloat() ?: 0f,
    )
}

fun Table.location(name: String): Column<Location> = registerColumn(name, LocationExposedType.INSTANCE)