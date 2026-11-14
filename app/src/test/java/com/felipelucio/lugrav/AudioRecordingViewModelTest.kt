package com.felipelucio.lugrav

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import java.io.IOException

@ExperimentalCoroutinesApi
class AudioRecordingViewModelTest {

    private lateinit var repository: AudioRecordingRepository
    private lateinit var viewModel: AudioRecordingViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        
        repository = mockk(relaxed = true)
        viewModel = AudioRecordingViewModel(repository, Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when view model is created then loading state should be true`() {
        assertTrue(viewModel.isLoadingRecordings.value)
    }

    @Test
    fun `when load recordings succeeds then loading state should be false and list updated`() = runTest {
        val recordings = listOf(AudioRecordingModel("recording.aac", "00:00:01", 1_000L))
        coEvery { repository.getRecordingsList(emptySet()) } returns recordings

        viewModel.loadRecordings()

        assertFalse(viewModel.isLoadingRecordings.value)
        assertEquals(recordings, viewModel.recordingsList.value)
    }

    @Test
    fun `when load recordings fails then loading state should be false`() = runTest {
        coEvery { repository.getRecordingsList(emptySet()) } throws IOException("fail")

        viewModel.loadRecordings()

        assertFalse(viewModel.isLoadingRecordings.value)
    }

    @Test
    fun `when recording audio then is recorded state should be true`() = runTest {
        viewModel.startRecording()
        assertTrue(viewModel.isRecording.value)
    }

    @Test
    fun `when recording audio then recording time increments`() = runTest {
        viewModel.startRecording()
        assertEquals(0, viewModel.recordingTimeSeconds.value)
        assertTrue(viewModel.isRecording.value)
    }

    @Test
    fun `when playing audio then play audio sets isPlaying to true`() = runTest {
        val filePath = "test_audio.aac"
        viewModel.playAudio(filePath)
        assertTrue(viewModel.isPlaying.value)
        assertEquals(filePath, viewModel.currentPlayingPath.value)
    }

    @Test
    fun `when stop reproducing audio then playing audio state should be set to null`() = runTest {
        viewModel.playAudio("test.aac")
        viewModel.stopAudio()
        assertFalse(viewModel.isPlaying.value)
        assertNull(viewModel.currentPlayingPath.value)
        assertEquals("00:00:00", viewModel.playbackTimeFormatted.value)
    }

    @Test
    fun `when pause audio then isPlaying should be set to false`() = runTest {
        viewModel.playAudio("test.aac")
        viewModel.pauseAudio()
        assertFalse(viewModel.isPlaying.value)
    }

    @Test
    fun `when resume audio then sets isPlaying to true`() = runTest {
        viewModel.playAudio("test.aac")
        viewModel.pauseAudio()
        viewModel.resumeAudio()
        assertTrue(viewModel.isPlaying.value)
    }

    @Test
    fun `when toggle play pause while no audio is playing then should start playing`() = runTest {
        val filePath = "test.aac"
        viewModel.togglePlayPause(filePath)
        assertTrue(viewModel.isPlaying.value)
        assertEquals(filePath, viewModel.currentPlayingPath.value)
    }

    @Test
    fun `when clear selected audio then should set selectedAudio to null`() = runTest {
        viewModel.togglePlayPause("test.aac")
        viewModel.clearSelectedAudio()
        assertNull(viewModel.selectedAudioPath.value)
    }

    @Test
    fun `when clear delete result is called then deleteResult should be set to null`() = runTest {
        viewModel.clearDeleteResult()
        assertNull(viewModel.deleteResult.value)
    }

    @Test
    fun `when rename succeeds then renameResult should be success`() = runTest {
        coEvery { repository.renameRecording("old.aac", "novo") } returns "novo.aac"

        viewModel.renameRecording("old.aac", "novo")

        assertTrue(viewModel.renameResult.value?.isSuccess == true)
        coVerify { repository.renameRecording("old.aac", "novo") }
    }

    @Test
    fun `when rename fails then renameResult should be failure`() = runTest {
        coEvery { repository.renameRecording(any(), any()) } throws IOException("fail")

        viewModel.renameRecording("old.aac", "novo")

        assertTrue(viewModel.renameResult.value?.isFailure == true)
    }

    @Test
    fun `when clear rename result then renameResult should be null`() = runTest {
        coEvery { repository.renameRecording(any(), any()) } returns "novo.aac"
        viewModel.renameRecording("old.aac", "novo")

        viewModel.clearRenameResult()

        assertNull(viewModel.renameResult.value)
    }

    @Test
    fun `when renaming current playing file then should stop playback`() = runTest {
        coEvery { repository.renameRecording(any(), any()) } returns "novo.aac"
        viewModel.playAudio("old.aac")

        viewModel.renameRecording("old.aac", "novo")

        verify { repository.stopAudio() }
        assertFalse(viewModel.isPlaying.value)
        assertNull(viewModel.currentPlayingPath.value)
    }

    @Test
    fun `when renaming another file then should not stop playback`() = runTest {
        coEvery { repository.renameRecording(any(), any()) } returns "novo.aac"
        viewModel.playAudio("playing.aac")

        viewModel.renameRecording("other.aac", "novo")

        verify(exactly = 0) { repository.stopAudio() }
        assertTrue(viewModel.isPlaying.value)
        assertEquals("playing.aac", viewModel.currentPlayingPath.value)
    }

    @Test
    fun `when load tags succeeds then tags should be updated`() = runTest {
        val tags = listOf(UserTag("Trabalho", "#29B6F6"))
        coEvery { repository.getTags() } returns tags

        viewModel.loadTags()

        assertFalse(viewModel.isLoadingTags.value)
        assertEquals(tags, viewModel.tags.value)
    }

    @Test
    fun `when load tags fails then loading state should be false`() = runTest {
        coEvery { repository.getTags() } throws IOException("fail")

        viewModel.loadTags()

        assertFalse(viewModel.isLoadingTags.value)
        assertTrue(viewModel.tags.value.isEmpty())
    }

    @Test
    fun `when load tags removes selected tag then should prune filters and reload`() = runTest {
        coEvery { repository.getRecordingsByTags(setOf("Trabalho")) } returns emptyList()
        coEvery { repository.getRecordingsByTags(emptySet()) } returns emptyList()
        coEvery { repository.getTags() } returns listOf(UserTag("Pessoal", "#FF0000"))
        viewModel.setTagFilters(setOf("Trabalho"))

        viewModel.loadTags()

        assertTrue(viewModel.selectedTagFilters.value.isEmpty())
        coVerify { repository.getRecordingsByTags(emptySet()) }
    }

    @Test
    fun `when create tag succeeds then should reload tags and set success result`() = runTest {
        val tag = UserTag("Trabalho", "#29B6F6")
        coEvery { repository.createTag("Trabalho", "#29B6F6") } returns tag
        coEvery { repository.getTags() } returns listOf(tag)

        viewModel.createTag("Trabalho", "#29B6F6")

        assertTrue(viewModel.createTagResult.value?.isSuccess == true)
        assertEquals(tag, viewModel.createTagResult.value?.getOrNull())
        assertEquals(listOf(tag), viewModel.tags.value)
        coVerify { repository.createTag("Trabalho", "#29B6F6") }
    }

    @Test
    fun `when create tag fails then should set failure result`() = runTest {
        coEvery { repository.createTag(any(), any()) } throws IllegalArgumentException("fail")

        viewModel.createTag("Trabalho", "blue")

        assertTrue(viewModel.createTagResult.value?.isFailure == true)
    }

    @Test
    fun `when clear create tag result then result should be null`() = runTest {
        val tag = UserTag("Trabalho", "#29B6F6")
        coEvery { repository.createTag(any(), any()) } returns tag
        coEvery { repository.getTags() } returns listOf(tag)
        viewModel.createTag("Trabalho", "#29B6F6")

        viewModel.clearCreateTagResult()

        assertNull(viewModel.createTagResult.value)
    }

    @Test
    fun `when delete tag succeeds then should remove filter and reload recordings`() = runTest {
        coEvery { repository.getTags() } returns listOf(UserTag("Pessoal", "#FF0000"))
        coEvery { repository.getRecordingsByTags(setOf("Trabalho")) } returns emptyList()
        coEvery { repository.getRecordingsByTags(emptySet()) } returns emptyList()
        viewModel.setTagFilters(setOf("Trabalho"))

        viewModel.deleteTag("Trabalho")

        assertTrue(viewModel.deleteTagResult.value?.isSuccess == true)
        assertTrue(viewModel.selectedTagFilters.value.isEmpty())
        coVerify { repository.deleteTag("Trabalho") }
        coVerify { repository.getRecordingsByTags(emptySet()) }
    }

    @Test
    fun `when delete tag fails then should keep filter and set failure result`() = runTest {
        coEvery { repository.getRecordingsByTags(setOf("Trabalho")) } returns emptyList()
        coEvery { repository.deleteTag("Trabalho") } throws IOException("fail")
        viewModel.setTagFilters(setOf("Trabalho"))

        viewModel.deleteTag("Trabalho")

        assertTrue(viewModel.deleteTagResult.value?.isFailure == true)
        assertEquals(setOf("Trabalho"), viewModel.selectedTagFilters.value)
    }

    @Test
    fun `when clear delete tag result then result should be null`() = runTest {
        coEvery { repository.getTags() } returns emptyList()
        coEvery { repository.getRecordingsByTags(emptySet()) } returns emptyList()
        viewModel.deleteTag("Trabalho")

        viewModel.clearDeleteTagResult()

        assertNull(viewModel.deleteTagResult.value)
    }

    @Test
    fun `when toggle tag filter then should add and remove filter`() = runTest {
        coEvery { repository.getRecordingsByTags(any()) } returns emptyList()

        viewModel.toggleTagFilter("Trabalho")

        assertEquals(setOf("Trabalho"), viewModel.selectedTagFilters.value)
        coVerify { repository.getRecordingsByTags(setOf("Trabalho")) }

        viewModel.toggleTagFilter("Trabalho")

        assertTrue(viewModel.selectedTagFilters.value.isEmpty())
        coVerify { repository.getRecordingsByTags(emptySet()) }
    }

    @Test
    fun `when set tag filters then should reload with all selected tags`() = runTest {
        coEvery { repository.getRecordingsByTags(any()) } returns emptyList()

        viewModel.setTagFilters(setOf("Trabalho", "Importante"))

        assertEquals(setOf("Trabalho", "Importante"), viewModel.selectedTagFilters.value)
        coVerify { repository.getRecordingsByTags(setOf("Trabalho", "Importante")) }
    }

    @Test
    fun `when clear tag filters then should reload all recordings`() = runTest {
        coEvery { repository.getRecordingsByTags(any()) } returns emptyList()
        viewModel.setTagFilters(setOf("Trabalho"))

        viewModel.clearTagFilters()

        assertTrue(viewModel.selectedTagFilters.value.isEmpty())
        coVerify { repository.getRecordingsByTags(emptySet()) }
    }

    @Test
    fun `when load recordings with filters then should use selected tags`() = runTest {
        val recordings = listOf(AudioRecordingModel("recording.aac", "00:00:01", 1_000L))
        coEvery { repository.getRecordingsByTags(setOf("Trabalho")) } returns emptyList()
        coEvery { repository.getRecordingsList(setOf("Trabalho")) } returns recordings
        viewModel.setTagFilters(setOf("Trabalho"))

        viewModel.loadRecordings()

        assertEquals(recordings, viewModel.recordingsList.value)
        coVerify { repository.getRecordingsList(setOf("Trabalho")) }
    }

    @Test
    fun `when set recording tags succeeds then should reload recordings`() = runTest {
        val recordings = listOf(
            AudioRecordingModel(
                "recording.aac",
                "00:00:01",
                1_000L,
                listOf(UserTag("Trabalho", "#29B6F6"))
            )
        )
        coEvery { repository.getRecordingsByTags(emptySet()) } returns recordings

        viewModel.setRecordingTags("recording.aac", setOf("Trabalho"))

        assertTrue(viewModel.setRecordingTagsResult.value?.isSuccess == true)
        assertEquals(recordings, viewModel.recordingsList.value)
        coVerify { repository.setRecordingTags("recording.aac", setOf("Trabalho")) }
        coVerify { repository.getRecordingsByTags(emptySet()) }
    }

    @Test
    fun `when set recording tags fails then should set failure result`() = runTest {
        coEvery { repository.setRecordingTags(any(), any()) } throws IOException("fail")

        viewModel.setRecordingTags("recording.aac", setOf("Trabalho"))

        assertTrue(viewModel.setRecordingTagsResult.value?.isFailure == true)
        coVerify(exactly = 0) { repository.getRecordingsByTags(any()) }
    }

    @Test
    fun `when set recording tags with active filters then should reload using selected tags`() = runTest {
        coEvery { repository.getRecordingsByTags(setOf("Trabalho")) } returns emptyList()
        viewModel.setTagFilters(setOf("Trabalho"))

        viewModel.setRecordingTags("recording.aac", setOf("Trabalho"))

        coVerify { repository.getRecordingsByTags(setOf("Trabalho")) }
    }

    @Test
    fun `when clear set recording tags result then result should be null`() = runTest {
        coEvery { repository.getRecordingsByTags(emptySet()) } returns emptyList()
        viewModel.setRecordingTags("recording.aac", setOf("Trabalho"))

        viewModel.clearSetRecordingTagsResult()

        assertNull(viewModel.setRecordingTagsResult.value)
    }
}
