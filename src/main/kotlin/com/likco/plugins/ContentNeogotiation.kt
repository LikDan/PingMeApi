package com.likco.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule

fun Application.configureContentNegotiation() {
    install(ContentNegotiation){
        val settings = Json {
            serializersModule = IdKotlinXSerializationModule

            ignoreUnknownKeys = true
            encodeDefaults = true
        }

        json(settings)
    }
}