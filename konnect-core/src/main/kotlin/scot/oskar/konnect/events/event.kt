package scot.oskar.konnect.events

import io.ktor.network.sockets.*

/**
 * Sealed class representing various network events.
 */
sealed class NetworkEvent {
    /**
     * Represents a socket connection event.
     *
     * @property socket The connected socket.
     */
    data class SocketConnected(val socket: Socket) : NetworkEvent()

    /**
     * Represents a socket disconnection event.
     */
    object SocketDisconnected : NetworkEvent()

    /**
     * Represents a data received event.
     *
     * @param data The received data.
     * @param T The type of data received.
     */
    data class DataReceived<T>(val data: T) : NetworkEvent()
}

/**
 * Interface for network event listeners.
 */
interface NetworkEventListener {
    /**
     * Called when a socket connection is established.
     *
     * @param socket The connected socket.
     */
    fun onSocketConnected(socket: Socket) {}

    /**
     * Called when a socket is disconnected.
     */
    fun onSocketDisconnected() {}

    /**
     * Called when data is received.
     *
     * @param data The received data.
     * @param T The type of data received.
     */
    fun <T> onDataReceived(data: T) {}
}

/**
 * Class responsible for emitting network events to registered listeners.
 */
class NetworkEventEmitter {
    private val listeners = mutableListOf<NetworkEventListener>()

    /**
     * Adds a listener to receive network events.
     *
     * @param listener The listener to add.
     */
    fun addListener(listener: NetworkEventListener) {
        listeners.add(listener)
    }

    /**
     * Removes a listener from receiving network events.
     *
     * @param listener The listener to remove.
     */
    fun removeListener(listener: NetworkEventListener) {
        listeners.remove(listener)
    }

    /**
     * Emits a network event to all registered listeners.
     *
     * @param event The network event to emit.
     */
    fun emitEvent(event: NetworkEvent) {
        when (event) {
            is NetworkEvent.SocketConnected -> listeners.forEach { it.onSocketConnected(event.socket) }
            is NetworkEvent.SocketDisconnected -> listeners.forEach { it.onSocketDisconnected() }
            is NetworkEvent.DataReceived<*> -> listeners.forEach { it.onDataReceived(event.data) }
        }
    }
}