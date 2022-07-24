package jp.tedo0627.jeloader

import jp.tedo0627.jeloader.converter.BlockConverter
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.ProtoChunk

class PopulateFeatureListener(
    private val chunkPos: ChunkPos,
    private val converter: BlockConverter
) {

    private val list = mutableListOf<FeatureData>()

    fun setBlockState(chunk: ProtoChunk, pos: BlockPos, state: BlockState) {
        val name = Registry.BLOCK.getKey(state.block).toString()
        list.add(FeatureData(chunkPos.minBlockX, chunkPos.minBlockZ, pos.x, pos.y, pos.z, converter.get(name)))
    }

    fun getResult(): Array<FeatureData> {
        return list.toTypedArray()
    }
}