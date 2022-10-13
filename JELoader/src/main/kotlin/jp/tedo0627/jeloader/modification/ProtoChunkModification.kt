package jp.tedo0627.jeloader.modification

import javassist.ClassPool
import javassist.CtField
import javassist.LoaderClassPath
import jp.tedo0627.jeloader.Mapping

class ProtoChunkModification : Modification {

    override fun applyJavassist() {
        val classLoader = ProtoChunkModification::class.java.classLoader
        val cp = ClassPool.getDefault()
        cp.appendClassPath(LoaderClassPath(classLoader))
        val ctc = cp.get(Mapping.CLASS_ProtoChunk) // net.minecraft.world.level.chunk.ProtoChunk
        ctc.addField(CtField.make("public jp.tedo0627.jeloader.PopulateFeatureListener listener = null;", ctc))
        val ctm = ctc.getDeclaredMethod(Mapping.METHOD_ProtoChunk_setBlockState) // setBlockState
        ctm.insertBefore("if (listener != null) listener.setBlockState($0, $1, $2);")
        ctc.toClass()
    }
}