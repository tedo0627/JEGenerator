package jp.tedo0627.jeloader

import aar
import aas
import aau
import abr
import acb
import acc
import adf
import adi
import adl
import adz
import aed
import af
import btj
import bwd
import bwm
import bwq
import bwu
import bxp
import bxx
import byd
import com.mojang.authlib.GameProfileRepository
import com.mojang.authlib.minecraft.MinecraftSessionService
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.serialization.Lifecycle
import cpu
import cql
import dig
import dm
import jp.tedo0627.jeloader.converter.BiomeConverter
import jp.tedo0627.jeloader.converter.BlockConverter
import jp.tedo0627.jeloader.modification.IgnoreLoggerModification
import jp.tedo0627.jeloader.modification.ProtoChunkModification
import jp.tedo0627.jeloader.modification.StoredUserListModification
import net.minecraft.server.MinecraftServer
import wv
import xc
import xk
import java.io.File
import java.net.Proxy
import java.nio.file.Paths

class JELoader {

    private var initialized = false
    private var agreedToEULA = false

    private lateinit var registryHolder: gx.b
    private lateinit var serverSettings: aau
    private lateinit var sessionService: MinecraftSessionService
    private lateinit var profileRepository: GameProfileRepository
    private lateinit var profileCache: aed
    private lateinit var storageAccess: dib.a
    private lateinit var packRepository: adi
    private lateinit var dataPackConfig: bwd
    private lateinit var serverResources: xk

    private lateinit var blockConverter: BlockConverter
    private lateinit var biomeConverter: BiomeConverter

    fun checkEula(path: String): Boolean {
        agreedToEULA = xc(Paths.get(path)).a()
        return agreedToEULA
    }

    fun init(path: String) {
        if (initialized) return

        if (!agreedToEULA) throw IllegalStateException("You must agree to eula.")

        val modifications = mutableListOf(
            IgnoreLoggerModification(af::class.java),
            IgnoreLoggerModification(dm::class.java),
            IgnoreLoggerModification(btj::class.java),
            IgnoreLoggerModification(adz::class.java),
            IgnoreLoggerModification(YggdrasilAuthenticationService::class.java),
            ProtoChunkModification(),
            StoredUserListModification(),
        )
        for (modification in modifications) modification.applyReflection()
        for (modification in modifications) modification.applyJavassist()

        ab.a()
        wy.a()
        wy.c()

        registryHolder = gx.a()
        val serverPropertiesPath = Paths.get("server.properties")
        serverSettings = aau(serverPropertiesPath)

        val file = File(path)
        val yggdrasil = YggdrasilAuthenticationService(Proxy.NO_PROXY)
        sessionService = yggdrasil.createMinecraftSessionService()
        profileRepository = yggdrasil.createProfileRepository()
        profileCache = aed(profileRepository, File(file, MinecraftServer.f.name))

        val storageSource = dib.a(file.toPath())
        val levelName = "world"
        storageAccess = storageSource.c(levelName)

        packRepository = adi(acw.b, adl(), adf(storageAccess.a(dhz.g).toFile(), adj.c))
        dataPackConfig = MinecraftServer.a(packRepository, bwd.a, false)
        val completableFuture = xk.a(packRepository.f(), registryHolder, dm.a.b, 2, ad.f(), ad.f())
        serverResources = completableFuture.get()
        serverResources.j()

        blockConverter = BlockConverter()
        biomeConverter = BiomeConverter()

        initialized = true
    }

    fun getGenerator(type: String, seed: Long, biome: String = ""): JEGenerator {
        synchronized(storageAccess) {
            if (!initialized) throw IllegalStateException("Not initialized")
            if (!agreedToEULA) throw IllegalStateException("You must agree to eula.")

            val levelSettings = bwu("world", bwn.a, false, ary.a, false, bwm(), dataPackConfig)

            val biomeRegistry = registryHolder.d(gw.aO)
            val dimensionRegistry = registryHolder.d(gw.P)
            val settingsRegistry = registryHolder.d(gw.aH)

            var targetBiomeOrNull: bxp? = null
            biomeRegistry.forEach {
                val location = biomeRegistry.b(it) ?: return@forEach
                if (location.a() == biome) targetBiomeOrNull = it
            }
            val targetBiome = targetBiomeOrNull ?: biomeRegistry.a(bxv.b)

            val mappedRegistry = cnv.a(dimensionRegistry, biomeRegistry, settingsRegistry, seed)
            val genSettings = cql(seed, true, false,
                cql.a(dimensionRegistry, mappedRegistry,
                    when (type) {
                        "OVERWORLD" -> cpu(byd(seed, false, false, biomeRegistry), seed) {
                            settingsRegistry.d(cpv.c)
                        }
                        "NETHER" -> mappedRegistry.a(cnw.c)?.c() ?: throw IllegalStateException()
                        "END" -> mappedRegistry.a(cnw.d)?.c() ?: throw IllegalStateException()
                        "LARGE_BIOMES" -> cpu(byd(seed, false, true, biomeRegistry), seed) {
                            settingsRegistry.d(cpv.c)
                        }
                        "AMPLIFIED" -> cpu(byd(seed, false, false, biomeRegistry), seed) {
                            settingsRegistry.d(cpv.d)
                        }
                        "SINGLE_BIOME" -> cpu(bxx(targetBiome), seed) {
                            settingsRegistry.d(cpv.c)
                        }
                        "CAVES" -> cpu(bxx(targetBiome), seed) {
                            settingsRegistry.d(cpv.g)
                        }
                        "FLOATING_ISLANDS" -> cpu(bxx(targetBiome), seed) {
                            settingsRegistry.d(cpv.h)
                        }
                        else -> throw IllegalArgumentException("$type is not support generator type")
                    }
                )
            )
            val chunkGenerator = genSettings.e()
            val levelData = dig(levelSettings, genSettings, Lifecycle.stable())

            val chunkProgressListener = ::acc
            val chunkProgressListenerFactory = acb { chunkProgressListener(it) }

            val server = aas(
                Thread.currentThread(), registryHolder, storageAccess,
                packRepository, serverResources, levelData, serverSettings,
                ahr.a(), sessionService, profileRepository,
                profileCache, chunkProgressListenerFactory
            )
            server.a(aar(server, registryHolder, storageAccess.b()))

            val level = abr(
                server, ad.f(), storageAccess,
                server.aV().H(), bwq.f,
                registryHolder.d(gw.P).d(cnv.k),
                chunkProgressListenerFactory.create(11), chunkGenerator,
                server.aV().A().g(),
                bxr.a(seed), mutableListOf(), true
            )

            val field = MinecraftServer::class.java.getDeclaredField("R")
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            val map = field.get(server) as MutableMap<wv<bwq>, bwq>
            map[bwq.f] = level

            return JEGenerator(level, blockConverter, biomeConverter)
        }
    }
}