package jp.tedo0627.jeloader

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Lifecycle
import jp.tedo0627.jeloader.converter.BiomeConverter
import jp.tedo0627.jeloader.converter.BlockConverter
import jp.tedo0627.jeloader.modification.IgnoreLoggerModification
import jp.tedo0627.jeloader.modification.ProtoChunkModification
import jp.tedo0627.jeloader.modification.StoredUserListModification
import net.minecraft.SharedConstants
import net.minecraft.Util
import net.minecraft.advancements.AdvancementList
import net.minecraft.commands.Commands
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.data.BuiltinRegistries
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.RegistryOps
import net.minecraft.resources.ResourceKey
import net.minecraft.server.*
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
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.datafix.DataFixers
import net.minecraft.world.Difficulty
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.*
import net.minecraft.world.level.biome.*
import net.minecraft.world.level.biome.MultiNoiseBiomeSource.Preset
import net.minecraft.world.level.dimension.BuiltinDimensionTypes
import net.minecraft.world.level.dimension.LevelStem
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings
import net.minecraft.world.level.levelgen.WorldGenSettings
import net.minecraft.world.level.storage.DerivedLevelData
import net.minecraft.world.level.storage.LevelResource
import net.minecraft.world.level.storage.LevelStorageSource
import net.minecraft.world.level.storage.PrimaryLevelData
import java.io.File
import java.net.Proxy
import java.nio.file.Paths
import java.util.*

class JELoader {

    private var initialized = false
    private var agreedToEULA = false

    private lateinit var serverSettings: DedicatedServerSettings
    private lateinit var service: Services
    private lateinit var storageAccess: LevelStorageSource.LevelStorageAccess
    private lateinit var packRepository: PackRepository
    private lateinit var registry: RegistryAccess.Writable

    private lateinit var blockConverter: BlockConverter
    private lateinit var biomeConverter: BiomeConverter

    fun checkEula(path: String): Boolean {
        agreedToEULA = Eula(Paths.get(path)).hasAgreedToEULA()
        return agreedToEULA
    }

    fun init(path: String) {
        if (initialized) return

        if (!agreedToEULA) throw IllegalStateException("You must agree to eula.")

        val modifications = mutableListOf(
            IgnoreLoggerModification(AdvancementList::class.java),
            IgnoreLoggerModification(Commands::class.java),
            IgnoreLoggerModification(RecipeManager::class.java),
            //IgnoreLoggerModification(SimpleReloadableResourceManager::class.java),
            IgnoreLoggerModification(YggdrasilAuthenticationService::class.java),
            ProtoChunkModification(),
            StoredUserListModification(),
        )
        for (modification in modifications) modification.applyReflection()
        for (modification in modifications) modification.applyJavassist()

        SharedConstants.tryDetectVersion()
        Bootstrap.bootStrap()
        Bootstrap.validate()

        val serverPropertiesPath = Paths.get("server.properties")
        serverSettings = DedicatedServerSettings(serverPropertiesPath)

        val file = File(path)
        service = Services.create(YggdrasilAuthenticationService(Proxy.NO_PROXY), file)

        val storageSource = LevelStorageSource.createDefault(file.toPath())
        val levelName = "world"
        storageAccess = storageSource.createAccess(levelName)

        packRepository = PackRepository(
            PackType.SERVER_DATA, ServerPacksSource(),
            FolderRepositorySource(storageAccess.getLevelPath(LevelResource.DATAPACK_DIR).toFile(), PackSource.WORLD)
        )

        registry = RegistryAccess.builtinCopy()

        blockConverter = BlockConverter()
        biomeConverter = BiomeConverter()

        initialized = true
    }

