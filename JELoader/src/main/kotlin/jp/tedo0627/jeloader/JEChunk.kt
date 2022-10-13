package jp.tedo0627.jeloader

import jp.tedo0627.jeloader.converter.BiomeConverter
import jp.tedo0627.jeloader.converter.BlockConverter
import net.minecraft.core.BlockPos
import net.minecraft.data.BuiltinRegistries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.chunk.ChunkAccess

class JEChunk(
    private val level: ServerLevel,
    private val chunk: ChunkAccess,
    private val blockConverter: BlockConverter,
    private val biomeConverter: BiomeConverter
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
        val list = mutableListOf<Int>()
        val minX = chunk.pos.minBlockX
        val minZ = chunk.pos.minBlockZ
        for (x in 0 until 16) {
            for (z in 0 until 16) {
                val biome = level.getBiome(BlockPos(minX + x, 63, minZ + z)).value()
                val location = BuiltinRegistries.BIOME.getResourceKey(biome).get()
                list.add(biomeConverter.getBiomeId(location.location().path))
            }
        }
        return list.toIntArray()
    }
}