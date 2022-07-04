package jp.tedo0627.jeloader

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

    fun init() {
        println("call kotlin init")
    }
}