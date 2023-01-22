package jp.tedo0627.jeloader

import jp.tedo0627.jeloader.converter.BiomeConverter
import jp.tedo0627.jeloader.converter.BlockConverter
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.ChunkStatus
import net.minecraft.world.level.chunk.ProtoChunk

class JEGenerator(
    private val level: ServerLevel,
    private val blockConverter: BlockConverter,
    private val biomeConverter: BiomeConverter
) {

    private val status = ChunkStatus.getStatusList().apply {
        remove(ChunkStatus.FEATURES)
        remove(ChunkStatus.LIGHT)
        remove(ChunkStatus.SPAWN)
        remove(ChunkStatus.HEIGHTMAPS)
        remove(ChunkStatus.FULL)
    }

    fun generateChunk(x: Int, z: Int): JEChunk {
        var chunk: ChunkAccess? = null
        for (chunkStats in status) {
            chunk = level.chunkSource.getChunk(x, z, chunkStats, true) ?: continue
        }

        if (chunk == null) throw IllegalStateException()

        return JEChunk(level, chunk, blockConverter, biomeConverter)
    }

    fun populateChunk(x: Int, z: Int): Array<FeatureData> {
        val listener = PopulateFeatureListener(ChunkPos(x - 1, z - 1), blockConverter)
        val list = mutableListOf<ProtoChunk>()
        for (xx in -1..1) {
            for (zz in -1..1) {
                val chunk = level.chunkSource.getChunk(x + xx, z + zz, ChunkStatus.EMPTY, true)
                if (chunk is ProtoChunk) {
                    list.add(chunk)
                    setListener(chunk, listener)
                }
            }
        }

        level.chunkSource.getChunk(x, z, ChunkStatus.FEATURES, true)

        for (chunk in list) setListener(chunk, null)

        return listener.getResult()
    }

    private fun setListener(chunk: ProtoChunk, listener: PopulateFeatureListener?) {
        val clazz = ProtoChunk::class.java
        val field = clazz.getDeclaredField("listener")
        field.set(chunk, listener)
    }
}