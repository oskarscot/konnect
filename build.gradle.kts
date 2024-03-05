plugins {
    kotlin("jvm") version "1.9.0"
    `java-library`
}

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "java-library")

    group = "scot.oskar"
    version = "0.0.1-alpha"

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("io.ktor:ktor-network:2.3.8")
    }

}




