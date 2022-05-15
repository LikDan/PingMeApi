package com.likco.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId

@Serializable
data class User(
    val name: String,
    var login: String,
    var password: String,
    var token: String? = null,
    @Contextual @BsonId var id: Id<User> = newId(),
)
