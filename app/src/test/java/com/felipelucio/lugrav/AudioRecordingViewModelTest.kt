package com.felipelucio.lugrav

import io.mockk.every
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
        val recordings = listOf(AudioRecordingModel("recording.aac", "00:00:01"))
        every { repository.getRecordingsList() } returns recordings

        viewModel.loadRecordings()

        assertFalse(viewModel.isLoadingRecordings.value)
        assertEquals(recordings, viewModel.recordingsList.value)
    }

    @Test
    fun `when load recordings fails then loading state should be false`() = runTest {
        every { repository.getRecordingsList() } throws IOException("fail")

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
        every { repository.renameRecording("old.aac", "novo") } returns "novo.aac"

        viewModel.renameRecording("old.aac", "novo")

        assertTrue(viewModel.renameResult.value?.isSuccess == true)
        verify { repository.renameRecording("old.aac", "novo") }
    }

    @Test
    fun `when rename fails then renameResult should be failure`() = runTest {
        every { repository.renameRecording(any(), any()) } throws IOException("fail")

        viewModel.renameRecording("old.aac", "novo")

        assertTrue(viewModel.renameResult.value?.isFailure == true)
    }

    @Test
    fun `when clear rename result then renameResult should be null`() = runTest {
        every { repository.renameRecording(any(), any()) } returns "novo.aac"
        viewModel.renameRecording("old.aac", "novo")

        viewModel.clearRenameResult()

        assertNull(viewModel.renameResult.value)
    }

    @Test
    fun `when renaming current playing file then should stop playback`() = runTest {
        every { repository.renameRecording(any(), any()) } returns "novo.aac"
        viewModel.playAudio("old.aac")

        viewModel.renameRecording("old.aac", "novo")

        verify { repository.stopAudio() }
        assertFalse(viewModel.isPlaying.value)
        assertNull(viewModel.currentPlayingPath.value)
    }

    @Test
    fun `when renaming another file then should not stop playback`() = runTest {
        every { repository.renameRecording(any(), any()) } returns "novo.aac"
        viewModel.playAudio("playing.aac")

        viewModel.renameRecording("other.aac", "novo")

        verify(exactly = 0) { repository.stopAudio() }
        assertTrue(viewModel.isPlaying.value)
        assertEquals("playing.aac", viewModel.currentPlayingPath.value)
    }
}
