package com.likco.routes

import com.likco.controlers.bindTest
import io.ktor.server.routing.*

fun Route.testRequests() = route("test") {
    get { bindTest() }
    post { bindTest() }
    put { bindTest() }
    delete { bindTest() }
}