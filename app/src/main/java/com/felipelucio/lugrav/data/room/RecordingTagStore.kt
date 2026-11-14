package com.felipelucio.lugrav.data.room

import com.felipelucio.lugrav.AudioRecordingModel
import com.felipelucio.lugrav.UserTag

data class RecordingMetadata(
    val path: String,
    val duration: String,
    val createdAtMillis: Long
)

interface RecordingTagStore {
    suspend fun syncRecordings(recordings: List<RecordingMetadata>)

    suspend fun getRecordings(tagNames: Set<String> = emptySet()): List<AudioRecordingModel>

    suspend fun insertRecording(recording: RecordingMetadata)

    suspend fun updateRecordingPath(oldPath: String, newPath: String)

    suspend fun deleteRecording(path: String)

    suspend fun createTag(name: String, colorHex: String): UserTag

    suspend fun getTags(): List<UserTag>

    suspend fun updateTag(currentName: String, newName: String, colorHex: String): UserTag

    suspend fun deleteTag(name: String)

    suspend fun setRecordingTags(recordingPath: String, tagNames: Set<String>)
}
