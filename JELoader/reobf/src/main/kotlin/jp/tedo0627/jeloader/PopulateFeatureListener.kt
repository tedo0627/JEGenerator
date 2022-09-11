package jp.tedo0627.jeloader

import cge
import cvo
import cyc
import gt
import jp.tedo0627.jeloader.converter.BlockConverter

class PopulateFeatureListener(
    private val chunkPos: cge,
    private val converter: BlockConverter
) {

    private val list = mutableListOf<FeatureData>()

    fun setBlockState(chunk: cyc, pos: gt, state: cvo) {
        val y = pos.v()
        if (y < 0 || 256 <= y) return

        list.add(FeatureData(chunkPos.d(), chunkPos.e(), pos.u(), y, pos.w(), converter.get(state)))
    }

    fun getResult(): Array<FeatureData> {
        return list.toTypedArray()
    }
}