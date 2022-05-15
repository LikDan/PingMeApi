package com.likco

import java.security.MessageDigest

fun hash(str: String): String {
    val bytes = str.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("") { s, it -> s + "%02x".format(it) }
}

fun token(): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')

    return (1..255).joinToString("") { allowedChars.random().toString() }
}