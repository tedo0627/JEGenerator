package jp.tedo0627.jeloader.converter

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class BlockConverter {

    private val map = mutableMapOf<String, Pair<Int, Int>>()

    init {
        val input = javaClass.getResourceAsStream("/block_table.json")
        if (input != null) {
            val jsonArray = GsonBuilder().setLenient().create().fromJson(InputStreamReader(input, StandardCharsets.UTF_8), JsonArray::class.java)
            for (element in jsonArray) {
                val obj = element.asJsonObject
                val name = obj["name"].asString
                map[name] = Pair(obj["id"].asInt, obj["damage"].asInt)
            }
        }
    }

    fun get(name: String): Int {
        return map[name]?.first ?: 0
    }
}