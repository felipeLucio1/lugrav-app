package com.felipelucio.lugrav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.Locale

class AudioRecordingViewModel(
    private val repository: AudioRecordingRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _recordingTimeSeconds = MutableStateFlow(0)
    val recordingTimeSeconds: StateFlow<Int> = _recordingTimeSeconds

    private val _recordingSuccess = MutableStateFlow<Result<String>?>(null)
    val recordingSuccess: StateFlow<Result<String>?> = _recordingSuccess

    private val _recordingsList = MutableStateFlow<List<AudioRecordingModel>>(emptyList())
    val recordingsList: StateFlow<List<AudioRecordingModel>> = _recordingsList

    private val _isLoadingRecordings = MutableStateFlow(true)
    val isLoadingRecordings: StateFlow<Boolean> = _isLoadingRecordings

    private val _tags = MutableStateFlow<List<UserTag>>(emptyList())
    val tags: StateFlow<List<UserTag>> = _tags

    private val _isLoadingTags = MutableStateFlow(true)
    val isLoadingTags: StateFlow<Boolean> = _isLoadingTags

    private val _selectedTagFilters = MutableStateFlow<Set<String>>(emptySet())
    val selectedTagFilters: StateFlow<Set<String>> = _selectedTagFilters

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _selectedAudioPath = MutableStateFlow<String?>(null)
    val selectedAudioPath: StateFlow<String?> = _selectedAudioPath

    private val _currentPlayingPath = MutableStateFlow<String?>(null)
    val currentPlayingPath: StateFlow<String?> = _currentPlayingPath

    private val _playbackTimeFormatted = MutableStateFlow("00:00:00")
    val playbackTimeFormatted: StateFlow<String> = _playbackTimeFormatted

    private var playbackJob: Job? = null
    private val fileOperationMutex = Mutex()

    private val _deleteResult = MutableStateFlow<Result<Unit>?>(null)
    val deleteResult: StateFlow<Result<Unit>?> = _deleteResult

    private val _renameResult = MutableStateFlow<Result<Unit>?>(null)
    val renameResult: StateFlow<Result<Unit>?> = _renameResult

    private val _createTagResult = MutableStateFlow<Result<UserTag>?>(null)
    val createTagResult: StateFlow<Result<UserTag>?> = _createTagResult

    private val _deleteTagResult = MutableStateFlow<Result<Unit>?>(null)
    val deleteTagResult: StateFlow<Result<Unit>?> = _deleteTagResult

    private val _setRecordingTagsResult = MutableStateFlow<Result<Unit>?>(null)
    val setRecordingTagsResult: StateFlow<Result<Unit>?> = _setRecordingTagsResult

    fun loadRecordings() {
        _isLoadingRecordings.value = true
        viewModelScope.launch {
            try {
                val recordings = withContext(ioDispatcher) {
                    repository.getRecordingsList(_selectedTagFilters.value)
                }
                _recordingsList.value = recordings
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
            } finally {
                _isLoadingRecordings.value = false
            }
        }
    }

    fun loadTags() {
        _isLoadingTags.value = true
        viewModelScope.launch {
            try {
                val filtersChanged = refreshTags()
                if (filtersChanged) {
                    refreshRecordingsBestEffort()
                }
            } finally {
                _isLoadingTags.value = false
            }
        }
    }

    fun createTag(name: String, colorHex: String) {
        viewModelScope.launch {
            try {
                val tag = withContext(ioDispatcher) {
                    repository.createTag(name, colorHex)
                }
                val filtersChanged = refreshTags()
                if (filtersChanged) {
                    refreshRecordingsBestEffort()
                }
                _createTagResult.value = Result.success(tag)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _createTagResult.value = Result.failure(e)
            }
        }
    }

    fun deleteTag(name: String) {
        viewModelScope.launch {
            try {
                withContext(ioDispatcher) {
                    repository.deleteTag(name)
                }
                refreshTags()
                _selectedTagFilters.value = _selectedTagFilters.value - name
                refreshRecordingsBestEffort()
                _deleteTagResult.value = Result.success(Unit)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _deleteTagResult.value = Result.failure(e)
            }
        }
    }

    fun setRecordingTags(recordingPath: String, tagNames: Set<String>) {
        viewModelScope.launch {
            try {
                withContext(ioDispatcher) {
                    repository.setRecordingTags(recordingPath, tagNames)
                }
                refreshRecordingsBestEffort()
                _setRecordingTagsResult.value = Result.success(Unit)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _setRecordingTagsResult.value = Result.failure(e)
            }
        }
    }

    fun toggleTagFilter(tagName: String) {
        val currentFilters = _selectedTagFilters.value
        _selectedTagFilters.value = if (tagName in currentFilters) {
            currentFilters - tagName
        } else {
            currentFilters + tagName
        }
        reloadRecordingsForFilters()
    }

    fun setTagFilters(tagNames: Set<String>) {
        _selectedTagFilters.value = tagNames.toSet()
        reloadRecordingsForFilters()
    }

    fun clearTagFilters() {
        _selectedTagFilters.value = emptySet()
        reloadRecordingsForFilters()
    }

    fun clearCreateTagResult() {
        _createTagResult.value = null
    }

    fun clearDeleteTagResult() {
        _deleteTagResult.value = null
    }

    fun clearSetRecordingTagsResult() {
        _setRecordingTagsResult.value = null
    }

    private suspend fun refreshTags(): Boolean {
        return try {
            val loadedTags = withContext(ioDispatcher) {
                repository.getTags()
            }
            _tags.value = loadedTags

            val validNames = loadedTags.mapTo(mutableSetOf()) { it.name }
            val currentFilters = _selectedTagFilters.value
            val prunedFilters = currentFilters.intersect(validNames)
            if (prunedFilters != currentFilters) {
                _selectedTagFilters.value = prunedFilters
                true
            } else {
                false
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            false
        }
    }

    private fun reloadRecordingsForFilters() {
        viewModelScope.launch {
            refreshRecordingsBestEffort()
        }
    }

    private suspend fun refreshRecordingsBestEffort() {
        try {
            val recordings = withContext(ioDispatcher) {
                repository.getRecordingsByTags(_selectedTagFilters.value)
            }
            _recordingsList.value = recordings
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
        }
    }

    fun deleteRecording(filePath: String) {
        viewModelScope.launch {
            fileOperationMutex.withLock {
                try {
                    withContext(ioDispatcher) {
                        repository.deleteRecording(filePath)
                    }
                    loadRecordings()
                    _deleteResult.value = Result.success(Unit)
                } catch (e: Exception) {
                    _deleteResult.value = Result.failure(e)
                }
            }
        }
    }

    fun clearDeleteResult() {
        _deleteResult.value = null
    }

    fun renameRecording(filePath: String, newName: String) {
        viewModelScope.launch {
            fileOperationMutex.withLock {
                try {
                    if (_currentPlayingPath.value == filePath) {
                        stopAudio()
                    }
                    withContext(ioDispatcher) {
                        repository.renameRecording(filePath, newName)
                    }
                    loadRecordings()
                    _renameResult.value = Result.success(Unit)
                } catch (e: Exception) {
                    _renameResult.value = Result.failure(e)
                }
            }
        }
    }

    fun clearRenameResult() {
        _renameResult.value = null
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
                val path = withContext(ioDispatcher) {
                    repository.stopAndSave()
                }
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
        _currentPlayingPath.value = filePath
        repository.playAudio(filePath) {
            _isPlaying.value = false
            _currentPlayingPath.value = null
        }
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
        _currentPlayingPath.value = null
    }

    fun clearSelectedAudio() {
        _selectedAudioPath.value = null
    }

    fun togglePlayPause(filePath: String) {
        _selectedAudioPath.value = filePath

        if (_isPlaying.value) {
            pauseAudio()
        } else {
            val currentPos = repository.getCurrentPosition()
            val isSameAudio = _currentPlayingPath.value == filePath

            if (isSameAudio && currentPos > 0) {
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