package jp.tedo0627.jeloader

import bvv
import ckt
import cnf
import gg
import jp.tedo0627.jeloader.converter.BlockConverter

class PopulateFeatureListener(
    private val chunkPos: bvv,
    private val converter: BlockConverter
) {

    private val list = mutableListOf<FeatureData>()

    fun setBlockState(chunk: cnf, pos: gg, state: ckt) {
        list.add(FeatureData(chunkPos.d(), chunkPos.e(), pos.u(), pos.v(), pos.w(), converter.get(state)))
    }

    fun getResult(): Array<FeatureData> {
        return list.toTypedArray()
    }
}