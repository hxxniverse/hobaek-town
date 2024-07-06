package io.github.hxxniverse.hobeaktown.util.extension

import io.github.hxxniverse.hobeaktown.util.coroutine.Hobeak
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun launch(block: suspend CoroutineScope.() -> Unit): Job {
    return CoroutineScope(Dispatchers.Hobeak).launch(block = block)
}