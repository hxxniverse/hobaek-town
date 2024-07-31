package io.github.hxxniverse.hobeaktown.util.coroutine

import io.github.hxxniverse.hobeaktown.HobeakTownPlugin.Companion.plugin
import kotlinx.coroutines.*

fun runTaskRepeat(interval: Long, repeat: Int = -1, task: suspend () -> Unit): Job {
    return CoroutineScope(Dispatchers.Hobeak + plugin.jobs).launch {
        if (repeat == -1) {
            while (true) {
                task()
                delay(interval)
            }
        } else {
            repeat(repeat) {
                task()
                delay(interval)
            }
        }
    }
}

fun runTaskLater(delay: Long, task: suspend () -> Unit): Job {
    return CoroutineScope(Dispatchers.Hobeak + plugin.jobs).launch {
        delay(delay)
        task()
    }
}

fun runTask(task: suspend () -> Unit): Job {
    return CoroutineScope(Dispatchers.Hobeak + plugin.jobs).launch {
        task()
    }
}