package jp.tedo0627.jeloader

import agg
import cge
import cxj
import cyc
import jp.tedo0627.jeloader.converter.BiomeConverter
import jp.tedo0627.jeloader.converter.BlockConverter

class JEGenerator(
    private val level: agg,
    private val blockConverter: BlockConverter,
    private val biomeConverter: BiomeConverter
) {

    private val status = cxn.a().apply {
        remove(cxn.k)
        remove(cxn.l)
        remove(cxn.m)
        remove(cxn.n)
        remove(cxn.o)
    }

    fun generateChunk(x: Int, z: Int): JEChunk {
        var chunk: cxj? = null
        for (chunkStats in status) {
            chunk = level.k().a(x, z, chunkStats, true) ?: continue
        }

        if (chunk == null) throw IllegalStateException()

        return JEChunk(level, chunk, blockConverter, biomeConverter)
    }

    fun populateChunk(x: Int, z: Int): Array<FeatureData> {
        val listener = PopulateFeatureListener(cge(x - 1, z - 1), blockConverter)
        val list = mutableListOf<cyc>()
        for (xx in -1..1) {
            for (zz in -1..1) {
                val chunk = level.k().a(x + xx, z + zz, cxn.c, true)
                if (chunk is cyc) {
                    list.add(chunk)
                    setListener(chunk, listener)
                }
            }
        }

        level.k().a(x, z, cxn.k, true)

        for (chunk in list) setListener(chunk, null)

        return listener.getResult()
    }

    private fun setListener(chunk: cyc, listener: PopulateFeatureListener?) {
        //val clazz = cyc::class.java
        //val field = clazz.getDeclaredField("listener")
        //field.set(chunk, listener)
    }
}