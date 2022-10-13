package jp.tedo0627.jeloader.modification

import javassist.ClassPool
import javassist.LoaderClassPath
import jp.tedo0627.jeloader.Mapping

class StoredUserListModification : Modification {

    override fun applyJavassist() {
        val classLoader = StoredUserListModification::class.java.classLoader
        val cp = ClassPool.getDefault()
        cp.appendClassPath(LoaderClassPath(classLoader))
        val ctc = cp.get(Mapping.CLASS_StoredUserList) //net.minecraft.server.players.StoredUserList

        val ctmSave = ctc.getDeclaredMethod(Mapping.METHOD_StoredUserList_save) // save
        ctmSave.setBody("{}")

        val ctmLoad = ctc.getDeclaredMethod(Mapping.METHOD_StoredUserList_load) // load
        ctmLoad.setBody("{}")

        ctc.toClass()
    }
}