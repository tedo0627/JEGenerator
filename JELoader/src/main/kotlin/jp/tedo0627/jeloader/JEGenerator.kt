package jp.tedo0627.jeloader

import com.mojang.datafixers.util.Either
import jp.tedo0627.jeloader.converter.BiomeConverter
import jp.tedo0627.jeloader.converter.BlockConverter
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ChunkHolder
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.ChunkStatus
import java.util.*
import java.util.concurrent.CompletableFuture

class JEGenerator(
    private val level: ServerLevel,
    private val blockConverter: BlockConverter,
    private val biomeConverter: BiomeConverter,
    private val thread: GeneratorThread
) {

    fun generateChunk(x: Int, z: Int) {
        val completableFuture = CompletableFuture<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>>()
        thread.addTask {
            completableFuture.complete(level.chunkSource.getChunkFuture(x, z, ChunkStatus.FULL, true))
        }
        completableFuture.get()
    }

    fun populateChunk(x: Int, z: Int): JEChunk {
        val completableFuture = CompletableFuture<ChunkResult>()
        thread.addTask {
            val queue = LinkedList<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>>()
            for (xx in -1..1) {
                for (zz in -1..1) {
                    if (xx == 0 && zz == 0) continue
                    queue.add(level.chunkSource.getChunkFuture(x + xx, z + zz, ChunkStatus.FULL, true))
                }
            }

            val targetChunkFuture = level.chunkSource.getChunkFuture(x, z, ChunkStatus.FULL, true)
            queue.add(targetChunkFuture)
            val aroundChunkFuture = combineFuture(queue)
            val result = ChunkResult(aroundChunkFuture, targetChunkFuture)
            completableFuture.complete(result)
        }
        val chunk = completableFuture.get().get()

        val biomeFuture = CompletableFuture<IntArray>()
        thread.addTask {
            val list = mutableListOf<Int>()
            val minX = chunk.pos.minBlockX
            val minZ = chunk.pos.minBlockZ
            for (xx in 0 until 16) {
                for (zz in 0 until 16) {
                    val biome = level.getBiomeName(BlockPos(minX + x, 63, minZ + z))
                    list.add(biomeConverter.getBiomeId(biome.get().location().path))
                }
            }
            biomeFuture.complete(list.toIntArray())
        }
        return JEChunk(level, chunk, blockConverter, biomeFuture.get())
    }

    private fun <T> combineFuture(queue: LinkedList<CompletableFuture<T>>): CompletableFuture<T> {
        val future = queue.poll()
        while (queue.isNotEmpty()) future.thenCombine(queue.poll()) { _, _ -> }
        return future
    }

    class ChunkResult(
        private val aroundChunkFuture: CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>,
        private val targetChunkFuture: CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>
    ) {

        fun get(): ChunkAccess {
            aroundChunkFuture.join()
            return targetChunkFuture.join().left().get()
        }
    }
}