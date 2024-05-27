package io.github.hxxniverse.hobeaktown.util.coroutine

import io.github.hxxniverse.hobeaktown.HobeakTownPlugin.Companion.plugin
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.Plugin

private object HobeakCoroutine {
    private var session: HobeakSession? = null

    fun session(): HobeakSession {
        // double-checked locking
        if (session == null) {
            synchronized(this) {
                if (session == null) {
                    require(plugin.isEnabled) { "Plugin attempted to register HobeakCoroutine while not enabled" }

                    val server = plugin.server

                    session = HobeakSession(plugin).also { activity ->
                        server.pluginManager.registerEvents(object : Listener {
                            @EventHandler(priority = EventPriority.LOWEST)
                            fun onPluginDisable(event: PluginDisableEvent) {
                                synchronized(this@HobeakCoroutine) {
                                    session = null
                                    activity.cancel()
                                }
                            }
                        }, plugin)
                    }
                }
            }
        }

        return session.validate()
    }
}

private fun HobeakSession?.validate(): HobeakSession {
    requireNotNull(this) { "Failed to create HobeakCoroutine" }
    require(isValid) { "Invalid HobeakCoroutine session" }

    return this
}

/**
 * Bukkit의 mainHeartBeat 에서 실행하는 [CoroutineDispatcher]를 가져옵니다
 *
 * 라이브러리를 로드한 [Plugin]의 생명주기를 따릅니다.
 */
val Dispatchers.Hobeak: CoroutineDispatcher
    get() = HobeakCoroutine.session().dispatcher

/**
 * [Dispatchers.Hobeak] 를 기본 [CoroutineDispatcher]로 가진 [CoroutineScope]를 생성합니다.
 *
 * 라이브러리를 로드한 [Plugin]의 생명주기를 따릅니다.
 */
@Suppress("FunctionName")
fun HobeakScope(): CoroutineScope = HobeakCoroutine.session().let { session ->
    CoroutineScope(session.dispatcher + Job(session.supervisorJob))
}