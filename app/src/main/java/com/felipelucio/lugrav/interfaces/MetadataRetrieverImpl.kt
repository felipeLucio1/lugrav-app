package com.felipelucio.lugrav.interfaces

import android.media.MediaMetadataRetriever

class MetadataRetrieverImpl : MetadataRetriever {
    private var retriever: MediaMetadataRetriever? = null

    override fun setDataSource(path: String) {
        release()
        val newRetriever = MediaMetadataRetriever()
        try {
            newRetriever.setDataSource(path)
            retriever = newRetriever
        } catch (e: Exception) {
            runCatching { newRetriever.release() }
            throw e
        }
    }

    override fun extractDuration(): Long? {
        return try {
            retriever
                ?.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull()
        } catch (e: Exception) {
            null
        }
    }

    override fun release() {
        retriever?.let { runCatching { it.release() } }
        retriever = null
    }
}
