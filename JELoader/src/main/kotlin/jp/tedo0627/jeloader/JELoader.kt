package jp.tedo0627.jeloader

import com.mojang.authlib.GameProfileRepository
import com.mojang.authlib.minecraft.MinecraftSessionService
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.serialization.Lifecycle
import jp.tedo0627.jeloader.converter.BlockConverter
import jp.tedo0627.jeloader.modification.IgnoreLoggerModification
import jp.tedo0627.jeloader.modification.StoredUserListModification
import net.minecraft.SharedConstants
import net.minecraft.Util
import net.minecraft.advancements.AdvancementList
import net.minecraft.commands.Commands
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceKey
import net.minecraft.server.Bootstrap
import net.minecraft.server.Eula
import net.minecraft.server.MinecraftServer
import net.minecraft.server.ServerResources
import net.minecraft.server.dedicated.DedicatedPlayerList
import net.minecraft.server.dedicated.DedicatedServer
import net.minecraft.server.dedicated.DedicatedServerSettings
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.progress.ChunkProgressListenerFactory
import net.minecraft.server.level.progress.LoggerChunkProgressListener
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.repository.FolderRepositorySource
import net.minecraft.server.packs.repository.PackRepository
import net.minecraft.server.packs.repository.PackSource
import net.minecraft.server.packs.repository.ServerPacksSource
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager
import net.minecraft.server.players.GameProfileCache
import net.minecraft.util.datafix.DataFixers
import net.minecraft.world.Difficulty
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.*
import net.minecraft.world.level.biome.*
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.dimension.LevelStem
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings
import net.minecraft.world.level.levelgen.WorldGenSettings
import net.minecraft.world.level.storage.LevelResource
import net.minecraft.world.level.storage.LevelStorageSource
import net.minecraft.world.level.storage.PrimaryLevelData
import java.io.File
import java.net.Proxy
import java.nio.file.Paths

class JELoader {

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

    private lateinit var blockConverter: BlockConverter

    fun checkEula(): Boolean {
        agreedToEULA = Eula(Paths.get("eula.txt")).hasAgreedToEULA()
        return agreedToEULA
    }

    fun init() {
        if (initialized) return

        if (!agreedToEULA) throw IllegalStateException("You must agree to eula.")

        val modifications = mutableListOf(
            IgnoreLoggerModification(AdvancementList::class.java),
            IgnoreLoggerModification(Commands::class.java),
            IgnoreLoggerModification(RecipeManager::class.java),
            IgnoreLoggerModification(SimpleReloadableResourceManager::class.java),
            IgnoreLoggerModification(YggdrasilAuthenticationService::class.java),
            StoredUserListModification(),
        )
        for (modification in modifications) modification.applyReflection()
        for (modification in modifications) modification.applyJavassist()

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

        blockConverter = BlockConverter()

        initialized = true
    }

