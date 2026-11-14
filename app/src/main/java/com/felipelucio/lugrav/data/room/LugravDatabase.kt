package com.felipelucio.lugrav.data.room

import androidx.room3.Database
import androidx.room3.RoomDatabase

@Database(
    entities = [
        RecordingEntity::class,
        TagEntity::class,
        RecordingTagCrossRef::class
    ],
    version = 1,
    exportSchema = true
)
abstract class LugravDatabase : RoomDatabase() {
    abstract fun recordingDao(): RecordingDao
    abstract fun tagDao(): TagDao
    abstract fun recordingTagDao(): RecordingTagDao
}
