package io.github.hxxniverse.hobeaktown.util

import io.github.hxxniverse.hobeaktown.HobeakTownPlugin.Companion.plugin
import io.github.hxxniverse.hobeaktown.util.serializer.ItemStackSerializer
import io.github.hxxniverse.hobeaktown.util.serializer.LocationSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import java.io.File
import java.io.IOException

abstract class FeatureConfig<T>(
    private val filePath: String,
    private val initialValue: T,
    private val serializer: KSerializer<T>
) {

    var configData: T = initialValue
        protected set

    fun load(): Boolean {
        return try {
            val file = File(plugin.dataFolder, filePath)
            if (!file.exists()) {
                println("File not found: $filePath Creating new config file.")
                configData = initialValue
                save()
                return false
            }
            val content = file.readText()
            configData = json.decodeFromString(serializer, content)
            println("Config loaded: $configData")
            true
        } catch (e: IOException) {
            println("Error loading config: ${e.message}")
            false
        } catch (e: SerializationException) {
            println("Error deserializing config: ${e.message}")
            false
        }
    }

    private val json = Json {
        encodeDefaults = true
        prettyPrint = true
        serializersModule = SerializersModule {
            contextual(LocationSerializer)
            contextual(ItemStackSerializer)
        }
    }

    fun save(): Boolean {
        return try {
            val file = File(plugin.dataFolder, filePath)
            val content = json.encodeToString(serializer, configData)
            println("Saving config: $content")
            file.writeText(content)
            println("Config saved: $configData")
            true
        } catch (e: IOException) {
            println("Error saving config: ${e.message}")
            false
        } catch (e: SerializationException) {
            println("Error serializing config: ${e.message}")
            false
        }
    }

    fun updateConfigData(block: T.() -> T) {
        configData?.block()?.let { newConfigData ->
            configData = newConfigData
            save()
        }
    }
}
