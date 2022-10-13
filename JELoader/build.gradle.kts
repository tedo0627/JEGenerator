import java.io.DataInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.zip.ZipInputStream

plugins {
    kotlin("jvm") version "1.6.20"
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "jp.tedo0627.jeloader"
version = "1.0.0"

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
            if (s.startsWith("versions")) continue

            compileOnly(files(Paths.get("lib", s).toString()))
        }
    }
    compileOnly(fileTree(mapOf("dir" to "lib", "include" to "server.jar")))
}

tasks {
    register("setupMinecraft") {
        val jar = "https://piston-data.mojang.com/v1/objects/f69c284232d7c7580bd89a5a4931c3581eae1378/server.jar"
        val mapping = "https://piston-data.mojang.com/v1/objects/ed5e6e8334ad67f5af0150beed0f3d156d74bd57/server.txt"
        val remapper = "https://github.com/HeartPattern/MC-Remapper/archive/refs/heads/master.zip"

        val executePath = Paths.get(project.projectDir.toString(), "..", "setup").normalize()
        val download = { url: String, name: String ->
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.allowUserInteraction = false
            connection.instanceFollowRedirects = true
            connection.requestMethod = "GET"
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw IllegalStateException("server.jar download failed")
            }

            val inputStream = DataInputStream(connection.inputStream)
            val path = Paths.get(executePath.toString(), name)
            Files.createDirectories(path.parent)
            Files.write(path, inputStream.readAllBytes())
            inputStream.close()
        }

        println("Download server.jar")
        download(jar, "server.jar")
        println("Download MC-Remapper.zip")
        download(remapper, "MC-Remapper.zip")

        println("Unzip libraries")
        val jarzip = ZipInputStream(Files.newInputStream(Paths.get(executePath.toString(), "server.jar")))
        val files = mutableListOf("META-INF/classpath-joined", "META-INF/libraries/", "META-INF/versions/")
        while (true) {
            val entry = jarzip.nextEntry ?: break
            if (entry.isDirectory) continue

            val name = entry.name
            if (files.none { name.startsWith(it) }) continue

            val fileName = entry.name.removePrefix("META-INF/")
            val targetPath = Paths.get(project.projectDir.toString(), "lib", fileName)

            Files.createDirectories(targetPath.parent)
            val bytes = jarzip.readAllBytes()
            Files.write(targetPath, bytes)
        }
        jarzip.close()

        println("Unzip MC-Remapper.zip")
        val zip = ZipInputStream(Files.newInputStream(Paths.get(executePath.toString(), "MC-Remapper.zip")))
        while (true) {
            val entry = zip.nextEntry ?: break
            if (entry.isDirectory) continue

            val dst = Paths.get(executePath.toString(), entry.name)
            Files.createDirectories(dst.parent)
            Files.write(dst, zip.readAllBytes())
        }
        zip.close()

        println("Run MC-Remapper")
        val osCheck = System.getProperty("os.name").toLowerCase().startsWith("windows")
        val prefix = if (osCheck) "" else "./"
        val extension = if (osCheck) ".bat" else ""
        val gradlePath = Paths.get(executePath.toString(), "MC-Remapper-master")
        val binPath = Paths.get(executePath.toString(), "MC-Remapper-master", "build", "install", "MC-Remapper", "bin")
        val inputPath = Paths.get("..", "..", "..", "..", "..", "server.jar")
        val thirdCommand = if (osCheck) {
            mutableListOf("cmd", "/c", "${prefix}MC-Remapper$extension", inputPath.toString(), mapping, "--output", "deobf.jar", "--fixlocalvar=rename")
        } else {
            mutableListOf("${prefix}MC-Remapper$extension", inputPath.toString(), mapping, "--output", "deobf.jar", "--fixlocalvar=rename")
        }
        mutableListOf(
            Triple(gradlePath, mutableListOf("chmod", "+x", "gradlew"), !osCheck),
            Triple(gradlePath, mutableListOf("${prefix}gradlew$extension", "installDist"), true),
            Triple(binPath, thirdCommand, true)
        ).forEach {
            if (!it.third) return@forEach

            println("execute: ${it.second.joinToString(" ")}")
            val builder = ProcessBuilder(it.second)
            builder.redirectErrorStream(true)
            builder.directory(it.first.toFile())
            val process = builder.start()

            val inputReader = process.inputReader()
            val errorReader = process.errorReader()
            Thread().run {
                while (true) println(inputReader.readLine() ?: break)
            }
            Thread().run {
                while (true) println(errorReader.readLine() ?: break)
            }

            inputReader.close()
            errorReader.close()
            process.waitFor()
            process.destroy()
        }

        println("Copy server.jar")
        val sourcePathDev = Paths.get(binPath.toString(), "deobf.jar")
        val targetPathDev = Paths.get(project.projectDir.toString(), "lib", "server.jar")
        Files.createDirectories(targetPathDev.parent)
        Files.copy(sourcePathDev, targetPathDev, StandardCopyOption.REPLACE_EXISTING)
    }
}