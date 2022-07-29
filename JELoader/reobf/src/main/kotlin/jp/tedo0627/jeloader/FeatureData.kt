package jp.tedo0627.jeloader

class FeatureData(
    private val minX: Int,
    private val minZ: Int,
    private val x: Int,
    private val y: Int,
    private val z: Int,
    val value: Int
) {
    fun getIndex(): Int {
        val x = x - minX
        val z = z - minZ

        return x.shl(16) + z.shl(8) + y
    }
}