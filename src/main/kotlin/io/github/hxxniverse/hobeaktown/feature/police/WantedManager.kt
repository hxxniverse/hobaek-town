package io.github.hxxniverse.hobeaktown.feature.police

import java.util.*

object WantedManager {
    private val wanteds = mutableMapOf<UUID, Wanted>()

    fun addWanted(uuid: UUID, level: Int) {
        wanteds[uuid] = Wanted(uuid, level)
    }

    fun removeWanted(uuid: UUID) {
        wanteds.remove(uuid)
    }

    fun getWanted(uuid: UUID): Wanted? {
        return wanteds[uuid]
    }

    fun getWanteds(): Collection<Wanted> {
        return wanteds.values
    }
}

data class Wanted(val uuid: UUID, val level: Int)