    fun getGenerator(type: String, seed: Long, biome: String = ""): JEGenerator {
        println("call kotlin getGenerator thread: ${Thread.currentThread().id}, seed: $seed")
        try {
            synchronized(storageAccess) {

                if (!initialized) throw IllegalStateException("Not initialized")
                if (!agreedToEULA) throw IllegalStateException("You must agree to eula.")

                val levelSettings = LevelSettings(
                    "world",
                    GameType.SURVIVAL,
                    false,
                    Difficulty.PEACEFUL,
                    false,
                    GameRules(),
                    dataPackConfig
                )

                val biomeRegistry = registryHolder.registryOrThrow(Registry.BIOME_REGISTRY)
                val dimensionRegistry = registryHolder.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY)
                val settingsRegistry = registryHolder.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY)

                var targetBiomeOrNull: Biome? = null
                biomeRegistry.forEach {
                    val location = biomeRegistry.getKey(it) ?: return@forEach
                    if (location.path == biome) targetBiomeOrNull = it
                }
                val targetBiome = targetBiomeOrNull ?: biomeRegistry.get(Biomes.PLAINS)!!

                val mappedRegistry =
                    DimensionType.defaultDimensions(dimensionRegistry, biomeRegistry, settingsRegistry, seed)
                val genSettings = WorldGenSettings(seed, true, false,
                    WorldGenSettings.withOverworld(dimensionRegistry, mappedRegistry,
                        when (type) {
                            "OVERWORLD" -> NoiseBasedChunkGenerator(
                                OverworldBiomeSource(seed, false, false, biomeRegistry),
                                seed
                            ) {
                                settingsRegistry.getOrThrow(NoiseGeneratorSettings.OVERWORLD)
                            }
                            "NETHER" -> mappedRegistry.get(LevelStem.NETHER)?.generator() ?: throw IllegalStateException()
                            "END" -> mappedRegistry.get(LevelStem.END)?.generator() ?: throw IllegalStateException()
                            "LARGE_BIOMES" -> NoiseBasedChunkGenerator(
                                OverworldBiomeSource(
                                    seed,
                                    false,
                                    true,
                                    biomeRegistry
                                ), seed
                            ) {
                                settingsRegistry.getOrThrow(NoiseGeneratorSettings.OVERWORLD)
                            }
                            "AMPLIFIED" -> NoiseBasedChunkGenerator(
                                OverworldBiomeSource(seed, false, false, biomeRegistry),
                                seed
                            ) {
                                settingsRegistry.getOrThrow(NoiseGeneratorSettings.AMPLIFIED)
                            }
                            "SINGLE_BIOME" -> NoiseBasedChunkGenerator(FixedBiomeSource(targetBiome), seed) {
                                settingsRegistry.getOrThrow(NoiseGeneratorSettings.OVERWORLD)
                            }
                            "CAVES" -> NoiseBasedChunkGenerator(FixedBiomeSource(targetBiome), seed) {
                                settingsRegistry.getOrThrow(NoiseGeneratorSettings.CAVES)
                            }
                            "FLOATING_ISLANDS" -> NoiseBasedChunkGenerator(FixedBiomeSource(targetBiome), seed) {
                                settingsRegistry.getOrThrow(NoiseGeneratorSettings.FLOATING_ISLANDS)
                            }
                            else -> throw IllegalArgumentException("$type is not support generator type")
                        }
                    )
                )
                val chunkGenerator = genSettings.overworld()
                val levelData = PrimaryLevelData(levelSettings, genSettings, Lifecycle.stable())

                val chunkProgressListener = ::LoggerChunkProgressListener
                val chunkProgressListenerFactory = ChunkProgressListenerFactory { chunkProgressListener(it) }

                val server = DedicatedServer(
                    Thread.currentThread(), registryHolder, storageAccess,
                    packRepository, serverResources, levelData, serverSettings,
                    DataFixers.getDataFixer(), sessionService, profileRepository,
                    profileCache, chunkProgressListenerFactory
                )
                server.playerList = DedicatedPlayerList(server, registryHolder, storageAccess.createPlayerStorage())

                val level = ServerLevel(
                    server, Util.backgroundExecutor(), storageAccess,
                    server.worldData.overworldData(), Level.OVERWORLD,
                    registryHolder.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY)
                        .getOrThrow(DimensionType.OVERWORLD_LOCATION),
                    chunkProgressListenerFactory.create(11), chunkGenerator,
                    server.worldData.worldGenSettings().isDebug,
                    BiomeManager.obfuscateSeed(seed), mutableListOf(), true
                )

                val field = MinecraftServer::class.java.getDeclaredField("levels")
                field.isAccessible = true
                @Suppress("UNCHECKED_CAST")
                val map = field.get(server) as MutableMap<ResourceKey<Level>, Level>
                map[Level.OVERWORLD] = level

                println("kotlin getGenerator finish")
                return JEGenerator(level, blockConverter)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        throw IllegalStateException()
    }
}