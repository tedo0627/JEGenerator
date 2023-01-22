package jp.tedo0627.jeloader.modification

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.Logger
import java.lang.reflect.Field

class IgnoreLoggerModification(private val clazz: Class<*>) : Modification {

    override fun applyReflection() {
        var targetField: Field? = null
        for (field in clazz.declaredFields) {
            if (field.type != Logger::class.java) continue

            targetField = field
            break
        }
        if (targetField == null) return

        targetField.isAccessible = true
        val logger = targetField.get(null) as Logger
        logger.level = Level.OFF
    }
}