package com.felipelucio.lugrav.data.room

import androidx.room3.Embedded
import androidx.room3.Junction
import androidx.room3.Relation

data class RecordingWithTags(
    @Embedded val recording: RecordingEntity,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["id"],
        associateBy = Junction(
            value = RecordingTagCrossRef::class,
            parentColumns = ["recordingId"],
            entityColumns = ["tagId"]
        )
    )
    val tags: List<TagEntity>
)
