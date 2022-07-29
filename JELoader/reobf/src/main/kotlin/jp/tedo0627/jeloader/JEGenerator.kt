package jp.tedo0627.jeloader

import abr
import bvv
import cmm
import cnf
import jp.tedo0627.jeloader.converter.BiomeConverter
import jp.tedo0627.jeloader.converter.BlockConverter

class JEGenerator(
    private val level: abr,
    private val blockConverter: BlockConverter,
    private val biomeConverter: BiomeConverter
) {

    private val status = cmq.a().apply {
        remove(cmq.i)
        remove(cmq.j)
        remove(cmq.k)
        remove(cmq.l)
        remove(cmq.m)
    }

    fun generateChunk(x: Int, z: Int): JEChunk {
        var chunk: cmm? = null
        for (chunkStats in status) {
            chunk = level.k().a(x, z, chunkStats, true) ?: continue
        }

        if (chunk == null) throw IllegalStateException()

        return JEChunk(level, chunk, blockConverter, biomeConverter)
    }

    fun populateChunk(x: Int, z: Int): Array<FeatureData> {
        val listener = PopulateFeatureListener(bvv(x - 1, z - 1), blockConverter)
        val list = mutableListOf<cnf>()
        for (xx in -1..1) {
            for (zz in -1..1) {
                val chunk = level.k().a(x + xx, z + zz, cmq.a, true)
                if (chunk is cnf) {
                    list.add(chunk)
                    setListener(chunk, listener)
                }
            }
        }

        level.k().a(x, z, cmq.i, true)

        for (chunk in list) setListener(chunk, null)

        return listener.getResult()
    }

    private fun setListener(chunk: cnf, listener: PopulateFeatureListener?) {
        val clazz = cnf::class.java
        val field = clazz.getDeclaredField("listener")
        field.set(chunk, listener)
    }
}