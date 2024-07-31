package io.github.hxxniverse.hobeaktown.feature.traffic

import io.github.hxxniverse.hobeaktown.util.extension.getPersistentData
import org.bukkit.inventory.ItemStack

fun ItemStack.isBusTicket(): Boolean {
    return getPersistentData<String>("busTicket") != null
}

fun ItemStack.isSubwayTicket(): Boolean {
    return getPersistentData<String>("subwayTicket") != null
}

fun ItemStack.isAirplaneTicket(): Boolean {
    return getPersistentData<String>("airplaneTicket") != null
}

