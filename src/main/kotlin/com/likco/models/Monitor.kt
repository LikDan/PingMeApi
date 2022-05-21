package com.likco.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId

@Serializable
data class Monitor(
    val name: String,
    val host: String,
    val method: String = "GET",
    val body: String = "",
    val headers: List<Pair<String, String>> = emptyList(),
    val cookies: List<Pair<String, String>> = emptyList(),
    val interval: Int,
    var events: List<String> = emptyList(),
    @Contextual var userId: Id<User>? = null,
    @Contextual @BsonId var id: Id<Monitor> = newId(),
)
