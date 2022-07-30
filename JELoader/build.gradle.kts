import java.io.DataInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.zip.ZipInputStream

group = "jp.tedo0627.jeloader"

allprojects {
    version = "1.0.0"
}

tasks {
    register("setupMinecraft") {
        val jar = "https://launcher.mojang.com/v1/objects/a16d67e5807f57fc4e550299cf20226194497dc2/server.jar"
        val mapping = "https://launcher.mojang.com/v1/objects/f6cae1c5c1255f68ba4834b16a0da6a09621fe13/server.txt"
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
        mutableMapOf(
            gradlePath to mutableListOf("${prefix}gradlew$extension", "installDist"),
            binPath to mutableListOf("cmd", "/c", "${prefix}MC-Remapper$extension", inputPath.toString(), mapping, "--output", "deobf.jar", "--fixlocalvar=rename")
        ).forEach {
            val builder = ProcessBuilder(it.value)
            builder.redirectErrorStream(true)
            builder.directory(it.key.toFile())
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
        val targetPathDev = Paths.get(project.projectDir.toString(), "dev", "lib", "server.jar")
        Files.createDirectories(targetPathDev.parent)
        Files.copy(sourcePathDev, targetPathDev, StandardCopyOption.REPLACE_EXISTING)

        val sourcePathReobf = Paths.get(executePath.toString(), "server.jar")
        val targetPathReobf = Paths.get(project.projectDir.toString(), "reobf", "lib", "server.jar")
        Files.createDirectories(targetPathReobf.parent)
        Files.copy(sourcePathReobf, targetPathReobf, StandardCopyOption.REPLACE_EXISTING)
    }
}