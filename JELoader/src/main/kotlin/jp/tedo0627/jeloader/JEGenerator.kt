package jp.tedo0627.jeloader

import jp.tedo0627.jeloader.converter.BlockConverter
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.ChunkStatus

class JEGenerator(
    private val level: ServerLevel,
    private val blockConverter: BlockConverter
) {

    private val status = ChunkStatus.getStatusList().apply {
        remove(ChunkStatus.LIGHT)
        remove(ChunkStatus.SPAWN)
        remove(ChunkStatus.HEIGHTMAPS)
        remove(ChunkStatus.FULL)
    }

    fun generateChunk(x: Int, z: Int): JEChunk {
        println("call generate chunk1 thread: ${Thread.currentThread().id}")
        var chunk: ChunkAccess? = null
        for (chunkStats in status) {
            chunk = level.chunkSource.getChunk(x, z, chunkStats, true) ?: continue
        }

        if (chunk == null) throw IllegalStateException()

        println("call generate chunk2")

        return JEChunk(chunk, blockConverter)
    }
}