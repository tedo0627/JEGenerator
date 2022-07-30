plugins {
    kotlin("jvm") version "1.6.20"
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "jp.tedo0627.jeloader.dev"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.javassist:javassist:3.29.0-GA")
    implementation(fileTree(mapOf("dir" to "lib", "include" to "server.jar")))
}

tasks {
    shadowJar {
        exclude("server.jar")
    }
}