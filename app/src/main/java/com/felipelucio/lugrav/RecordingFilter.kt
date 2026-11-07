package com.felipelucio.lugrav

fun interface RecordingFilter {
    fun matches(recording: AudioRecordingModel): Boolean

    companion object {
        val All = RecordingFilter { true }
    }
}
