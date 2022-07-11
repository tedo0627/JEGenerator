package jp.tedo0627.jeloader

import net.minecraft.server.Eula
import java.nio.file.Paths

class JELoader {

    companion object {

        @JvmStatic
        fun sayHi() {
            println("hi")
            val eula = Eula(Paths.get("eula.txt"))
            println("eula: ${eula.hasAgreedToEULA()}")
        }

        @JvmStatic
        fun Square(i: Int): Int {
            return i * i
        }
    }

    fun init() {
        println("call kotlin init")
    }
}