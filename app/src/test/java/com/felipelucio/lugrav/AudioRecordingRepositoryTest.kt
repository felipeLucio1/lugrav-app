package com.felipelucio.lugrav

import com.felipelucio.lugrav.data.room.RecordingMetadata
import com.felipelucio.lugrav.data.room.RecordingTagStore
import com.felipelucio.lugrav.interfaces.AudioPlayer
import com.felipelucio.lugrav.interfaces.AudioRecorder
import com.felipelucio.lugrav.interfaces.FileProvider
import com.felipelucio.lugrav.interfaces.MetadataRetriever
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.IOException

class AudioRecordingRepositoryTest {

    private lateinit var fileProvider: FileProvider
    private lateinit var audioRecorder: AudioRecorder
    private lateinit var audioPlayer: AudioPlayer
    private lateinit var metadataRetriever: MetadataRetriever
    private lateinit var recordingTagStore: FakeRecordingTagStore
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
        recordingTagStore = FakeRecordingTagStore()

        every { fileProvider.getCacheDir() } returns tempCacheDir
        every { fileProvider.getExternalFilesDir() } returns tempExternalDir

        every { audioRecorder.setup(any(), any(), any(), any(), any(), any(), capture(outputFileSlot)) } answers {
            val file = File(outputFileSlot.captured)
            file.parentFile?.mkdirs()
            file.createNewFile()
        }

