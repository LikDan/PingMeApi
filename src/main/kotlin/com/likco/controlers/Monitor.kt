package com.likco.controlers

import com.likco.models.Monitor
import com.likco.plugins.monitorsCollection
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.id.toId

fun validateMonitor(monitor: Monitor): Boolean {
    val urlRegex = Regex("^(http://|https://).*+$")
    val intervalRegex = Regex("\\d{1,4}")

    return monitor.host.matches(urlRegex) && monitor.interval.matches(intervalRegex)
}

suspend fun PipelineContext<*, ApplicationCall>.getMonitors() {
    val user = authUser(call) ?: return

    val monitors = monitorsCollection.find(Monitor::userId eq user.id).toList()
    call.respond(monitors)
}

suspend fun PipelineContext<*, ApplicationCall>.createMonitor() {
    val user = authUser(call) ?: return
    val monitor = call.receiveOrNull<Monitor>() ?: return
    if (!validateMonitor(monitor)) return

    monitor.userId = user.id
    monitor.events = emptyList()

    monitorsCollection.insertOne(monitor)

    call.respond(monitor)
}

suspend fun PipelineContext<*, ApplicationCall>.getMonitor() {
    val user = authUser(call) ?: return
    val id = call.parameters["id"] ?: return
    if (!ObjectId.isValid(id)) return

    val monitor = monitorsCollection.findOneById(ObjectId(id)) ?: return
    if (monitor.userId != user.id) return

    call.respond(monitor)
}

suspend fun PipelineContext<*, ApplicationCall>.editMonitor() {
    val user = authUser(call) ?: return
    val id = call.parameters["id"] ?: return
    if (!ObjectId.isValid(id)) return

    val newMonitor = call.receiveOrNull<Monitor>() ?: return
    if (!validateMonitor(newMonitor)) return

    newMonitor.userId = user.id
    newMonitor.id = ObjectId(id).toId()
    newMonitor.events = emptyList()

    monitorsCollection.updateOne(
        and(Monitor::id eq newMonitor.id, Monitor::userId eq newMonitor.userId),
        SetTo(Monitor::name, newMonitor.name),
    )

    call.respond(newMonitor)
}

suspend fun PipelineContext<*, ApplicationCall>.deleteMonitor() {
    val user = authUser(call) ?: return
    val id = call.parameters["id"] ?: return
    if (!ObjectId.isValid(id)) return

    monitorsCollection.deleteOne(and(Monitor::id eq ObjectId(id).toId(), Monitor::userId eq user.id))

    call.respond(id)
}