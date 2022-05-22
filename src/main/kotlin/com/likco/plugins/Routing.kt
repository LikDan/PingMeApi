package com.likco.plugins

import com.likco.routes.monitors
import com.likco.routes.testRequests
import com.likco.routes.users
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        route("api") {
            users()
            monitors()
            testRequests()
        }
    }
}
