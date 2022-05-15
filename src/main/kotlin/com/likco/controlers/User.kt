package com.likco.controlers

import com.likco.hash
import com.likco.models.User
import com.likco.plugins.usersCollection
import com.likco.token
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.bson.types.ObjectId
import org.litote.kmongo.*

const val authTokenName = "authToken"

fun authUser(login: String, password: String): User? {
    val passwordHash = hash(password)

    val user = usersCollection.findOne(User::login eq login, User::password eq passwordHash) ?: return null
    if (user.token != null) return user

    user.token = token()
    usersCollection.updateOneById(user.id, setValue(User::token, user.token))

    return user
}

fun createUser(user: User): User? {
    if (user.password.length < 8 || user.name.length < 2) return null

    val passwordHash = hash(user.password)
    user.password = passwordHash

    usersCollection.insertOne(user)
    return user
}

fun authUser(call: ApplicationCall): User? {
    val token = call.request.cookies[authTokenName] ?: return null

    return usersCollection.findOne(User::token eq token)
}

suspend fun PipelineContext<*, ApplicationCall>.login() {
    @kotlinx.serialization.Serializable
    data class LoginData(val login: String, val password: String)

    val data = call.receiveOrNull<LoginData>() ?: return
    val user = authUser(data.login, data.password) ?: return

    if (user.token != null)
        call.response.cookies.append(authTokenName, user.token!!, path = "/", maxAge = 1000000L)

    call.respond(user)
}

suspend fun PipelineContext<*, ApplicationCall>.singUp() {
    val user = call.receiveOrNull<User>() ?: return
    createUser(user)

    call.respond(user)
}

suspend fun PipelineContext<*, ApplicationCall>.getUser() {
    val user = authUser(call) ?: return

    call.respond(user)
}

suspend fun PipelineContext<*, ApplicationCall>.editUser() {
    val user = authUser(call) ?: return
    val newUser = call.receive<User>()

    if (user.password.length < 8 || user.name.length < 2) return

    newUser.password = hash(newUser.password)
    usersCollection.updateOneById(
        user.id,
        SetTo(User::name, newUser.name),
        SetTo(User::password, newUser.password),
        SetTo(User::login, newUser.login)
    )

    call.respond(newUser)
}

suspend fun PipelineContext<*, ApplicationCall>.deleteUser() {
    val user = authUser(call) ?: return

    usersCollection.deleteOneById(user.id)

    call.respond(user.id)
}

suspend fun PipelineContext<*, ApplicationCall>.revokeUserToken() {
    call.response.cookies.append(authTokenName, "", maxAge = -1L)
    call.respond("ok")
}

suspend fun PipelineContext<*, ApplicationCall>.getUserInfo() {
    val id = call.parameters["id"] ?: return
    if (!ObjectId.isValid(id)) return

    val user = usersCollection.findOneById(ObjectId(id)) ?: return
    user.login = ""
    user.password = ""
    user.token = ""

    call.respond(user)
}