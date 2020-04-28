package com.example.grpc_channel_switching.network

import java.net.Socket

fun ping(host: String, port: Int): Boolean = try {
    val socket = Socket(host, port)
    socket.close()
    true
} catch (throwable: Throwable) {
    false
}