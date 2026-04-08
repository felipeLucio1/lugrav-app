package com.felipelucio.lugrav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class AudioRecordingViewModel(
    private val repository: AudioRecordingRepository
) : ViewModel() {

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _recordingTimeSeconds = MutableStateFlow(0)
    val recordingTimeSeconds: StateFlow<Int> = _recordingTimeSeconds

    private val _recordingSuccess = MutableStateFlow<Result<String>?>(null)
    val recordingSuccess: StateFlow<Result<String>?> = _recordingSuccess

    private val _recordingsList = MutableStateFlow<List<AudioRecordingModel>>(emptyList())
    val recordingsList: StateFlow<List<AudioRecordingModel>> = _recordingsList

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _playbackTimeFormatted = MutableStateFlow("00:00:00")
    val playbackTimeFormatted: StateFlow<String> = _playbackTimeFormatted

    private var playbackJob: Job? = null

    fun loadRecordings() {
        viewModelScope.launch {
            val recordings = repository.getRecordingsList()
            _recordingsList.value = recordings
        }
    }

    fun startRecording() {
        viewModelScope.launch {
            try {
                repository.startRecording()
                _isRecording.value = true
                _recordingTimeSeconds.value = 0
                
                while (_isRecording.value) {
                    delay(1000)
                    _recordingTimeSeconds.value++
                }
            } catch (e: Exception) {
                _recordingSuccess.value = Result.failure(e)
            }
        }
    }

    fun finalizeRecording() {
        viewModelScope.launch {
            try {
                val path = repository.stopAndSave()
                _isRecording.value = false
                _recordingTimeSeconds.value = 0
                _recordingSuccess.value = Result.success(path)
                loadRecordings()
            } catch (e: Exception) {
                _isRecording.value = false
                _recordingTimeSeconds.value = 0
                _recordingSuccess.value = Result.failure(e)
            }
        }
    }

    fun playAudio(filePath: String) {
        repository.playAudio(filePath)
        _isPlaying.value = true
        startPositionTracking()
    }

    fun pauseAudio() {
        repository.pauseAudio()
        _isPlaying.value = false
    }

    fun resumeAudio() {
        repository.resumeAudio()
        _isPlaying.value = true
        startPositionTracking()
    }

    fun stopAudio() {
        playbackJob?.cancel()
        repository.stopAudio()
        _isPlaying.value = false
        _playbackTimeFormatted.value = "00:00:00"
    }

    fun togglePlayPause(filePath: String) {
        if (_isPlaying.value) {
            pauseAudio()
        } else {
            if (repository.getCurrentPosition() > 0) {
                resumeAudio()
            } else {
                playAudio(filePath)
            }
        }
    }

    private fun startPositionTracking() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (_isPlaying.value) {
                updatePlaybackTimeFormatted()
                delay(1000)
            }
        }
    }

    private fun updatePlaybackTimeFormatted() {
        val currentPosition = repository.getCurrentPosition()
        val duration = repository.getDuration()
        
        if (duration > 0) {
            val remaining = duration - currentPosition
            _playbackTimeFormatted.value = formatDuration(remaining.toLong())
        }
    }

    private fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format(Locale("pt", "BR"), TIME_FORMAT, hours, minutes, seconds)
    }

    override fun onCleared() {
        super.onCleared()
        stopAudio()
    }

    companion object {
        const val TIME_FORMAT = "%02d:%02d:%02d"
    }
}