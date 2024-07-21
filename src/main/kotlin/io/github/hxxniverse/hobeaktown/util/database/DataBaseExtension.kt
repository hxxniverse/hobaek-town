package io.github.hxxniverse.hobeaktown.util.database

import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

fun <T> loggedTransaction(statement: Transaction.() -> T): T = transaction {
    addLogger(StdOutSqlLogger)
    statement()
}