package jp.tedo0627.jeloader

import jp.tedo0627.jeloader.converter.BlockConverter
import net.minecraft.core.BlockPos
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.ProtoChunk

class PopulateFeatureListener(
    private val chunkPos: ChunkPos,
    private val converter: BlockConverter
) {

    private val list = mutableListOf<FeatureData>()

    fun setBlockState(chunk: ProtoChunk, pos: BlockPos, state: BlockState) {
        val y = pos.y
        if (y < 0 || 256 <= y) return

        list.add(FeatureData(chunkPos.minBlockX, chunkPos.minBlockZ, pos.x, y, pos.z, converter.get(state)))
    }

    fun getResult(): Array<FeatureData> {
        return list.toTypedArray()
    }
}