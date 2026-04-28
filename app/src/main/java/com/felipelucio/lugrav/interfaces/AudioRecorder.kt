package com.felipelucio.lugrav.interfaces

interface AudioRecorder {
    fun setup(audioSource: Int, outputFormat: Int, audioEncoder: Int,
                samplingRate: Int, encodingBitRate: Int, channels: Int, outputFile: String)
    fun prepare()
    fun start()
    fun stop()
    fun release()
}
