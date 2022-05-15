package com.likco.routes

import com.likco.controlers.*
import io.ktor.server.routing.*

fun Route.users() = route("user") {
    get { getUser() }
    put { editUser() }
    delete { deleteUser() }

    delete("revoke") { revokeUserToken() }
    post("signUp") { singUp() }
    post("login") { login() }

    get("{id}") { getUserInfo() }
}