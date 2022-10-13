package jp.tedo0627.jeloader.converter

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class BiomeConverter {

    private val biomeMap = mutableMapOf<String, Int>()

    init {
        val input = javaClass.getResourceAsStream("/biome_table.json")
        if (input != null) {
            val jsonObject = GsonBuilder().setLenient().create().fromJson(InputStreamReader(input, StandardCharsets.UTF_8), JsonObject::class.java)
            for (entry in jsonObject.entrySet()) {
                biomeMap[entry.key] = entry.value.asInt
            }
        }
    }

    fun getBiomeId(name: String): Int {
        return biomeMap[name] ?: 0
    }

    fun getBiome(id: Int): String {
        for (entry in biomeMap) {
            if (entry.value != id) continue

            return entry.key
        }

        return ""
    }
}