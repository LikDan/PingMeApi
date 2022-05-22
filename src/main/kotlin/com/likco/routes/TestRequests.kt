package com.likco.routes

import com.likco.controlers.testDelete
import com.likco.controlers.testGet
import com.likco.controlers.testPost
import com.likco.controlers.testPut
import io.ktor.server.routing.*

fun Route.testRequests() = route("test") {
    get { testGet() }
    post { testPost() }
    put { testPut() }
    delete { testDelete() }
}