package jp.tedo0627.jeloader

import abr
import cmm
import gg
import jp.tedo0627.jeloader.converter.BiomeConverter
import jp.tedo0627.jeloader.converter.BlockConverter

class JEChunk(
    private val level: abr,
    private val chunk: cmm,
    private val blockConverter: BlockConverter,
    private val biomeConverter: BiomeConverter
) {

    fun getBlocks(): IntArray {
        val list = mutableListOf<Int>()
        for (x in 0 until 16) {
            for (z in 0 until 16) {
                for (y in 0 until 256) {
                    val state = chunk.a_(gg(x, y, z))
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
                val biome = level.j(gg(minX + x, 0, minZ + z))
                list.add(biomeConverter.getBiomeId(biome.get().a().a()))
            }
        }
        return list.toIntArray()
    }
}