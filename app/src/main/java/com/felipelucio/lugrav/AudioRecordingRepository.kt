package com.felipelucio.lugrav

import com.felipelucio.lugrav.interfaces.AudioPlayer
import com.felipelucio.lugrav.interfaces.AudioRecorder
import com.felipelucio.lugrav.interfaces.FileProvider
import com.felipelucio.lugrav.interfaces.MetadataRetriever
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AudioRecordingRepository(
    private val fileProvider: FileProvider,
    private val audioRecorder: AudioRecorder,
    private val audioPlayer: AudioPlayer,
    private val metadataRetriever: MetadataRetriever
) {

    private var tempFile: File? = null

    companion object {
        private const val APP_FOLDER = "lugrav"
        private const val DATE_FORMAT = "yyyyMMdd_HHmmss"
        private const val FILE_EXTENSION = ".aac"
    }

    fun startRecording() {
        tempFile = File(fileProvider.getCacheDir(), "temp_audio_${System.currentTimeMillis()}.aac")

        audioRecorder.setup(
            audioSource = android.media.MediaRecorder.AudioSource.MIC,
            outputFormat = android.media.MediaRecorder.OutputFormat.AAC_ADTS,
            audioEncoder = android.media.MediaRecorder.AudioEncoder.AAC,
            samplingRate = 44100,
            encodingBitRate = 128000,
            channels = 2,
            outputFile = tempFile?.absolutePath ?: ""
        )
        audioRecorder.prepare()
        audioRecorder.start()
    }

    fun stopRecording(): ByteArray {
        audioRecorder.stop()
        audioRecorder.release()

        val audioBytes = tempFile?.let { file ->
            FileInputStream(file).use { it.readBytes() }
        } ?: byteArrayOf()

        tempFile?.delete()
        tempFile = null

        return audioBytes
    }

    fun stopAndSave(): String {
        val audioBytes = stopRecording()

        val baseDir = fileProvider.getExternalFilesDir()
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

    fun getRecordingsList(): List<AudioRecordingModel> {
        val baseDir = fileProvider.getExternalFilesDir()
        val appFolder = File(baseDir, APP_FOLDER)

        if (!appFolder.exists()) {
            return emptyList()
        }

        val files = appFolder.listFiles { file ->
            file.extension == "aac"
        } ?: return emptyList()

        return files.map { file ->
            val duration = getAudioDuration(file.absolutePath)
            AudioRecordingModel(
                path = file.absolutePath,
                duration = duration
            )
        }
    }

    fun playAudio(filePath: String, onCompletion: () -> Unit = {}) {
        audioPlayer.stop()
        audioPlayer.setDataSource(filePath)
        audioPlayer.prepare()
        audioPlayer.start()
        audioPlayer.setOnCompletionListener {
            onCompletion()
        }
    }

    fun pauseAudio() {
        audioPlayer.pause()
    }

    fun resumeAudio() {
        audioPlayer.start()
    }

    fun stopAudio() {
        audioPlayer.stop()
        audioPlayer.release()
    }

    @Throws(IOException::class)
    fun deleteRecording(filePath: String) {
        stopAudio()

        val file = File(filePath)
        if (!file.exists()) {
            throw IOException("File does not exist: $filePath")
        }

        if (!file.delete()) {
            throw IOException("Failed to delete file: $filePath")
        }
    }

    fun getCurrentPosition(): Int = audioPlayer.getCurrentPosition()

    fun getDuration(): Int = audioPlayer.getDuration()

    private fun getAudioDuration(filePath: String): String {
        return try {
            metadataRetriever.setDataSource(filePath)
            val durationMs = metadataRetriever.extractDuration() ?: 0L
            metadataRetriever.release()
            formatDuration(durationMs)
        } catch (e: Exception) {
            "00:00:00"
        }
    }

    private fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
