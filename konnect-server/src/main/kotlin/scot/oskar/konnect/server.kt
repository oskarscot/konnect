package scot.oskar.konnect

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import scot.oskar.konnect.events.NetworkEvent
import scot.oskar.konnect.events.NetworkEventEmitter
import scot.oskar.konnect.events.NetworkEventListener
import scot.oskar.konnect.serializer.ByteArraySerializer
import scot.oskar.konnect.serializer.Serializer

/**
 * Creates a KonnectServer with the specified port, host, and configuration options.
 *
 * @param port The port number for the server to listen on. Default is 54666.
 * @param host The host address for the server. Default is "127.0.0.1".
 * @param block Configuration block for the KonnectServerBuilder to apply additional settings.
 * @return A KonnectServer instance configured according to the provided parameters and block.
 */
fun konnectServer(port: Int = 54666, host: String = "127.0.0.1", block: KonnectServerBuilder.() -> Unit): KonnectServer {
    val builder = KonnectServerBuilder(port, host)
    builder.block()
    return builder.build()
}

/**
 * Builder class for configuring a KonnectServer instance.
 *
 * @property port The port number for the server.
 * @property host The host address for the server.
 */
class KonnectServerBuilder(private val port: Int, private val host: String) {
    private var serializer: Serializer<*> = ByteArraySerializer()
    private var loggingEnabled: Boolean = false
    private val listeners = mutableListOf<NetworkEventListener>()

    /**
     * Registers a custom serializer for the server.
     *
     * @param serializer The serializer to be registered.
     * @return This KonnectServerBuilder instance for method chaining.
     */
    fun registerSerializer(serializer: Serializer<*>) = apply { this.serializer = serializer }

    /**
     * Enables logging for the server.
     *
     * @return This KonnectServerBuilder instance for method chaining.
     */
    fun enableLogging() = apply { this.loggingEnabled = true }

    /**
     * Registers a listener for network events.
     *
     * @param listener The listener to be registered.
     * @return This KonnectServerBuilder instance for method chaining.
     */
    fun registerListener(listener: NetworkEventListener) = apply { listeners.add(listener) }

    /**
     * Builds a KonnectServer instance with the configured settings.
     *
     * @return A KonnectServer instance.
     */
    fun build(): KonnectServer {
        val emitter = NetworkEventEmitter()
        val unit: () -> Unit = {
            registerSerializer(serializer)
            if (loggingEnabled) enableLogging()
            listeners.forEach { emitter.addListener(it) }
        }
        return KonnectServer(host, port, emitter, serializer, loggingEnabled, unit)
    }
}

/**
 * Represents a KonnectServer instance.
 *
 * @property host The host address for the server.
 * @property port The port number for the server.
 * @property emitter The network event emitter for the server.
 * @property serializer The serializer used by the server.
 * @property loggingEnabled Indicates whether logging is enabled for the server.
 * @property unit The initialization unit for the server.
 */
class KonnectServer internal constructor(
    host: String,
    port: Int,
    emitter: NetworkEventEmitter,
    serializer: Serializer<*>,
    loggingEnabled: Boolean,
    unit: () -> Unit
) {
    private lateinit var serverSocket: ServerSocket
    private lateinit var selector: SelectorManager
    private val connections: MutableMap<Int, Socket> = mutableMapOf()
    private val pendingWrites: MutableMap<Int, ByteArray> = mutableMapOf()

    /**
     * Initializes the KonnectServer with the specified host, port, emitter, serializer, loggingEnabled, and unit.
     */
    init {
        if(loggingEnabled) println("Starting Konnect server")
        unit()
        runBlocking {
            selector = SelectorManager(Dispatchers.IO)
            serverSocket = aSocket(selector).tcp().bind(host, port)
            if (loggingEnabled) println("Listening on $host:$port\nAccepting connections...")
            while (true) {
                val socket = serverSocket.accept()
                connections[connections.size] = socket
                if (loggingEnabled) println("Accepted connection from ${socket.remoteAddress}")
                launch(Dispatchers.IO) {
                    val input = socket.openReadChannel()
                    val output = socket.openWriteChannel(autoFlush = true)
                    emitter.emitEvent(NetworkEvent.SocketConnected(socket))
                    output.writeStringUtf8("Hello from server ${socket.localAddress}\n")
                    while (true) {
                        val line = input.readUTF8Line()
                        println("Received: $line")
                    }
                }
            }
        }
    }

    /**
     * Stops the KonnectServer by closing the server socket.
     */
    fun stop() {
        serverSocket.close()
        selector.close()
        println("Stopping Konnect server")
    }
}