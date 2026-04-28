package com.felipelucio.lugrav.interfaces

import android.media.MediaMetadataRetriever

class MetadataRetrieverImpl : MetadataRetriever {
    private val retriever = MediaMetadataRetriever()
    
    override fun setDataSource(path: String) {
        retriever.setDataSource(path)
    }
    
    override fun extractDuration(): Long? {
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
    }
    
    override fun release() {
        retriever.release()
    }
}
