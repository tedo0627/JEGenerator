package jp.tedo0627.jeloader

import aay
import ab
import aba
import abd
import abh
import abr
import abt
import abu
import ad
import afg
import afh
import afj
import ag
import agg
import agq
import agr
import ahm
import ahv
import ahy
import ahz
import aib
import aim
import ank
import bag
import cdq
import cgl
import cgt
import cgu
import cgx
import chb
import cht
import chv
import cia
import cie
import cig
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Lifecycle
import cys
import cyv
import dau
import daw
import dbm
import drl
import dro
import drq
import dru
import ds
import hh
import hm
import hn
import iw
import jp.tedo0627.jeloader.converter.BiomeConverter
import jp.tedo0627.jeloader.converter.BlockConverter
import jp.tedo0627.jeloader.modification.IgnoreLoggerModification
import jp.tedo0627.jeloader.modification.ProtoChunkModification
import jp.tedo0627.jeloader.modification.StoredUserListModification
import net.minecraft.server.MinecraftServer
import pu
import java.io.File
import java.net.Proxy
import java.nio.file.Paths
import java.util.*

class JELoader {

    private var initialized = false
    private var agreedToEULA = false

    private lateinit var serverSettings: afj
    private lateinit var service: abr
    private lateinit var storageAccess: drq.c
    private lateinit var packRepository: ahy
    private lateinit var registry: hn.e

    private lateinit var blockConverter: BlockConverter
    private lateinit var biomeConverter: BiomeConverter

    fun checkEula(path: String): Boolean {
        agreedToEULA = abh(Paths.get(path)).a()
        return agreedToEULA
    }

    fun init(path: String) {
        if (initialized) return

        if (!agreedToEULA) throw IllegalStateException("You must agree to eula.")

        val modifications = mutableListOf(
            IgnoreLoggerModification(ag::class.java),
            IgnoreLoggerModification(ds::class.java),
            IgnoreLoggerModification(cdq::class.java),
            //IgnoreLoggerModification(SimpleReloadableResourceManager::class.java),
            IgnoreLoggerModification(YggdrasilAuthenticationService::class.java),
            ProtoChunkModification(),
            StoredUserListModification(),
        )
        for (modification in modifications) modification.applyReflection()
        for (modification in modifications) modification.applyJavassist()

        ab.a()
        abd.a()
        abd.c()

        val serverPropertiesPath = Paths.get("server.properties")
        serverSettings = afj(serverPropertiesPath)

        val file = File(path)
        service = abr.a(YggdrasilAuthenticationService(Proxy.NO_PROXY), file)

        val storageSource = drq.a(file.toPath())
        val levelName = "world"
        storageAccess = storageSource.javaClass.getMethod("c", String::class.java).invoke(storageSource, levelName) as drq.c

        packRepository = ahy(
            ahm.a, aib(),
            ahv(storageAccess.a(dro.j).toFile(), ahz.a)
        )

        registry = hn.e()

        blockConverter = BlockConverter()
        biomeConverter = BiomeConverter()

        initialized = true
    }

    fun getGenerator(type: String, seed: Long, biome: String = ""): JEGenerator {
        synchronized(storageAccess) {
            if (!initialized) throw IllegalStateException("Not initialized")
            if (!agreedToEULA) throw IllegalStateException("You must agree to eula.")

            val biomeRegistry = registry.d(hm.aR)
            var targetBiomeOrNull: aba<cht>? = null
            biomeRegistry.forEach {
                val location = biomeRegistry.b(it) ?: return@forEach
                if (location.a() == biome) targetBiomeOrNull = biomeRegistry.c(it).get()
            }
            val targetBiome = targetBiomeOrNull ?: cia.b

            val biomeSource = cig.a.b.a(iw.j)
            val structureSets = iw.g
            val noise = iw.k
            val generatorSettings = iw.m
            val generator = when (type) {
                "OVERWORLD" -> dau(structureSets, noise, biomeSource, generatorSettings.c(daw.c))
                "NETHER" -> dau(structureSets, noise, biomeSource, generatorSettings.c(daw.f))
                "END" -> dau(structureSets, noise, biomeSource, generatorSettings.c(daw.g))
                "LARGE_BIOMES" -> dau(structureSets, noise, biomeSource, generatorSettings.c(daw.d))
                "AMPLIFIED" -> dau(structureSets, noise, biomeSource, generatorSettings.c(daw.e))
                "SINGLE_BIOME" -> dau(structureSets, noise, cie(iw.j.c(targetBiome)), generatorSettings.c(daw.c))
                "CAVES" -> dau(structureSets, noise, cie(iw.j.c(targetBiome)), generatorSettings.c(daw.h))
                "FLOATING_ISLANDS" -> dau(structureSets, noise, cie(iw.j.c(targetBiome)), generatorSettings.c(daw.i))
                else -> throw IllegalArgumentException("$type is not support generator type")
            }
            val holder = iw.b.b(cys.a).get()
            val levelStemRegistry = hh(hm.Q, Lifecycle.experimental(), null)
            levelStemRegistry.a(cyv.b, cyv(holder, generator), Lifecycle.stable())
            val worldGenSettings = dbm(seed, true, false, levelStemRegistry.k())

            val dataPackConfig = Objects.requireNonNullElse(storageAccess.d(), cgl.a) ?: throw IllegalStateException()
            val packConfig = abt.b(packRepository, dataPackConfig, false)
            val initConfig = abt.a(packConfig, ds.a.b, serverSettings.a().B)
            val worldStem = ad.b { executor ->
                abu.a(initConfig, { resourceManager: aim, dataPackConfig: cgl ->
                    val ops = aay.a(pu.a, registry, resourceManager)
                    val worldData = storageAccess.a(ops, dataPackConfig, registry.g())
                    if (worldData != null) return@a Pair.of(worldData, registry.f())

                    val levelSettings = chb("world", cgu.a, false, bag.a, false, cgt(), dataPackConfig)
                    val primaryLevelData = dru(levelSettings, worldGenSettings, Lifecycle.stable())
                    return@a Pair.of(primaryLevelData, registry.f())

                }, ad.g(), executor)
            }.get()

            val chunkProgressListener = ::agr
            val chunkProgressListenerFactory = agq { chunkProgressListener(it) }

            val server = afh(
                Thread.currentThread(), storageAccess,
                packRepository, worldStem, serverSettings,
                ank.a(), service, chunkProgressListenerFactory
            )
            server.a(afg(server, worldStem.c(), storageAccess.b()))

            val derivedLevelData = drl(server.aW(), server.aW().H())
            //val levelStem = registry.d(hm.Q).g(cyv.b)
            val levelStem = cyv(holder, generator)
            val level = agg(
                server, ad.g(), storageAccess,
                derivedLevelData, cgx.e, levelStem,
                chunkProgressListenerFactory.create(11),
                server.aW().A().g(),
                chv.a(seed), mutableListOf(), false
            )

            val field = MinecraftServer::class.java.getDeclaredField("O")
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            val map = field.get(server) as MutableMap<aba<cgx>, cgx>
            map[cgx.e] = level

            return JEGenerator(level, blockConverter, biomeConverter)
        }
    }
}