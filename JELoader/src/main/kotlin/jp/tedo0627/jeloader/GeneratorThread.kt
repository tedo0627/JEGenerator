package jp.tedo0627.jeloader

import java.util.concurrent.ConcurrentLinkedQueue

class GeneratorThread : Thread() {

    private val tasks = ConcurrentLinkedQueue<Runnable>()

    override fun run() {
        while (true) {
            if (tasks.isEmpty()) {
                sleep(10)
                continue
            }
            tasks.poll().run()
        }
    }

    fun addTask(task: Runnable) {
        tasks.add(task)
    }
}