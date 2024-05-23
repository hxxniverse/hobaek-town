package io.github.hxxniverse.hobeaktown.util.database

import org.bukkit.Bukkit
import org.bukkit.World
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.vendors.currentDialect

class WorldExposedType : ColumnType<World>() {
    override fun sqlType(): String {
        return currentDialect.dataTypeProvider.textType()
    }

    override fun valueFromDB(value: Any): World = when (value) {
        is String -> value.toWorld()
        is World -> value
        else -> error("$value is not a valid World on from db value is ${value::class.simpleName}")
    }

    override fun notNullValueToDB(value: World): Any = value.name

    companion object {
        internal val INSTANCE = WorldExposedType()
    }
}

private fun String.toWorld(): World {
    return Bukkit.getWorld(this) ?: throw Exception("World not found")
}

fun Table.world(name: String): Column<World> = registerColumn(name, WorldExposedType.INSTANCE)
