package com.likco

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.likco.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", watchPaths = listOf("classes", "resources")) {
        configureContentNegotiation()
        configureCallLogging()
        configureMongoDb()
        configureRouting()
    }.start(wait = true)
}
