package io.github.hxxniverse.hobeaktown.util

import io.github.hxxniverse.hobeaktown.util.coroutine.Hobeak
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * @param repeat 반복 여부
 * @param delay 시작 딜레이
 * @param interval 간격 ms
 * @param cycle 반복 횟수
 */
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
    private var startDate: LocalDateTime? = null
    private var onEachDate: LocalDateTime? = null

    // 다음 onEach까지 남은 시간
    fun getLeftInterval(): Long {
        // 시작하자마자 onEach 가 실행되지 않음.
        // 즉 첫번째 onEach 시간은 startDate + interval 임, 참고로 interval 은 ms 단위
        return (onEachDate ?: startDate)?.toInstant(ZoneOffset.UTC)?.toEpochMilli()
            ?.plus(interval)!! - LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()
    }

    fun start() {
        startDate = LocalDateTime.now()
        job = CoroutineScope(Dispatchers.Hobeak).launch {
            onStart()
            if (repeat) {
                delay(delay)
                var count = 0
                while (isActive) {
                    onEachDate = LocalDateTime.now()
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
