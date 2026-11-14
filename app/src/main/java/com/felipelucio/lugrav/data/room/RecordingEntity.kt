package com.felipelucio.lugrav.data.room

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "recordings",
    indices = [Index(value = ["path"], unique = true)]
)
data class RecordingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val path: String,
    val duration: String,
    val createdAtMillis: Long
)
