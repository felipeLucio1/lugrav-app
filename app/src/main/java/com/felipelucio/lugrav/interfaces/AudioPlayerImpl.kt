package com.felipelucio.lugrav.interfaces

import android.media.MediaPlayer

class AudioPlayerImpl : AudioPlayer {
    private var player: MediaPlayer? = null
    
    override fun setDataSource(path: String) {
        player = MediaPlayer().apply {
            setDataSource(path)
        }
    }
    
    override fun prepare() { player?.prepare() }
    override fun start() { player?.start() }
    override fun pause() { player?.pause() }
    override fun stop() { player?.stop() }
    override fun release() { 
        player?.release() 
        player = null
    }
    
    override fun setOnCompletionListener(listener: () -> Unit) {
        player?.setOnCompletionListener { 
            listener()
            player?.release()
            player = null
        }
    }
    
    override fun getCurrentPosition(): Int = player?.currentPosition ?: 0
    override fun getDuration(): Int = player?.duration ?: 0
}
