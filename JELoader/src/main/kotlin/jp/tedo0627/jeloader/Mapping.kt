package jp.tedo0627.jeloader

class Mapping {

    companion object {
        // net.minecraft.world.level.chunk.ProtoChunk
        const val CLASS_ProtoChunk = "cnf"
        const val METHOD_ProtoChunk_setBlockState = "a"

        // net.minecraft.server.players.StoredUserList
        const val CLASS_StoredUserList = "aem"
        const val METHOD_StoredUserList_save = "e"
        const val METHOD_StoredUserList_load = "f"

        const val FIELD_MinecraftServer_levels = "R"
    }
}
