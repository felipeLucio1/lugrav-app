package com.felipelucio.lugrav.interfaces

interface AudioPlayer {
    fun setDataSource(path: String)
    fun prepare()
    fun start()
    fun pause()
    fun stop()
    fun release()
    fun setOnCompletionListener(listener: () -> Unit)
    fun getCurrentPosition(): Int
    fun getDuration(): Int
}
