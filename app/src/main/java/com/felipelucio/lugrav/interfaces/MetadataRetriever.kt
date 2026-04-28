package com.felipelucio.lugrav.interfaces

interface MetadataRetriever {
    fun setDataSource(path: String)
    fun extractDuration(): Long?
    fun release()
}
