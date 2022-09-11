package jp.tedo0627.jeloader.modification

import javassist.ClassPool
import javassist.CtField
import javassist.LoaderClassPath

class ProtoChunkModification : Modification {

    override fun applyJavassist() {
        /*
        val classLoader = ProtoChunkModification::class.java.classLoader
        val cp = ClassPool.getDefault()
        cp.appendClassPath(LoaderClassPath(classLoader))
        val ctc = cp.get("cyc")
        ctc.addField(CtField.make("public jp.tedo0627.jeloader.PopulateFeatureListener listener = null;", ctc))
        val ctm = ctc.getDeclaredMethod("a")
        ctm.insertBefore("if (listener != null) listener.setBlockState($0, $1, $2);")
        ctc.toClass()

         */
    }
}