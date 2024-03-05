Simple and easy to use networking library. It is designed to be used with Kotlin and Coroutines.
## Konnect is:
- **Simple**: Konnect wraps around Ktor Sockets and provides a simple and easy to use API, simply create a server and connect to it with the client.
- **Lightweight**: The library is designed to be lightweight and easy to use with the only dependency being ktor using the `ktor-network` module and it's sockets.
## Features

- **Send TCP and UDP packets**: Send and receive TCP and UDP packets with ease.
- **Modular**: Konnect is designed to be modular and easy to use, with the ability to customise everything to your liking.

## Installation
Add the following to your `build.gradle.kts` file:
```kotlin
//TODO
```

## Usage

### Server
```kotlin
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
```

### Client
```kotlin
fun main() {
    konnectClient(port = 54666, host = "localhost") {
        enableLogging()
        registerListener(SocketConnectedListener())
    }
}

//TODO: Add more examples
```


