package com.felipelucio.lugrav.data.room

import androidx.room3.withWriteTransaction
import com.felipelucio.lugrav.AudioRecordingModel
import com.felipelucio.lugrav.UserTag

class RoomRecordingTagStore(
    private val database: LugravDatabase,
    private val recordingDao: RecordingDao,
    private val tagDao: TagDao,
    private val recordingTagDao: RecordingTagDao
) : RecordingTagStore {

    override suspend fun syncRecordings(recordings: List<RecordingMetadata>) {
        val incomingPaths = recordings.mapTo(mutableSetOf()) { it.path }
        val existingPaths = recordingDao.getAllPaths().toSet()

        (existingPaths - incomingPaths).forEach { path ->
            recordingDao.deleteByPath(path)
        }

        recordings.forEach { metadata ->
            val existing = recordingDao.findByPath(metadata.path)
            if (existing == null) {
                recordingDao.insert(metadata.toEntity())
            } else if (
                existing.duration != metadata.duration ||
                existing.createdAtMillis != metadata.createdAtMillis
            ) {
                recordingDao.update(
                    existing.copy(
                        duration = metadata.duration,
                        createdAtMillis = metadata.createdAtMillis
                    )
                )
            }
        }
    }

    override suspend fun getRecordings(tagNames: Set<String>): List<AudioRecordingModel> {
        val tagNamesList = tagNames.toList()
        val recordings = if (tagNamesList.isEmpty()) {
            recordingDao.getRecordingsWithTags()
        } else {
            recordingDao.getRecordingsWithAllTags(tagNamesList, tagNamesList.size)
        }
        return recordings.map { it.toModel() }
    }

    override suspend fun insertRecording(recording: RecordingMetadata) {
        val existing = recordingDao.findByPath(recording.path)
        if (existing == null) {
            recordingDao.insert(recording.toEntity())
        } else {
            recordingDao.update(
                existing.copy(
                    duration = recording.duration,
                    createdAtMillis = recording.createdAtMillis
                )
            )
        }
    }

    override suspend fun updateRecordingPath(oldPath: String, newPath: String) {
        val existing = recordingDao.findByPath(oldPath)
            ?: throw IllegalArgumentException("Recording not found: $oldPath")
        recordingDao.update(existing.copy(path = newPath))
    }

    override suspend fun deleteRecording(path: String) {
        recordingDao.deleteByPath(path)
    }

    override suspend fun createTag(name: String, colorHex: String): UserTag {
        val normalizedName = name.trim()
        require(normalizedName.isNotBlank()) { "Tag name must not be blank" }
        require(tagDao.findByName(normalizedName) == null) {
            "Tag already exists: $normalizedName"
        }
        tagDao.insert(TagEntity(name = normalizedName, colorHex = colorHex))
        return UserTag(normalizedName, colorHex)
    }

    override suspend fun getTags(): List<UserTag> =
        tagDao.getAll().map { UserTag(it.name, it.colorHex) }

    override suspend fun updateTag(
        currentName: String,
        newName: String,
        colorHex: String
    ): UserTag {
        val normalizedNewName = newName.trim()
        require(normalizedNewName.isNotBlank()) { "Tag name must not be blank" }
        val existing = tagDao.findByName(currentName)
            ?: throw IllegalArgumentException("Tag not found: $currentName")
        if (normalizedNewName != currentName) {
            require(tagDao.findByName(normalizedNewName) == null) {
                "Tag already exists: $normalizedNewName"
            }
        }
        tagDao.update(existing.copy(name = normalizedNewName, colorHex = colorHex))
        return UserTag(normalizedNewName, colorHex)
    }

    override suspend fun deleteTag(name: String) {
        val existing = tagDao.findByName(name)
            ?: throw IllegalArgumentException("Tag not found: $name")
        tagDao.delete(existing)
    }

    override suspend fun setRecordingTags(recordingPath: String, tagNames: Set<String>) {
        database.withWriteTransaction {
            val recording = recordingDao.findByPath(recordingPath)
                ?: throw IllegalArgumentException("Recording not found: $recordingPath")

            val tagEntities = if (tagNames.isEmpty()) {
                emptyList()
            } else {
                tagDao.getAll().filter { it.name in tagNames }
            }

            require(tagEntities.size == tagNames.size) {
                val foundNames = tagEntities.mapTo(mutableSetOf()) { it.name }
                "Tag not found: ${tagNames - foundNames}"
            }

            recordingTagDao.deleteForRecording(recording.id)
            tagEntities.forEach { tag ->
                recordingTagDao.insert(RecordingTagCrossRef(recording.id, tag.id))
            }
        }
    }

    private fun RecordingMetadata.toEntity() = RecordingEntity(
        path = path,
        duration = duration,
        createdAtMillis = createdAtMillis
    )

    private fun RecordingWithTags.toModel() = AudioRecordingModel(
        path = recording.path,
        duration = recording.duration,
        createdAtMillis = recording.createdAtMillis,
        tags = tags.map { UserTag(it.name, it.colorHex) }
    )
}
