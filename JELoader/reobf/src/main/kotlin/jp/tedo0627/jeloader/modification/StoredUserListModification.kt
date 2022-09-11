package jp.tedo0627.jeloader.modification

import javassist.ClassPool
import javassist.LoaderClassPath

class StoredUserListModification : Modification {

    override fun applyJavassist() {
        val classLoader = StoredUserListModification::class.java.classLoader
        val cp = ClassPool.getDefault()
        cp.appendClassPath(LoaderClassPath(classLoader))
        val ctc = cp.get("aje")

        val ctmSave = ctc.getDeclaredMethod("e")
        ctmSave.setBody("{}")

        val ctmLoad = ctc.getDeclaredMethod("f")
        ctmLoad.setBody("{}")

        ctc.toClass()
    }
}