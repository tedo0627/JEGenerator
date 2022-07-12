package jp.tedo0627.jeloader

import net.minecraft.server.Eula
import java.nio.file.Paths

class JELoader {

    companion object {

        @JvmStatic
        fun sayHi() {
            println("hi")
        }

        @JvmStatic
        fun Square(i: Int): Int {
            return i * i
        }
    }

    private var agreedToEULA = false

    fun checkEula(): Boolean {
        agreedToEULA = Eula(Paths.get("eula.txt")).hasAgreedToEULA()
        return agreedToEULA
    }

    fun init() {
        println("call kotlin init")
    }
}