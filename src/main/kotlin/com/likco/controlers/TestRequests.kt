package com.likco.controlers

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<*, ApplicationCall>.testGet() {
    call.respond("This is get request")
}

suspend fun PipelineContext<*, ApplicationCall>.testPost() {
    call.respond("This is post request")
}

suspend fun PipelineContext<*, ApplicationCall>.testPut() {
    call.respond("This is put request")
}

suspend fun PipelineContext<*, ApplicationCall>.testDelete() {
    call.respond("This is delete request")
}
