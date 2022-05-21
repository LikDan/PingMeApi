package com.likco.models

import com.likco.plugins.monitorsCollection
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.litote.kmongo.Id
import org.litote.kmongo.findOneById
import kotlin.time.Duration.Companion.seconds

class Uptime(private val id: Id<Monitor>, val userId: Id<User>, var running: Boolean = true) {
    private val scheduledEventFlow = flow {
        while (true) {
            val monitor = monitorsCollection.findOneById(id)
            if (monitor == null) {
                emit(null)
                break
            }

            if (running) emit(monitor)
            delay(monitor.interval.seconds)
        }
    }

    suspend fun run() {
        coroutineScope {
            scheduledEventFlow.onEach {
                println("Check status for $id (${it?.host})")
            }.launchIn(this)
        }
    }

    companion object {
        val uptimes = mutableMapOf<Id<Monitor>, Uptime>()

        suspend fun new(id: Id<Monitor>, userId: Id<User>): Uptime {
            val uptime = Uptime(id, userId)
            uptimes[id] = uptime

            uptime.run()
            return uptime
        }
    }
}