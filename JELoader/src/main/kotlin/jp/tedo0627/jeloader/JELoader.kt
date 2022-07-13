package jp.tedo0627.jeloader

import com.mojang.authlib.GameProfileRepository
import com.mojang.authlib.minecraft.MinecraftSessionService
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import net.minecraft.SharedConstants
import net.minecraft.Util
import net.minecraft.commands.Commands
import net.minecraft.core.RegistryAccess
import net.minecraft.server.Bootstrap
import net.minecraft.server.Eula
import net.minecraft.server.MinecraftServer
import net.minecraft.server.ServerResources
import net.minecraft.server.dedicated.DedicatedServerSettings
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.repository.FolderRepositorySource
import net.minecraft.server.packs.repository.PackRepository
import net.minecraft.server.packs.repository.PackSource
import net.minecraft.server.packs.repository.ServerPacksSource
import net.minecraft.server.players.GameProfileCache
import net.minecraft.world.level.DataPackConfig
import net.minecraft.world.level.storage.LevelResource
import net.minecraft.world.level.storage.LevelStorageSource
import java.io.File
import java.net.Proxy
import java.nio.file.Paths

class JELoader {

    companion object {

        @JvmStatic
        fun sayHi() {
            println("hi")
        }

        @JvmStatic
        fun Square(i: Int): Int {
            return i * i
        }
    }

    private var initialized = false
    private var agreedToEULA = false

    private lateinit var registryHolder: RegistryAccess.RegistryHolder
    private lateinit var serverSettings: DedicatedServerSettings
    private lateinit var sessionService: MinecraftSessionService
    private lateinit var profileRepository: GameProfileRepository
    private lateinit var profileCache: GameProfileCache
    private lateinit var storageAccess: LevelStorageSource.LevelStorageAccess
    private lateinit var packRepository: PackRepository
    private lateinit var dataPackConfig: DataPackConfig
    private lateinit var serverResources: ServerResources


    fun checkEula(): Boolean {
        agreedToEULA = Eula(Paths.get("eula.txt")).hasAgreedToEULA()
        return agreedToEULA
    }

    fun init() {
        if (initialized) return

        if (!agreedToEULA) throw IllegalStateException("You must agree to eula.")

        SharedConstants.tryDetectVersion()
        Bootstrap.bootStrap()
        Bootstrap.validate()

        registryHolder = RegistryAccess.builtin()
        val serverPropertiesPath = Paths.get("server.properties")
        serverSettings = DedicatedServerSettings(serverPropertiesPath)

        val file = File("jeloader") // TODO
        val yggdrasil = YggdrasilAuthenticationService(Proxy.NO_PROXY)
        sessionService = yggdrasil.createMinecraftSessionService()
        profileRepository = yggdrasil.createProfileRepository()
        profileCache = GameProfileCache(profileRepository, File(file, MinecraftServer.USERID_CACHE_FILE.name))

        val storageSource = LevelStorageSource.createDefault(file.toPath())
        val levelName = "world"
        storageAccess = storageSource.createAccess(levelName)

        packRepository = PackRepository(
            PackType.SERVER_DATA,
            ServerPacksSource(),
            FolderRepositorySource(storageAccess.getLevelPath(LevelResource.DATAPACK_DIR).toFile(), PackSource.WORLD)
        )
        dataPackConfig = MinecraftServer.configurePackRepository(packRepository, DataPackConfig.DEFAULT, false)
        val completableFuture = ServerResources.loadResources(packRepository.openAllSelected(), registryHolder, Commands.CommandSelection.DEDICATED, 2, Util.backgroundExecutor(), Util.backgroundExecutor())
        serverResources = completableFuture.get()
        serverResources.updateGlobals()

        initialized = true
    }
}