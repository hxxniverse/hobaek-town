package io.github.hxxniverse.hobeaktown.util.extension

import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataContainer
import kotlin.reflect.jvm.jvmName

inline fun <reified T : Any> Entity.setPersistentData(key: String, value: T) {
    persistentData[key] = value
}

inline fun <reified T : Any> Entity.removePersistentData(key: String) {
    persistentData.remove(key)
}

inline fun <reified T : Any> Entity.setPersistentData(value: T) {
    persistentData[T::class.jvmName] = value
}

inline fun <reified T : Any> Entity.getPersistentData(key: String): T? {
    return persistentData.get<T>(key)
}

inline fun <reified T : Any> Entity.getPersistentData(): T? {
    return persistentData[T::class.jvmName]
}

inline fun <reified T : Any> ItemStack.setPersistentData(key: String, value: T): ItemStack {
    if (itemMeta == null) return this
    itemMeta = itemMeta.apply { persistentData[key] = value }
    return this
}

inline fun <reified T : Any> ItemStack.setPersistentData(value: T): ItemStack {
    if (itemMeta == null) return this
    itemMeta = itemMeta.apply { persistentData[value::class.jvmName] = value }
    return this
}

inline fun <reified T : Any> ItemStack.getPersistentData(key: String): T? {
    if (itemMeta == null) return null
    return itemMeta.persistentData[key]
}

inline fun <reified T : Any> ItemStack.getPersistentData(): T? {
    if (itemMeta == null) return null
    return itemMeta.persistentData[T::class.jvmName]
}

inline fun <reified T : Any> ItemMeta.setPersistentData(key: String, value: T): ItemMeta {
    apply { persistentData[key] = value }
    return this
}

inline fun <reified T : Any> ItemMeta.setPersistentData(value: T): ItemMeta {
    apply { persistentData[value::class.jvmName] = value }
    return this
}

inline fun <reified T : Any> ItemMeta.getPersistentData(key: String): T? {
    return persistentData[key]
}

inline fun <reified T : Any> ItemMeta.getPersistentData(): T? {
    return persistentData[T::class.jvmName]
}
