package io.github.hxxniverse.hobeaktown.util.extension

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType

data class PersistentDataKey<T, Z>(
    val namespacedKey: NamespacedKey,
    val dataType: PersistentDataType<T, Z>,
)
