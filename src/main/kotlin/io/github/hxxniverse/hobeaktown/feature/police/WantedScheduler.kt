package io.github.hxxniverse.hobeaktown.feature.police

import io.github.hxxniverse.hobeaktown.util.BaseScheduler

class WantedScheduler(
    private val plugin: PoliceFeature
) : BaseScheduler(true, 5000) {
    override suspend fun onStart() {

    }

    override suspend fun onEach(count: Int) {

    }

    override fun onStop() {

    }
}