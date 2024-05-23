package io.github.hxxniverse.hobeaktown.util.extension

import java.util.UUID

val systemUUID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

val UUID.isSystemUUID: Boolean
    get() = this == systemUUID

val marketUUID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000001")

val UUID.isMarketUUID: Boolean
    get() = this == marketUUID