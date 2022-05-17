package com.likco.plugins

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.likco.models.Monitor
import com.likco.models.User
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import io.ktor.server.application.*
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import org.slf4j.LoggerFactory

lateinit var client: MongoClient
lateinit var database: MongoDatabase

lateinit var usersCollection: MongoCollection<User>
lateinit var monitorsCollection: MongoCollection<Monitor>

fun Application.configureMongoDb() {
    val logger = LoggerFactory.getLogger("org.mongodb.driver") as Logger
    logger.level = Level.WARN

    client = KMongo.createClient(System.getenv("dbCredentials"))
    database = client.getDatabase("PingMe")

    usersCollection = database.getCollection()
    monitorsCollection = database.getCollection()
}



