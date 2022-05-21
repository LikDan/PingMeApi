package com.likco.plugins

import com.likco.models.Uptime
import io.ktor.server.application.*
import kotlinx.coroutines.launch

fun Application.configureUptime() {
    monitorsCollection.find().mapNotNull { it.id to (it.userId ?: return@mapNotNull null) }.forEach {
        launch { Uptime.new(it.first, it.second) }
    }
}
