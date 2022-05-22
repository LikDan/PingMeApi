package com.likco.models

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.Parameters
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
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
                if (it == null) return@onEach

                val httpAsync = FuelManager().request(Method.valueOf(it.method), it.host, null)
                    .responseString { request, response, result ->
                        when (result) {
                            is Result.Failure -> {
                                val ex = result.getException()
                                println(ex)
                            }
                            is Result.Success -> {
                                val data = result.get()
                                println(result.value)
                            }
                        }
                    }

                httpAsync.join()
                println("Check status for $id (${it.host})")
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