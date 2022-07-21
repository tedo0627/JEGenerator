package jp.tedo0627.jeloader.modification

import javassist.ClassPool
import javassist.LoaderClassPath

class StoredUserListModification : Modification {

    override fun applyJavassist() {
        val classLoader = StoredUserListModification::class.java.classLoader
        val cp = ClassPool.getDefault()
        cp.appendClassPath(LoaderClassPath(classLoader))
        val ctc = cp.get("net.minecraft.server.players.StoredUserList")
        val ctm = ctc.getDeclaredMethod("load")
        ctm.setBody("{}")
        ctc.toClass()
    }
}