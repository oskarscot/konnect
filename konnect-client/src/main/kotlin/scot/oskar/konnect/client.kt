package scot.oskar.konnect

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import scot.oskar.konnect.events.NetworkEventEmitter
import scot.oskar.konnect.events.NetworkEventListener
import scot.oskar.konnect.serializer.ByteArraySerializer
import scot.oskar.konnect.serializer.Serializer

/**
 * Creates a KonnectClient with the specified port, host, and configuration options.
 *
 * @param port The port number for the client to connect to. Default is 54666.
 * @param host The host address for the client to connect to. Default is "127.0.0.1".
 * @param block Configuration block for the KonnectClientBuilder to apply additional settings.
 * @return A KonnectClient instance configured according to the provided parameters and block.
 */
fun konnectClient(port: Int = 54666, host: String = "127.0.0.1", block: KonnectClientBuilder.() -> Unit): KonnectClient {
    val builder = KonnectClientBuilder(port, host)
    builder.block()
    return builder.build()
}

/**
 * Builder class for configuring a KonnectClient instance.
 *
 * @property port The port number for the client.
 * @property host The host address for the client.
 */
class KonnectClientBuilder(private val port: Int, private val host: String) {
    private var serializer: Serializer = ByteArraySerializer()
    private var loggingEnabled: Boolean = false
    private val listeners = mutableListOf<NetworkEventListener>()

    /**
     * Registers a custom serializer for the client.
     *
     * @param serializer The serializer to be registered.
     * @return This KonnectClientBuilder instance for method chaining.
     */
    fun registerSerializer(serializer: Serializer) = apply { this.serializer = serializer }

    /**
     * Enables logging for the client.
     *
     * @return This KonnectClientBuilder instance for method chaining.
     */
    fun enableLogging() = apply { this.loggingEnabled = true }

    /**
     * Registers a listener for network events.
     *
     * @param listener The listener to be registered.
     * @return This KonnectClientBuilder instance for method chaining.
     */
    fun registerListener(listener: NetworkEventListener) = apply { listeners.add(listener) }

    /**
     * Builds a KonnectClient instance with the configured settings.
     *
     * @return A KonnectClient instance.
     */
    fun build(): KonnectClient {
        val emitter = NetworkEventEmitter()
        val unit: () -> Unit = {
            registerSerializer(serializer)
            if (loggingEnabled) enableLogging()
            listeners.forEach { emitter.addListener(it) }
        }
        return KonnectClient(host, port, emitter, serializer, loggingEnabled, unit)
    }
}

/**
 * Represents a KonnectClient instance.
 *
 * @property host The host address for the client.
 * @property port The port number for the client.
 * @property emitter The network event emitter for the client.
 * @property serializer The serializer used by the client.
 * @property loggingEnabled Indicates whether logging is enabled for the client.
 * @property unit The initialization unit for the client.
 */
class KonnectClient internal constructor(
    private val host: String,
    private val port: Int,
    private val emitter: NetworkEventEmitter,
    private val serializer: Serializer,
    private val loggingEnabled: Boolean,
    unit: () -> Unit
) {
    private var selectorManager: SelectorManager
    private var clientSocket: Socket

    init {
        unit()
        runBlocking {
            selectorManager = SelectorManager(Dispatchers.IO)
            clientSocket = aSocket(selectorManager).tcp().connect(host, port)
            if (loggingEnabled) println("Connected to server: $host:$port")
            val input = clientSocket.openReadChannel()
            val output = clientSocket.openWriteChannel(autoFlush = true)
            launch(Dispatchers.IO) {
                output.writeStringUtf8("Hello from client: ${clientSocket.localAddress}\n")
                while (true) {
                    val message = input.readUTF8Line()
                    println("Received: $message")
                }
            }
        }
    }

    /**
     * Stops the KonnectClient by closing the client socket and selector manager.
     */
    fun stop() {
        selectorManager.close()
        clientSocket.close()
        println("Connection closed")
    }
}