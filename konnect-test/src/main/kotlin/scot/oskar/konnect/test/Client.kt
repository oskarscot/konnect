package scot.oskar.konnect.test

import scot.oskar.konnect.konnectClient
fun main() {
    konnectClient(port = 54666, host = "localhost") {
        enableLogging()
    }
}
