package io.github.hxxniverse.hobeaktown.util.database

import org.bukkit.Material
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.vendors.currentDialect

class MaterialExposedType : ColumnType<Material>() {
    override fun sqlType(): String {
        return currentDialect.dataTypeProvider.textType()
    }

    override fun valueFromDB(value: Any): Material = when (value) {
        is String -> value.toMaterial()
        is Material -> value
        else -> error("$value is not a valid Material on from db value is ${value::class.simpleName}")
    }

    override fun notNullValueToDB(value: Material): Any = value.name

    companion object {
        internal val INSTANCE = MaterialExposedType()
    }
}

private fun String.toMaterial(): Material {
    return Material.getMaterial(this) ?: throw Exception("Material not found")
}

fun Table.material(name: String): Column<Material> = registerColumn(name, MaterialExposedType.INSTANCE)
