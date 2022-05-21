package com.likco.controlers

import com.likco.models.Monitor
import com.likco.models.Uptime
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

    return monitor.host.matches(urlRegex) && monitor.interval / 100 < 1
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

    Uptime.new(monitor.id, monitor.userId ?: return)

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

    var newMonitor = call.receiveOrNull<Monitor>() ?: return
    if (!validateMonitor(newMonitor)) return

    monitorsCollection.updateOne(
        and(Monitor::id eq ObjectId(id).toId(), Monitor::userId eq user.id),
        SetTo(Monitor::name, newMonitor.name),
        SetTo(Monitor::host, newMonitor.host),
        SetTo(Monitor::method, newMonitor.method),
        SetTo(Monitor::body, newMonitor.body),
        SetTo(Monitor::headers, newMonitor.headers),
        SetTo(Monitor::cookies, newMonitor.cookies),
        SetTo(Monitor::interval, newMonitor.interval),
    )

    newMonitor = monitorsCollection.findOneById(ObjectId(id)) ?: return
    call.respond(newMonitor)
}

suspend fun PipelineContext<*, ApplicationCall>.deleteMonitor() {
    val user = authUser(call) ?: return
    val id = call.parameters["id"] ?: return
    if (!ObjectId.isValid(id)) return

    monitorsCollection.deleteOne(and(Monitor::id eq ObjectId(id).toId(), Monitor::userId eq user.id))

    call.respond(id)
}

suspend fun PipelineContext<*, ApplicationCall>.actionMonitor(running: Boolean) {
    val user = authUser(call) ?: return
    val id = call.parameters["id"] ?: return
    if (!ObjectId.isValid(id)) return

    val uptime = Uptime.uptimes[ObjectId(id).toId()] ?: return
    if (uptime.userId != user.id) return
    uptime.running = running

    call.respond(id)
}