    fun getGenerator(type: String, seed: Long, biome: String = ""): JEGenerator {
        synchronized(storageAccess) {
            if (!initialized) throw IllegalStateException("Not initialized")
            if (!agreedToEULA) throw IllegalStateException("You must agree to eula.")

            val biomeRegistry = registry.registryOrThrow(Registry.BIOME_REGISTRY)
            var targetBiomeOrNull: ResourceKey<Biome>? = null
            biomeRegistry.forEach {
                val location = biomeRegistry.getKey(it) ?: return@forEach
                if (location.path == biome) targetBiomeOrNull = biomeRegistry.getResourceKey(it).get()
            }
            val targetBiome = targetBiomeOrNull ?: Biomes.PLAINS

            val biomeSource = Preset.OVERWORLD.biomeSource(BuiltinRegistries.BIOME)
            val structureSets = BuiltinRegistries.STRUCTURE_SETS
            val noise = BuiltinRegistries.NOISE
            val generatorSettings = BuiltinRegistries.NOISE_GENERATOR_SETTINGS
            val generator = when (type) {
                "OVERWORLD" -> NoiseBasedChunkGenerator(structureSets, noise, biomeSource, generatorSettings.getOrCreateHolderOrThrow(NoiseGeneratorSettings.OVERWORLD))
                "NETHER" -> NoiseBasedChunkGenerator(structureSets, noise, biomeSource, generatorSettings.getOrCreateHolderOrThrow(NoiseGeneratorSettings.NETHER))
                "END" -> NoiseBasedChunkGenerator(structureSets, noise, biomeSource, generatorSettings.getOrCreateHolderOrThrow(NoiseGeneratorSettings.END))
                "LARGE_BIOMES" -> NoiseBasedChunkGenerator(structureSets, noise, biomeSource, generatorSettings.getOrCreateHolderOrThrow(NoiseGeneratorSettings.LARGE_BIOMES))
                "AMPLIFIED" -> NoiseBasedChunkGenerator(structureSets, noise, biomeSource, generatorSettings.getOrCreateHolderOrThrow(NoiseGeneratorSettings.AMPLIFIED))
                "SINGLE_BIOME" -> NoiseBasedChunkGenerator(structureSets, noise, FixedBiomeSource(BuiltinRegistries.BIOME.getOrCreateHolderOrThrow(targetBiome)), generatorSettings.getOrCreateHolderOrThrow(NoiseGeneratorSettings.OVERWORLD))
                "CAVES" -> NoiseBasedChunkGenerator(structureSets, noise, FixedBiomeSource(BuiltinRegistries.BIOME.getOrCreateHolderOrThrow(targetBiome)), generatorSettings.getOrCreateHolderOrThrow(NoiseGeneratorSettings.CAVES))
                "FLOATING_ISLANDS" -> NoiseBasedChunkGenerator(structureSets, noise, FixedBiomeSource(BuiltinRegistries.BIOME.getOrCreateHolderOrThrow(targetBiome)), generatorSettings.getOrCreateHolderOrThrow(NoiseGeneratorSettings.FLOATING_ISLANDS))
                else -> throw IllegalArgumentException("$type is not support generator type")
            }
            val holder = BuiltinRegistries.DIMENSION_TYPE.getHolder(BuiltinDimensionTypes.OVERWORLD).get()
            val levelStemRegistry = MappedRegistry(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental(), null)
            levelStemRegistry.register(LevelStem.OVERWORLD, LevelStem(holder, generator), Lifecycle.stable())
            val worldGenSettings = WorldGenSettings(seed, true, false, levelStemRegistry.freeze())

            val dataPackConfig = Objects.requireNonNullElse(storageAccess.dataPacks, DataPackConfig.DEFAULT) ?: throw IllegalStateException()
            val packConfig = WorldLoader.PackConfig(packRepository, dataPackConfig, false)
            val initConfig = WorldLoader.InitConfig(packConfig, Commands.CommandSelection.DEDICATED, serverSettings.properties.functionPermissionLevel)
            val worldStem = Util.blockUntilDone { executor ->
                WorldStem.load(initConfig, { resourceManager: ResourceManager, dataPackConfig: DataPackConfig ->
                    val ops = RegistryOps.createAndLoad(NbtOps.INSTANCE, registry, resourceManager)
                    val worldData = storageAccess.getDataTag(ops, dataPackConfig, registry.allElementsLifecycle())
                    if (worldData != null) return@load Pair.of(worldData, registry.freeze())

                    val levelSettings = LevelSettings("world", GameType.SURVIVAL, false, Difficulty.PEACEFUL, false, GameRules(), dataPackConfig)
                    val primaryLevelData = PrimaryLevelData(levelSettings, worldGenSettings, Lifecycle.stable())
                    return@load Pair.of(primaryLevelData, registry.freeze())

                }, Util.backgroundExecutor(), executor)
            }.get()

            val chunkProgressListener = ::LoggerChunkProgressListener
            val chunkProgressListenerFactory = ChunkProgressListenerFactory { chunkProgressListener(it) }

            val server = DedicatedServer(
                Thread.currentThread(), storageAccess,
                packRepository, worldStem, serverSettings,
                DataFixers.getDataFixer(), service, chunkProgressListenerFactory
            )
            server.playerList = DedicatedPlayerList(server, worldStem.registryAccess(), storageAccess.createPlayerStorage())

            val derivedLevelData = DerivedLevelData(server.worldData, server.worldData.overworldData())
            //val levelStem = registry.registryOrThrow(Registry.LEVEL_STEM_REGISTRY).getOrThrow(LevelStem.OVERWORLD)
            val levelStem = LevelStem(holder, generator)
            val level = ServerLevel(
                server, Util.backgroundExecutor(), storageAccess,
                derivedLevelData, Level.OVERWORLD, levelStem,
                chunkProgressListenerFactory.create(11),
                server.worldData.worldGenSettings().isDebug,
                BiomeManager.obfuscateSeed(seed), mutableListOf(), false
            )

            val field = MinecraftServer::class.java.getDeclaredField("levels")
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            val map = field.get(server) as MutableMap<ResourceKey<Level>, Level>
            map[Level.OVERWORLD] = level

            return JEGenerator(level, blockConverter, biomeConverter)
        }
    }
}