package com.example.projteste

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AudioRecordingViewModel(
    private val repository: AudioRecordingRepository
) : ViewModel() {

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _recordingSuccess = MutableStateFlow<Result<String>?>(null)
    val recordingSuccess: StateFlow<Result<String>?> = _recordingSuccess

    fun startRecording() {
        viewModelScope.launch {
            try {
                repository.startRecording()
                _isRecording.value = true
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
                _recordingSuccess.value = Result.success(path)
            } catch (e: Exception) {
                _isRecording.value = false
                _recordingSuccess.value = Result.failure(e)
            }
        }
    }
}