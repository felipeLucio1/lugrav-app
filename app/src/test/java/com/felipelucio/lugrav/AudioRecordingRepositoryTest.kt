package com.felipelucio.lugrav

import com.felipelucio.lugrav.interfaces.AudioPlayer
import com.felipelucio.lugrav.interfaces.AudioRecorder
import com.felipelucio.lugrav.interfaces.FileProvider
import com.felipelucio.lugrav.interfaces.MetadataRetriever
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.IOException

class AudioRecordingRepositoryTest {

    private lateinit var fileProvider: FileProvider
    private lateinit var audioRecorder: AudioRecorder
    private lateinit var audioPlayer: AudioPlayer
    private lateinit var metadataRetriever: MetadataRetriever
    private lateinit var repository: AudioRecordingRepository
    private lateinit var tempCacheDir: File
    private lateinit var tempExternalDir: File
    private val outputFileSlot = slot<String>()

    @Before
    fun setup() {
        tempCacheDir = File(System.getProperty("java.io.tmpdir"), "test_cache_${System.nanoTime()}")
        tempCacheDir.mkdirs()
        tempExternalDir = File(System.getProperty("java.io.tmpdir"), "test_external_${System.nanoTime()}")
        tempExternalDir.mkdirs()

        fileProvider = mockk(relaxed = true)
        audioRecorder = mockk(relaxed = true)
        audioPlayer = mockk(relaxed = true)
        metadataRetriever = mockk(relaxed = true)

        every { fileProvider.getCacheDir() } returns tempCacheDir
        every { fileProvider.getExternalFilesDir() } returns tempExternalDir

        every { audioRecorder.setup(any(), any(), any(), any(), any(), any(), capture(outputFileSlot)) } answers {
            val file = File(outputFileSlot.captured)
            file.parentFile?.mkdirs()
            file.createNewFile()
        }

        repository = AudioRecordingRepository(fileProvider, audioRecorder, audioPlayer, metadataRetriever)
    }

    // ==================== startRecording() Tests ====================

    @Test
    fun `when start recording then should create temp file in cache directory`() {
        repository.startRecording()

        verify { fileProvider.getCacheDir() }
        val tempFiles = tempCacheDir.listFiles { f -> f.name.startsWith("temp_audio") && f.name.endsWith(".aac") }
        assertEquals(1, tempFiles?.size ?: 0)
    }

    @Test
    fun `when start recording then should setup audio recorder with correct parameters`() {
        repository.startRecording()

        verify {
            audioRecorder.setup(
                audioSource = android.media.MediaRecorder.AudioSource.MIC,
                outputFormat = android.media.MediaRecorder.OutputFormat.AAC_ADTS,
                audioEncoder = android.media.MediaRecorder.AudioEncoder.AAC,
                samplingRate = 44100,
                encodingBitRate = 128000,
                channels = 2,
                outputFile = any()
            )
        }
    }

    @Test
    fun `when start recording then should call prepare and start on recorder`() {
        repository.startRecording()

        verifyOrder {
            audioRecorder.prepare()
            audioRecorder.start()
        }
    }

    // ==================== stopRecording() Tests ====================

    @Test
    fun `when stop recording then should stop and release audio recorder`() {
        repository.startRecording()
        repository.stopRecording()

        verifyOrder {
            audioRecorder.stop()
            audioRecorder.release()
        }
    }

    @Test
    fun `when stop recording then should read bytes from temp file`() {
        repository.startRecording()
        val tempFile = tempCacheDir.listFiles()?.first { it.name.startsWith("temp_audio") }
        tempFile?.writeBytes(byteArrayOf(1, 2, 3, 4, 5))

        val bytes = repository.stopRecording()

        assertTrue(bytes.isNotEmpty())
    }

    @Test
    fun `when stop recording then should delete temp file after reading`() {
        repository.startRecording()
        val tempFile = tempCacheDir.listFiles()?.first { it.name.startsWith("temp_audio") }

        repository.stopRecording()

        assertFalse(tempFile?.exists() ?: true)
    }

    @Test
    fun `when stop recording with null temp file then should return empty byte array`() {
        val bytes = repository.stopRecording()
        assertArrayEquals(byteArrayOf(), bytes)
    }

    // ==================== stopAndSave() Tests ====================

    @Test
    fun `when stop and save then should create app folder if not exists`() {
        val appFolder = File(tempExternalDir, "lugrav")
        if (appFolder.exists()) appFolder.deleteRecursively()

        repository.startRecording()
        repository.stopAndSave()

        assertTrue(appFolder.exists())
    }

    @Test
    fun `when stop and save then should generate file name with correct pattern`() {
        repository.startRecording()
        val filePath = repository.stopAndSave()
        val fileName = File(filePath).name

        assertTrue(fileName.startsWith("lugrav_"))
        assertTrue(fileName.endsWith(".aac"))
    }

    @Test
    fun `when stop and save then should write audio bytes to file`() {
        repository.startRecording()
        val filePath = repository.stopAndSave()
        val file = File(filePath)

        assertTrue(file.exists())
        file.delete()
    }

    @Test
    fun `when stop and save then should return absolute path of saved file`() {
        repository.startRecording()
        val filePath = repository.stopAndSave()
        val file = File(filePath)

        assertEquals(file.absolutePath, filePath)
        file.delete()
    }

    // ==================== getRecordingsList() Tests ====================