        repository = AudioRecordingRepository(
            fileProvider,
            audioRecorder,
            audioPlayer,
            metadataRetriever,
            recordingTagStore
        )
    }

    @After
    fun tearDown() {
        tempCacheDir.deleteRecursively()
        tempExternalDir.deleteRecursively()
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
    fun `when stop and save then should create app folder if not exists`() = runTest {
        val appFolder = File(tempExternalDir, "lugrav")
        if (appFolder.exists()) appFolder.deleteRecursively()

        repository.startRecording()
        repository.stopAndSave()

        assertTrue(appFolder.exists())
    }

    @Test
    fun `when stop and save then should generate file name with correct pattern`() = runTest {
        repository.startRecording()
        val filePath = repository.stopAndSave()
        val fileName = File(filePath).name

        assertTrue(fileName.startsWith("lugrav_"))
        assertTrue(fileName.endsWith(".aac"))
    }

    @Test
    fun `when stop and save then should write audio bytes to file`() = runTest {
        repository.startRecording()
        val filePath = repository.stopAndSave()
        val file = File(filePath)

        assertTrue(file.exists())
        file.delete()
    }

    @Test
    fun `when stop and save then should return absolute path of saved file`() = runTest {
        repository.startRecording()
        val filePath = repository.stopAndSave()
        val file = File(filePath)

        assertEquals(file.absolutePath, filePath)
        file.delete()
    }

    @Test
    fun `when stop and save then should persist recording metadata`() = runTest {
        repository.startRecording()
        val filePath = repository.stopAndSave()

        assertTrue(recordingTagStore.recordings.containsKey(filePath))
        assertEquals(filePath, recordingTagStore.recordings.getValue(filePath).path)
    }

    // ==================== getRecordingsList() Tests ====================

    @Test
    fun `when get recordings list and folder not exists then should return empty list`() = runTest {
        val appFolder = File(tempExternalDir, "lugrav")
        if (appFolder.exists()) appFolder.deleteRecursively()

        val list = repository.getRecordingsList()

        assertTrue(list.isEmpty())
    }

    @Test
    fun `when get recordings list then should return list of audio recording models`() = runTest {
        val appFolder = File(tempExternalDir, "lugrav")
        appFolder.mkdirs()
        File(appFolder, "test1.aac").createNewFile()
        File(appFolder, "test2.aac").createNewFile()

        val list = repository.getRecordingsList()

        assertEquals(2, list.size)
    }

    @Test
    fun `when get recordings list then should filter only aac files`() = runTest {
        val appFolder = File(tempExternalDir, "lugrav")
        appFolder.mkdirs()
        File(appFolder, "test.aac").createNewFile()
        File(appFolder, "test.txt").createNewFile()
        File(appFolder, "test.mp3").createNewFile()

        val list = repository.getRecordingsList()

        assertEquals(1, list.size)
    }

    @Test
    fun `when get recordings list then should extract duration for each file`() = runTest {
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

    @Test
    fun `when get recordings list with multiple files then should extract duration for each file`() = runTest {
        val appFolder = File(tempExternalDir, "lugrav")
        appFolder.mkdirs()
        File(appFolder, "test1.aac").createNewFile()
        File(appFolder, "test2.aac").createNewFile()
        every { metadataRetriever.extractDuration() } returns 10000L

        repository.getRecordingsList()

        verify(exactly = 2) { metadataRetriever.setDataSource(any()) }
        verify(exactly = 2) { metadataRetriever.extractDuration() }
        verify(exactly = 2) { metadataRetriever.release() }
    }

    @Test
    fun `when duration is 65000 milliseconds then should format as 00_01_05`() = runTest {
        val appFolder = File(tempExternalDir, "lugrav")
        appFolder.mkdirs()
        File(appFolder, "test.aac").createNewFile()
        every { metadataRetriever.extractDuration() } returns 65000L

        val list = repository.getRecordingsList()

        assertEquals("00:01:05", list.first().duration)
    }

    @Test
    fun `when get recordings list then should use last modified as created at`() = runTest {
        val appFolder = File(tempExternalDir, "lugrav")
        appFolder.mkdirs()
        val file = File(appFolder, "test.aac")
        file.createNewFile()
        assertTrue(file.setLastModified(1_700_000_000_000L))
        every { metadataRetriever.extractDuration() } returns 10000L

        val list = repository.getRecordingsList()

        assertEquals(1_700_000_000_000L, list.first().createdAtMillis)
    }

    @Test
    fun `when get recordings list then should sort by last modified descending`() = runTest {
        val appFolder = File(tempExternalDir, "lugrav")
        appFolder.mkdirs()
        val older = File(appFolder, "older.aac").apply {
            createNewFile()
            setLastModified(1_700_000_000_000L)
        }
        val newer = File(appFolder, "newer.aac").apply {
            createNewFile()
            setLastModified(1_700_000_100_000L)
        }
        every { metadataRetriever.extractDuration() } returns 10000L

        val list = repository.getRecordingsList()

        assertEquals(listOf(newer.absolutePath, older.absolutePath), list.map { it.path })
    }

    @Test
    fun `when recordings have same last modified then should sort by path ascending`() = runTest {
        val appFolder = File(tempExternalDir, "lugrav")
        appFolder.mkdirs()
        val sameTimestamp = 1_700_000_000_000L
        val first = File(appFolder, "a.aac").apply {
            createNewFile()
            setLastModified(sameTimestamp)
        }
        val second = File(appFolder, "b.aac").apply {
            createNewFile()
            setLastModified(sameTimestamp)
        }
        every { metadataRetriever.extractDuration() } returns 10000L

        val list = repository.getRecordingsList()

        assertEquals(listOf(first.absolutePath, second.absolutePath), list.map { it.path })
    }

    @Test
    fun `when get recordings list then should sync file metadata with store`() = runTest {
        val appFolder = File(tempExternalDir, "lugrav")
        appFolder.mkdirs()
        val file = File(appFolder, "test.aac").apply {
            createNewFile()
            setLastModified(1_700_000_000_000L)
        }
        every { metadataRetriever.extractDuration() } returns 10000L

        repository.getRecordingsList()

        assertEquals(1, recordingTagStore.syncedRecordings.size)
        val metadata = recordingTagStore.syncedRecordings.first()
        assertEquals(file.absolutePath, metadata.path)
        assertEquals("00:00:10", metadata.duration)
        assertEquals(1_700_000_000_000L, metadata.createdAtMillis)
    }

    @Test
    fun `when stored recording has no file then sync should remove it`() = runTest {
        repository.startRecording()
        val stalePath = repository.stopAndSave()
        assertTrue(File(stalePath).delete())

        val appFolder = File(tempExternalDir, "lugrav")
        File(appFolder, "current.aac").createNewFile()

        val list = repository.getRecordingsList()

        assertFalse(recordingTagStore.recordings.containsKey(stalePath))
        assertEquals(1, list.size)
    }

    @Test
    fun `when get recordings list then should return tags from store`() = runTest {
        repository.startRecording()
        val path = repository.stopAndSave()
        val tag = repository.createTag("Trabalho", "#29B6F6")
        repository.setRecordingTags(path, setOf(tag.name))

        val list = repository.getRecordingsList()

        assertEquals(listOf(tag), list.single { it.path == path }.tags)
    }

    @Test
    fun `when filtering recordings by one tag then should return only tagged recordings`() = runTest {
        val appFolder = File(tempExternalDir, "lugrav")
        appFolder.mkdirs()
        val tagged = File(appFolder, "tagged.aac").apply {
            createNewFile()
            setLastModified(1_700_000_000_000L)
        }
        File(appFolder, "untagged.aac").apply {
            createNewFile()
            setLastModified(1_700_000_100_000L)
        }
        every { metadataRetriever.extractDuration() } returns 10000L

        repository.getRecordingsList()
        repository.createTag("Trabalho", "#29B6F6")
        repository.setRecordingTags(tagged.absolutePath, setOf("Trabalho"))

        val filtered = repository.getRecordingsList(setOf("Trabalho"))

        assertEquals(listOf(tagged.absolutePath), filtered.map { it.path })
    }

    @Test
    fun `when filtering recordings by multiple tags then should require all tags`() = runTest {
        val appFolder = File(tempExternalDir, "lugrav")
        appFolder.mkdirs()
        val bothTags = File(appFolder, "both.aac").apply {
            createNewFile()
            setLastModified(1_700_000_000_000L)
        }
        val onlyWork = File(appFolder, "work.aac").apply {
            createNewFile()
            setLastModified(1_700_000_100_000L)
        }
        every { metadataRetriever.extractDuration() } returns 10000L

        repository.getRecordingsList()
        repository.createTag("Trabalho", "#29B6F6")
        repository.createTag("Importante", "#FF0000")
        repository.setRecordingTags(bothTags.absolutePath, setOf("Trabalho", "Importante"))
        repository.setRecordingTags(onlyWork.absolutePath, setOf("Trabalho"))

        val filtered = repository.getRecordingsList(setOf("Trabalho", "Importante"))

        assertEquals(listOf(bothTags.absolutePath), filtered.map { it.path })
    }

    @Test
    fun `when filtering recordings by unknown tag then should return empty list`() = runTest {
        val appFolder = File(tempExternalDir, "lugrav")
        appFolder.mkdirs()
        File(appFolder, "test.aac").createNewFile()
        every { metadataRetriever.extractDuration() } returns 10000L

        repository.getRecordingsList()

        val filtered = repository.getRecordingsList(setOf("Inexistente"))

        assertTrue(filtered.isEmpty())
    }

    @Test
    fun `when get recordings by tags then should filter without resyncing files`() = runTest {
        val appFolder = File(tempExternalDir, "lugrav")
        appFolder.mkdirs()
        val tagged = File(appFolder, "tagged.aac").apply {
            createNewFile()
            setLastModified(1_700_000_000_000L)
        }
        File(appFolder, "untagged.aac").createNewFile()
        every { metadataRetriever.extractDuration() } returns 10000L

        repository.getRecordingsList()
        val syncedRecordings = recordingTagStore.syncedRecordings
        repository.createTag("Trabalho", "#29B6F6")
        repository.setRecordingTags(tagged.absolutePath, setOf("Trabalho"))

        val filtered = repository.getRecordingsByTags(setOf("Trabalho"))

        assertEquals(listOf(tagged.absolutePath), filtered.map { it.path })
        assertEquals(syncedRecordings, recordingTagStore.syncedRecordings)
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
    fun `when delete recording then should stop audio playback first`() = runTest {
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
    fun `when delete recording then should delete the file`() = runTest {
        val file = File(tempExternalDir, "test_delete.aac")
        file.createNewFile()
        assertTrue(file.exists())

        repository.deleteRecording(file.absolutePath)

        assertFalse(file.exists())
    }

    @Test(expected = IOException::class)
    fun `when delete recording with non-existent file then should throw IOException`() = runTest {
        val nonExistentPath = "/non/existent/path/test.aac"
        repository.deleteRecording(nonExistentPath)
    }

    @Test(expected = IOException::class)
    fun `when delete recording fails then should throw IOException`() = runTest {
        val nonEmptyDir = File(tempExternalDir, "non_empty_dir")
        nonEmptyDir.mkdirs()
        File(nonEmptyDir, "child.aac").createNewFile()

        repository.deleteRecording(nonEmptyDir.absolutePath)
    }

    @Test
    fun `when delete recording succeeds then should remove stored recording`() = runTest {
        repository.startRecording()
        val path = repository.stopAndSave()

        repository.deleteRecording(path)

        assertFalse(recordingTagStore.recordings.containsKey(path))
    }

    // ==================== renameRecording() Tests ====================

    @Test
    fun `when rename recording then should rename file and preserve content`() = runTest {
        val appFolder = File(tempExternalDir, "lugrav")
        appFolder.mkdirs()
        val source = File(appFolder, "original.aac")
        val content = byteArrayOf(1, 2, 3, 4, 5)
        source.writeBytes(content)
        repository.getRecordingsList()

        val newPath = repository.renameRecording(source.absolutePath, "Renomeada")
        val renamed = File(newPath)

        assertTrue(renamed.exists())
        assertFalse(source.exists())
        assertEquals("Renomeada.aac", renamed.name)
        assertArrayEquals(content, renamed.readBytes())
    }

    @Test
    fun `when rename recording with extension then should not duplicate extension`() = runTest {
        repository.startRecording()
        val sourcePath = repository.stopAndSave()

        val newPath = repository.renameRecording(sourcePath, "Renomeada.aac")

        assertEquals("Renomeada.aac", File(newPath).name)
    }

    @Test
    fun `when rename recording to same name then should return same path`() = runTest {
        val source = File(tempExternalDir, "original.aac")
        source.writeBytes(byteArrayOf(1))

        val newPath = repository.renameRecording(source.absolutePath, "original")

        assertEquals(source.absolutePath, newPath)
        assertTrue(source.exists())
    }

    @Test(expected = IOException::class)
    fun `when rename recording with empty name then should throw IOException`() = runTest {
        val source = File(tempExternalDir, "original.aac")
        source.createNewFile()

        repository.renameRecording(source.absolutePath, "")
    }

    @Test(expected = IOException::class)
    fun `when rename recording with invalid characters then should throw IOException`() = runTest {
        val source = File(tempExternalDir, "original.aac")
        source.createNewFile()

        repository.renameRecording(source.absolutePath, "nome/invalido")
    }

    @Test(expected = IOException::class)
    fun `when rename recording to existing name then should throw IOException`() = runTest {
        val source = File(tempExternalDir, "original.aac")
        val target = File(tempExternalDir, "Renomeada.aac")
        source.createNewFile()
        target.createNewFile()

        repository.renameRecording(source.absolutePath, "Renomeada")
    }

    @Test(expected = IOException::class)
    fun `when rename recording non-existent file then should throw IOException`() = runTest {
        repository.renameRecording(File(tempExternalDir, "missing.aac").absolutePath, "Renomeada")
    }

    @Test
    fun `when rename recording succeeds then should update stored path`() = runTest {
        repository.startRecording()
        val sourcePath = repository.stopAndSave()

        val newPath = repository.renameRecording(sourcePath, "Renomeada")

        assertFalse(recordingTagStore.recordings.containsKey(sourcePath))
        assertTrue(recordingTagStore.recordings.containsKey(newPath))
    }

    // ==================== Tag Operations Tests ====================

    @Test
    fun `when create tag then should persist normalized tag`() = runTest {
        val tag = repository.createTag(" Trabalho ", "#29b6f6")

        assertEquals(UserTag("Trabalho", "#29B6F6"), tag)
        assertEquals(tag, recordingTagStore.tags["Trabalho"])
    }

    @Test(expected = IllegalArgumentException::class)
    fun `when create duplicate tag then should fail`() = runTest {
        repository.createTag("Trabalho", "#29B6F6")

        repository.createTag("Trabalho", "#29B6F6")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `when create duplicate tag with different case then should fail`() = runTest {
        repository.createTag("Trabalho", "#29B6F6")

        repository.createTag("  trabalho  ", "#29B6F6")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `when create tag with invalid color then should fail`() = runTest {
        repository.createTag("Trabalho", "blue")
    }

    @Test
    fun `when get tags then should return stored tags`() = runTest {
        repository.createTag("Pessoal", "#FF0000")
        repository.createTag("Trabalho", "#29B6F6")

        val tags = repository.getTags()

        assertEquals(2, tags.size)
        assertTrue(tags.contains(UserTag("Pessoal", "#FF0000")))
        assertTrue(tags.contains(UserTag("Trabalho", "#29B6F6")))
    }

    @Test
    fun `when update tag then should persist changes`() = runTest {
        repository.createTag("Trabalho", "#29B6F6")

        val updated = repository.updateTag("Trabalho", "Pessoal", "#ff0000")

        assertEquals(UserTag("Pessoal", "#FF0000"), updated)
        assertFalse(recordingTagStore.tags.containsKey("Trabalho"))
        assertEquals(updated, recordingTagStore.tags["Pessoal"])
    }

    @Test
    fun `when delete tag then should remove tag`() = runTest {
        repository.createTag("Trabalho", "#29B6F6")

        repository.deleteTag("Trabalho")

        assertFalse(recordingTagStore.tags.containsKey("Trabalho"))
    }

    @Test
    fun `when set recording tags then should replace associations`() = runTest {
        repository.startRecording()
        val path = repository.stopAndSave()
        repository.createTag("Trabalho", "#29B6F6")
        repository.createTag("Pessoal", "#FF0000")
        repository.setRecordingTags(path, setOf("Pessoal"))

        repository.setRecordingTags(path, setOf("Trabalho"))

        assertEquals(setOf("Trabalho"), recordingTagStore.associations[path])
    }

    @Test
    fun `when set recording tags with empty set then should remove associations`() = runTest {
        repository.startRecording()
        val path = repository.stopAndSave()
        repository.createTag("Trabalho", "#29B6F6")
        repository.setRecordingTags(path, setOf("Trabalho"))

        repository.setRecordingTags(path, emptySet())

        assertTrue(recordingTagStore.associations[path].isNullOrEmpty())
    }

    @Test
    fun `when set recording tags with unknown tag then should keep associations unchanged`() = runTest {
        repository.startRecording()
        val path = repository.stopAndSave()
        repository.createTag("Trabalho", "#29B6F6")
        repository.setRecordingTags(path, setOf("Trabalho"))

        try {
            repository.setRecordingTags(path, setOf("Inexistente"))
        } catch (_: IllegalArgumentException) {
        }

        assertEquals(setOf("Trabalho"), recordingTagStore.associations[path])
    }

    @Test
    fun `when set recording tags then should normalize tag names`() = runTest {
        repository.startRecording()
        val path = repository.stopAndSave()
        repository.createTag("Trabalho", "#29B6F6")

        repository.setRecordingTags(path, setOf("  Trabalho  "))

        assertEquals(setOf("Trabalho"), recordingTagStore.associations[path])
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
    fun `when get audio duration fails then should return default 00_00_00`() = runTest {
        val appFolder = File(tempExternalDir, "lugrav")
        appFolder.mkdirs()
        File(appFolder, "test.aac").createNewFile()
        every { metadataRetriever.extractDuration() } throws Exception("Error")

        val list = repository.getRecordingsList()

        assertEquals("00:00:00", list.first().duration)
        verify { metadataRetriever.release() }
    }

    @Test
    fun `when set data source fails then should return default and release retriever`() = runTest {
        val appFolder = File(tempExternalDir, "lugrav")
        appFolder.mkdirs()
        File(appFolder, "test.aac").createNewFile()
        every { metadataRetriever.setDataSource(any()) } throws Exception("Error")

        val list = repository.getRecordingsList()

        assertEquals("00:00:00", list.first().duration)
        verify { metadataRetriever.release() }
    }

    @Test
    fun `when extract duration returns null then should return default 00_00_00`() = runTest {
        val appFolder = File(tempExternalDir, "lugrav")
        appFolder.mkdirs()
        File(appFolder, "test.aac").createNewFile()
        every { metadataRetriever.extractDuration() } returns null

        val list = repository.getRecordingsList()

        assertEquals("00:00:00", list.first().duration)
        verify { metadataRetriever.release() }
    }

    @Test
    fun `when get recordings list with null file array then should return empty list`() = runTest {
        every { fileProvider.getExternalFilesDir() } returns null
        val list = repository.getRecordingsList()
        assertTrue(list.isEmpty())
    }
}

private class FakeRecordingTagStore : RecordingTagStore {

    val recordings = linkedMapOf<String, RecordingMetadata>()
    val tags = linkedMapOf<String, UserTag>()
    val associations = mutableMapOf<String, MutableSet<String>>()
    var syncedRecordings: List<RecordingMetadata> = emptyList()

    override suspend fun syncRecordings(recordings: List<RecordingMetadata>) {
        syncedRecordings = recordings
        val incoming = recordings.associateBy { it.path }
        this.recordings.keys.retainAll(incoming.keys)
        this.recordings.putAll(incoming)
    }

    override suspend fun getRecordings(tagNames: Set<String>): List<AudioRecordingModel> =
        recordings.values
            .filter { metadata ->
                tagNames.isEmpty() || tagNames.all { tagName ->
                    associations[metadata.path].orEmpty().contains(tagName)
                }
            }
            .map { metadata ->
                AudioRecordingModel(
                    path = metadata.path,
                    duration = metadata.duration,
                    createdAtMillis = metadata.createdAtMillis,
                    tags = associations[metadata.path].orEmpty().mapNotNull { tags[it] }
                )
            }

    override suspend fun insertRecording(recording: RecordingMetadata) {
        recordings[recording.path] = recording
    }

    override suspend fun updateRecordingPath(oldPath: String, newPath: String) {
        val metadata = recordings.remove(oldPath)
            ?: throw IllegalArgumentException("Recording not found: $oldPath")
        recordings[newPath] = metadata.copy(path = newPath)
        associations.remove(oldPath)?.let { associations[newPath] = it }
    }

    override suspend fun deleteRecording(path: String) {
        recordings.remove(path)
        associations.remove(path)
    }

    override suspend fun createTag(name: String, colorHex: String): UserTag {
        require(tags[name] == null) { "Tag already exists: $name" }
        val tag = UserTag(name, colorHex)
        tags[name] = tag
        return tag
    }

    override suspend fun getTags(): List<UserTag> = tags.values.toList()

    override suspend fun updateTag(
        currentName: String,
        newName: String,
        colorHex: String
    ): UserTag {
        if (newName != currentName) {
            require(tags[newName] == null) { "Tag already exists: $newName" }
        }
        val existing = tags.remove(currentName)
            ?: throw IllegalArgumentException("Tag not found: $currentName")
        val updated = existing.copy(name = newName, colorHex = colorHex)
        tags[newName] = updated
        associations.values.forEach { names ->
            if (names.remove(currentName)) {
                names.add(newName)
            }
        }
        return updated
    }

    override suspend fun deleteTag(name: String) {
        tags.remove(name)
        associations.values.forEach { it.remove(name) }
    }

    override suspend fun setRecordingTags(recordingPath: String, tagNames: Set<String>) {
        require(recordings.containsKey(recordingPath)) {
            "Recording not found: $recordingPath"
        }
        val missing = tagNames.filterNot { tags.containsKey(it) }
        require(missing.isEmpty()) { "Tag not found: ${missing.first()}" }

        if (tagNames.isEmpty()) {
            associations.remove(recordingPath)
        } else {
            associations[recordingPath] = tagNames.toMutableSet()
        }
    }
}
