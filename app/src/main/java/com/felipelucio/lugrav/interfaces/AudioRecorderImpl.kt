package com.felipelucio.lugrav.interfaces

import android.media.MediaRecorder
import android.os.Build

class AudioRecorderImpl(private val context: android.content.Context) : AudioRecorder {

    private var mediaRecorder: MediaRecorder? = null

    override fun setup(
        audioSource: Int,
        outputFormat: Int,
        audioEncoder: Int,
        samplingRate: Int,
        encodingBitRate: Int,
        channels: Int,
        outputFile: String
    ) {
        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }

        mediaRecorder?.apply {
            setAudioSource(audioSource)
            setOutputFormat(outputFormat)
            setAudioEncoder(audioEncoder)
            setAudioSamplingRate(samplingRate)
            setAudioEncodingBitRate(encodingBitRate)
            setAudioChannels(channels)
            setOutputFile(outputFile)
        }
    }

    override fun prepare() {
        mediaRecorder?.prepare()
    }

    override fun start() {
        mediaRecorder?.start()
    }

    override fun stop() {
        mediaRecorder?.stop()
    }

    override fun release() {
        mediaRecorder?.release()
        mediaRecorder = null
    }
}
