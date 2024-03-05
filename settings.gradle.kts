pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "konnect"
include("konnect-client")
include("konnect-server")
include("konnect-core")
include("konnect-test")
