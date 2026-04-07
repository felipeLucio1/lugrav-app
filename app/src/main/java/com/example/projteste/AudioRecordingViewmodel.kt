package com.example.projteste

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projteste.AudioRecordingModel
import com.example.projteste.AudioRecordingRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
}