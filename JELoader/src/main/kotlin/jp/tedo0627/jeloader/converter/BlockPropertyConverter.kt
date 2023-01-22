package jp.tedo0627.jeloader.converter

import com.google.gson.JsonObject
import net.minecraft.world.level.block.state.BlockState

class BlockPropertyConverter(json: JsonObject) {

    val name = json["name"].asString
    private val type = json["type"].asString
    private val value = json["value"].asJsonObject

    fun getDamage(state: BlockState, id: Int, damage: Int): Pair<Int, Int> {
        for (property in state.properties) {
            if (property.name != name) continue

            val propertyValue = state.getValue(property).toString()
            val element = value[propertyValue]
            if (element is JsonObject) {
                val newId = element["id"].asInt
                val changeDamage = element["damage"].asInt

                return Pair(newId, when (type) {
                    "add_damage" -> damage + changeDamage
                    "set_damage" -> changeDamage
                    else -> throw IllegalStateException()
                })
            }

            val changeDamage = value[propertyValue].asInt
            return Pair(id, when (type) {
                "add_damage" -> damage + changeDamage
                "set_damage" -> changeDamage
                else -> throw IllegalStateException()
            })
        }

        throw IllegalStateException()
    }
}