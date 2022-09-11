import java.nio.file.Files
import java.nio.file.Paths

plugins {
    kotlin("jvm") version "1.6.20"
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "jp.tedo0627.jeloader.reobf"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.javassist:javassist:3.29.0-GA")

    val path = Paths.get(project.projectDir.toString(), "lib", "classpath-joined")
    if (Files.exists(path)) {
        val str = Files.readString(path)
        for (s in str.split(";")) {
            compileOnly(files(Paths.get("lib", s).toString()))
        }
    }
}