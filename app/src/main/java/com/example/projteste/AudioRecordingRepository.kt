package com.example.projteste

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AudioRecordingRepository(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var tempFile: File? = null

    companion object {
        private const val APP_FOLDER = "lugrav"
        private const val DATE_FORMAT = "yyyyMMdd_HHmmss"
        private const val FILE_EXTENSION = ".aac"
    }

    suspend fun startRecording() {
        tempFile = File(context.cacheDir, "temp_audio_${System.currentTimeMillis()}.aac")

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(44100)
            setAudioEncodingBitRate(128000)
            setAudioChannels(2) // estéreo
            setOutputFile(tempFile?.absolutePath)

            prepare()
            start()
        }
    }

    suspend fun stopRecording(): ByteArray {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null

        val audioBytes = tempFile?.let { file ->
            FileInputStream(file).use { it.readBytes() }
        } ?: byteArrayOf()

        tempFile?.delete()
        tempFile = null

        return audioBytes
    }

    suspend fun stopAndSave(): String {
        val audioBytes = stopRecording()

        val baseDir = context.getExternalFilesDir(null)
        val appFolder = File(baseDir, APP_FOLDER)

        if (!appFolder.exists()) {
            appFolder.mkdirs()
        }

        val timestamp = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
        val fileName = buildString {
            append(APP_FOLDER)
            append("_")
            append(timestamp)
            append(FILE_EXTENSION)
        }
        val outputFile = File(appFolder, fileName)

        outputFile.writeBytes(audioBytes)

        return outputFile.absolutePath
    }

    suspend fun getRecordingsList(): List<String> {
        val baseDir = context.getExternalFilesDir(null)
        val appFolder = File(baseDir, APP_FOLDER)

        if (!appFolder.exists()) {
            return emptyList()
        }

        return appFolder.listFiles { file ->
            file.extension == "aac"
        }?.map { it.absolutePath } ?: emptyList()
    }
}