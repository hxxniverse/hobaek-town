package io.github.hxxniverse.hobeaktown.util

import io.github.monun.heartbeat.coroutines.Heartbeat
import kotlinx.coroutines.*

abstract class BaseScheduler(
    val repeat: Boolean,
    val delay: Long,
    val interval: Long,
    val cycle: Long,
) {

    constructor(
        repeat: Boolean,
        interval: Long,
    ) : this(repeat, 0, interval, 0)

    private var job: Job? = null

    fun start() {
        job = CoroutineScope(Dispatchers.Heartbeat).launch {
            onStart()
            if (repeat) {
                delay(delay)
                var count = 0
                while (isActive) {
                    onEach(count)
                    count++
                    delay(interval)
                }
            } else {
                repeat(cycle.toInt()) {
                    delay(interval)
                    onEach(it)
                }
            }
            onStop()
        }
    }

    fun stop() {
        job?.cancel()
        job?.invokeOnCompletion { onStop() }
    }

    fun cancel() {
        job?.cancel()
    }

    protected abstract suspend fun onStart()

    protected abstract suspend fun onEach(count: Int)

    protected abstract fun onStop()
}
