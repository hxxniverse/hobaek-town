package io.github.hxxniverse.hobeaktown

object HobeakTownConfig {

    val memory = HashMap<String, Any>()

    inline fun <reified T : Any> set(value: T) {
        memory[T::class.java.simpleName] = value
    }

    inline fun <reified T> get(): T? {
        return memory[T::class.java.simpleName] as T?
    }

    inline fun <reified T> get(defaultValue: T): T {
        return memory[T::class.java.simpleName] as T? ?: defaultValue
    }
}