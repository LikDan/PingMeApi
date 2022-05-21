package com.likco.routes

import com.likco.controlers.*
import io.ktor.server.routing.*

fun Route.monitors() = route("monitors") {
    get { getMonitors() }

    post { createMonitor() }
    get("{id}") { getMonitor() }
    put("{id}") { editMonitor() }
    delete("{id}") { deleteMonitor() }

    post("{id}/start") { actionMonitor(true) }
    post("{id}/stop") { actionMonitor(false) }
}