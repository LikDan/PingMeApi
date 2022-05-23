package com.likco.controlers

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<*, ApplicationCall>.bindTest() {
    fun string(number: Int, value: String) = buildString {
        appendLine()
        for (i in 0..number) append(value)
        appendLine()
    }

    fun Map<*, *>.normalize() =
        toList().joinToString("\n", string(25, "*"), string(25, "*")) { "${it.first}: ${it.second}" }

    call.respond(
        """
This is ${call.request.httpMethod.value} request with:

body:
'${call.receiveText()}'

query params:
${call.request.queryParameters.toMap().normalize()}

headers:
${call.request.headers.toMap().normalize()}

cookies:
${call.request.cookies.rawCookies.normalize()}
        """.trimIndent()
    )
}