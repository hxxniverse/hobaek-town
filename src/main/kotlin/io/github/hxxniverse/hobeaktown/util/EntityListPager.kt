package io.github.hxxniverse.hobeaktown.util

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction

class EntityListPager<out T : Entity<Int>>(
    private val entityClass: EntityClass<Int, T>,
    private val limit: Int = 45,
    private val condition: SqlExpressionBuilder.() -> Op<Boolean> = { Op.TRUE }
) {
    private var page = 0

    fun nextPage() {
        if (loggedTransaction { entityClass.count() } <= limit * (page + 1)) return
        page++
    }

    fun previousPage() {
        if (page > 0) {
            page--
        }
    }

    fun get(page: Int): List<T> {
        return loggedTransaction { entityClass.find(condition).limit(limit, page * limit.toLong()).toList() }
    }

    fun getCurrentPage(): List<T> {
        return get(page)
    }

    fun getCurrentPageNumber(): Int {
        return page
    }

    fun hasNextPage(): Boolean {
        return loggedTransaction { entityClass.count() > limit * (page + 1) }
    }

    fun hasPreviousPage(): Boolean {
        return page > 0
    }
}
