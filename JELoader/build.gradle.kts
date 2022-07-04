plugins {
    kotlin("jvm") version "1.6.20"
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "jp.tedo0627.jeloader"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}