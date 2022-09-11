package jp.tedo0627.jeloader

import agg
import cxj
import gt
import iw
import jp.tedo0627.jeloader.converter.BiomeConverter
import jp.tedo0627.jeloader.converter.BlockConverter

class JEChunk(
    private val level: agg,
    private val chunk: cxj,
    private val blockConverter: BlockConverter,
    private val biomeConverter: BiomeConverter
) {

    fun getBlocks(): IntArray {
        val list = mutableListOf<Int>()
        for (x in 0 until 16) {
            for (z in 0 until 16) {
                for (y in 0 until 256) {
                    val state = chunk.a_(gt(x, y, z))
                    val id = blockConverter.get(state)
                    list.add(id)
                }
            }
        }

        return list.toIntArray()
    }

    fun getBiomes(): IntArray {
        val list = mutableListOf<Int>()
        val minX = chunk.f().d()
        val minZ = chunk.f().e()
        for (x in 0 until 16) {
            for (z in 0 until 16) {
                val biome = level.w(gt(minX + x, 63, minZ + z)).a()
                val location = iw.j.c(biome).get()
                list.add(biomeConverter.getBiomeId(location.a().a()))
            }
        }
        return list.toIntArray()
    }
}