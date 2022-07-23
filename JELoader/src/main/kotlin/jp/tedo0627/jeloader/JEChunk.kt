package jp.tedo0627.jeloader

import jp.tedo0627.jeloader.converter.BiomeConverter
import jp.tedo0627.jeloader.converter.BlockConverter
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
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
                    val name = Registry.BLOCK.getKey(state.block).toString()
                    val id = blockConverter.get(name)
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
                val biome = level.getBiomeName(BlockPos(minX + x, 0, minZ + z))
                list.add(biomeConverter.getBiomeId(biome.get().location().path))
            }
        }
        return list.toIntArray()
    }
}