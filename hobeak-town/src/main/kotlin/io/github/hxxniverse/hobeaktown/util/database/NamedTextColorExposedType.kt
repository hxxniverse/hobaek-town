package io.github.hxxniverse.hobeaktown.util.database

import net.kyori.adventure.text.format.NamedTextColor
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.vendors.currentDialect

class NamedTextColorExposedType : ColumnType<NamedTextColor>() {
    override fun sqlType(): String {
        return currentDialect.dataTypeProvider.integerType()
    }

    override fun valueFromDB(value: Any): NamedTextColor = when (value) {
        is Int -> NamedTextColor.ofExact(value) ?: error("$value is not a valid NamedTextColor on from db")
        is NamedTextColor -> value
        else -> error("$value is not a valid NamedTextColor on from db value is ${value::class.simpleName}")
    }

    override fun notNullValueToDB(value: NamedTextColor): Any = value.value()

    companion object {
        internal val INSTANCE = NamedTextColorExposedType()
    }
}

fun Table.namedTextColor(name: String): Column<NamedTextColor> =
    registerColumn(name, NamedTextColorExposedType.INSTANCE)