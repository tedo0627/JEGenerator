package jp.tedo0627.jeloader

import jp.tedo0627.jeloader.converter.BlockConverter
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.chunk.ChunkAccess

class JEChunk(
    private val level: ServerLevel,
    private val chunk: ChunkAccess,
    private val blockConverter: BlockConverter,
    private val biomes: IntArray
) {

    fun getBlocks(): IntArray {
        val list = mutableListOf<Int>()
        for (x in 0 until 16) {
            for (z in 0 until 16) {
                for (y in 0 until 256) {
                    val state = chunk.getBlockState(BlockPos(x, y, z))
                    val id = blockConverter.get(state)
                    list.add(id)
                }
            }
        }

        return list.toIntArray()
    }

    fun getBiomes(): IntArray {
        return biomes
    }
}