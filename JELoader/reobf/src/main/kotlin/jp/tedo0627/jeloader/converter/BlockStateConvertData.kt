package jp.tedo0627.jeloader.converter

class BlockStateConvertData(
    val id: Int,
    val damage: Int,
    private val properties: Map<String, BlockPropertyConverter>
) {
    fun getPropertyConverter(name: String): BlockPropertyConverter? {
        return properties[name]
    }
}