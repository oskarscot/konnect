package scot.oskar.konnect.test

import io.ktor.network.sockets.*
import scot.oskar.konnect.events.NetworkEventListener
import scot.oskar.konnect.konnectServer

fun main() {
    konnectServer(port = 54666, host = "localhost") {
        enableLogging()
        registerListener(SocketConnectedListener())
    }
}

class SocketConnectedListener : NetworkEventListener {
    override fun onSocketConnected(socket: Socket) {
        println("Connected to socket ${socket.remoteAddress}")
    }
}



