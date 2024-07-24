package io.github.hxxniverse.hobeaktown.util.extension

fun <T> Map<T, Int>.weightedRandomFromList(): T {
    val itemList = mutableListOf<T>()
    for ((item, weight) in this) {
        repeat(weight) {
            itemList.add(item)
        }
    }
    return itemList.random()
}