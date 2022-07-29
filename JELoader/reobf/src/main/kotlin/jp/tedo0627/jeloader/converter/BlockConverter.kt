package jp.tedo0627.jeloader.converter

import ckt
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class BlockConverter {

    private val map = mutableMapOf<String, BlockStateConvertData>()

    init {
        val input = javaClass.getResourceAsStream("/block_table.json")
        if (input != null) {
            val jsonArray = GsonBuilder().setLenient().create().fromJson(InputStreamReader(input, StandardCharsets.UTF_8), JsonArray::class.java)
            for (element in jsonArray) {
                val obj = element.asJsonObject

                val properties = mutableMapOf<String, BlockPropertyConverter>()
                val propertiesJson = obj["properties"]
                if (propertiesJson != null && propertiesJson.isJsonArray) {
                    for (jsonObject in propertiesJson.asJsonArray) {
                        if (jsonObject !is JsonObject) continue

                        val converter = BlockPropertyConverter(jsonObject)
                        properties[converter.name] = converter
                    }
                }

                val name = obj["name"].asString
                map[name] = BlockStateConvertData(obj["id"].asInt, obj["damage"].asInt, properties)
            }
        }
    }

    fun get(state: ckt): Int {
        val name = gw.W.b(state.b()).toString()
        val data = map[name] ?: return 0

        var id = data.id
        var damage = data.damage
        for (property in state.s()) {
            val converter = data.getPropertyConverter(property.f()) ?: continue
            val pair = converter.getDamage(state, id, damage)
            id = pair.first
            damage = pair.second
        }

        if (damage >= 16) damage %= 16
        return id.shl(4) + damage
    }
}