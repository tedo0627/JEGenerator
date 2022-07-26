package jp.tedo0627.jeloader.modification

import javassist.ClassPool
import javassist.LoaderClassPath

class StoredUserListModification : Modification {

    override fun applyJavassist() {
        val classLoader = StoredUserListModification::class.java.classLoader
        val cp = ClassPool.getDefault()
        cp.appendClassPath(LoaderClassPath(classLoader))
        val ctc = cp.get("net.minecraft.server.players.StoredUserList")

        val ctmSave = ctc.getDeclaredMethod("save")
        ctmSave.setBody("{}")

        val ctmLoad = ctc.getDeclaredMethod("load")
        ctmLoad.setBody("{}")

        ctc.toClass()
    }
}