    @Test
    fun `when get recordings list and folder not exists then should return empty list`() {
        val appFolder = File(tempExternalDir, "lugrav")
        if (appFolder.exists()) appFolder.deleteRecursively()

        val list = repository.getRecordingsList()

        assertTrue(list.isEmpty())
    }

    @Test
    fun `when get recordings list then should return list of audio recording models`() {
        val appFolder = File(tempExternalDir, "lugrav")
        appFolder.mkdirs()
        File(appFolder, "test1.aac").createNewFile()
        File(appFolder, "test2.aac").createNewFile()

        val list = repository.getRecordingsList()

        assertEquals(2, list.size)
    }

    @Test
    fun `when get recordings list then should filter only aac files`() {
        val appFolder = File(tempExternalDir, "lugrav")
        appFolder.mkdirs()
        File(appFolder, "test.aac").createNewFile()
        File(appFolder, "test.txt").createNewFile()
        File(appFolder, "test.mp3").createNewFile()

        val list = repository.getRecordingsList()

        assertEquals(1, list.size)
    }

    @Test
    fun `when get recordings list then should extract duration for each file`() {
        val appFolder = File(tempExternalDir, "lugrav")
        appFolder.mkdirs()
        File(appFolder, "test.aac").createNewFile()
        every { metadataRetriever.extractDuration() } returns 10000L

        repository.getRecordingsList()

        verify {
            metadataRetriever.setDataSource(any())
            metadataRetriever.extractDuration()
            metadataRetriever.release()
        }
    }

    // ==================== playAudio() Tests ====================

    @Test
    fun `when play audio then should stop current playback before playing`() {
        val filePath = "test.aac"
        repository.playAudio(filePath)

        verifyOrder {
            audioPlayer.stop()
            audioPlayer.setDataSource(filePath)
        }
    }

    @Test
    fun `when play audio then should set data source to file path`() {
        val filePath = "test.aac"
        repository.playAudio(filePath)

        verify { audioPlayer.setDataSource(filePath) }
    }

    @Test
    fun `when play audio then should prepare and start player`() {
        val filePath = "test.aac"
        repository.playAudio(filePath)

        verifyOrder {
            audioPlayer.prepare()
            audioPlayer.start()
        }
    }

    @Test
    fun `when play audio then should set completion listener`() {
        val filePath = "test.aac"
        repository.playAudio(filePath)

        verify { audioPlayer.setOnCompletionListener(any()) }
    }

    // ==================== pauseAudio() and resumeAudio() Tests ====================

    @Test
    fun `when pause audio then should call pause on audio player`() {
        repository.pauseAudio()
        verify { audioPlayer.pause() }
    }

    @Test
    fun `when resume audio then should call start on audio player`() {
        repository.resumeAudio()
        verify { audioPlayer.start() }
    }

    // ==================== stopAudio() Test ====================

    @Test
    fun `when stop audio then should stop and release audio player`() {
        repository.stopAudio()

        verifyOrder {
            audioPlayer.stop()
            audioPlayer.release()
        }
    }

    // ==================== deleteRecording() Tests ====================

    @Test
    fun `when delete recording then should stop audio playback first`() {
        val file = File(tempExternalDir, "test_delete.aac")
        file.createNewFile()
        assertTrue(file.exists())

        repository.deleteRecording(file.absolutePath)

        verify {
            audioPlayer.stop()
            audioPlayer.release()
        }

        file.delete()
    }

    @Test
    fun `when delete recording then should delete the file`() {
        val file = File(tempExternalDir, "test_delete.aac")
        file.createNewFile()
        assertTrue(file.exists())

        repository.deleteRecording(file.absolutePath)

        assertFalse(file.exists())
    }

    @Test(expected = IOException::class)
    fun `when delete recording with non-existent file then should throw IOException`() {
        val nonExistentPath = "/non/existent/path/test.aac"
        repository.deleteRecording(nonExistentPath)
    }

    @Test(expected = IOException::class)
    fun `when delete recording fails then should throw IOException`() {
        val file = mockk<File>(relaxed = true)
        every { file.exists() } returns true
        every { file.delete() } returns false
        every { file.absolutePath } returns "/fake/path/test.aac"

        repository.deleteRecording("/fake/path/test.aac")
    }

    // ==================== getCurrentPosition() and getDuration() Tests ====================

    @Test
    fun `when get current position then should return position from player`() {
        every { audioPlayer.getCurrentPosition() } returns 5000
        val position = repository.getCurrentPosition()
        assertEquals(5000, position)
    }

    @Test
    fun `when get duration then should return duration from player`() {
        every { audioPlayer.getDuration() } returns 30000
        val duration = repository.getDuration()
        assertEquals(30000, duration)
    }

    // ==================== Error Handling Tests ====================

    @Test
    fun `when get audio duration fails then should return default 00_00_00`() {
        val appFolder = File(tempExternalDir, "lugrav")
        appFolder.mkdirs()
        File(appFolder, "test.aac").createNewFile()
        every { metadataRetriever.extractDuration() } throws Exception("Error")

        val list = repository.getRecordingsList()

        assertEquals("00:00:00", list.first().duration)
    }

    @Test
    fun `when get recordings list with null file array then should return empty list`() {
        every { fileProvider.getExternalFilesDir() } returns null
        val list = repository.getRecordingsList()
        assertTrue(list.isEmpty())
    }
}
