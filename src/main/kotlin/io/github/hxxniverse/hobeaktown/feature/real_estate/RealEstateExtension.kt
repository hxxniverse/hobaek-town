package io.github.hxxniverse.hobeaktown.feature.real_estate

import io.github.hxxniverse.hobeaktown.HobeakTownPlugin.Companion.plugin
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.extension.getBlockList
import io.github.hxxniverse.hobeaktown.util.extension.pretty
import kotlinx.serialization.json.*
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack
import java.io.File


fun RealEstate.toItemStack(): ItemStack {
    return ItemStackBuilder()
        .setDisplayName(name)
        .addLore("위치: ${pos1.pretty()} ~ ${pos2.pretty()}") // 좌표를 문자열로 변환
        .addLore("가격: $price")
        .addLore("남은일자: $remainingDays 일")
        .build()
}

// pos1, pos2를 이용하여 해당 좌표 사이의 블록을 저장하는 함수 x,y,z,material로 구성된 리스트를 반환
fun RealEstate.saveScheme() {
    println("saveScheme ${pos1.pretty()} ~ ${pos2.pretty()}")
    val file = File(plugin.dataFolder, "schematics/${name}.json")
    val blocks = mutableListOf<JsonElement>()
    (pos1 to pos2).getBlockList().forEach { block ->
        blocks.add(
            JsonObject(
                mapOf(
                    "x" to JsonPrimitive(block.x),
                    "y" to JsonPrimitive(block.y),
                    "z" to JsonPrimitive(block.z),
                    "material" to JsonPrimitive(block.type.name)
                )
            )
        )
    }
    file.writeText(JsonArray(blocks).toString())
}

fun RealEstate.getScheme(): List<Block> {
    val file = File(plugin.dataFolder, "schematics/${name}.json")
    val blocks = mutableListOf<Block>()

    if (!file.exists()) {
        return blocks
    }

    val json = Json.decodeFromString<JsonArray>(file.readText())

    for (element in json) {
        val x = element.jsonObject["x"]!!.jsonPrimitive.int
        val y = element.jsonObject["y"]!!.jsonPrimitive.int
        val z = element.jsonObject["z"]!!.jsonPrimitive.int

        val block = centerLocation.world.getBlockAt(x, y, z)
        blocks.add(block)
    }

    return blocks
}

fun RealEstate.loadScheme() {
    getScheme().forEach { block ->
        block.type = Material.AIR
    }
}