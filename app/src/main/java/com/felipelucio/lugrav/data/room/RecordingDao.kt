package com.felipelucio.lugrav.data.room

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Update

@Dao
interface RecordingDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(recording: RecordingEntity): Long

    @Update
    suspend fun update(recording: RecordingEntity)

    @Query("SELECT * FROM recordings WHERE path = :path LIMIT 1")
    suspend fun findByPath(path: String): RecordingEntity?

    @Query("SELECT path FROM recordings")
    suspend fun getAllPaths(): List<String>

    @Query("DELETE FROM recordings WHERE path = :path")
    suspend fun deleteByPath(path: String): Int

    @Transaction
    @Query("SELECT * FROM recordings ORDER BY createdAtMillis DESC, path ASC")
    suspend fun getRecordingsWithTags(): List<RecordingWithTags>

    @Transaction
    @Query(
        """
        SELECT * FROM recordings
        WHERE id IN (
            SELECT rt.recordingId
            FROM recording_tags rt
            INNER JOIN tags t ON t.id = rt.tagId
            WHERE t.name IN (:tagNames)
            GROUP BY rt.recordingId
            HAVING COUNT(DISTINCT t.name) = :tagCount
        )
        ORDER BY createdAtMillis DESC, path ASC
        """
    )
    suspend fun getRecordingsWithAllTags(
        tagNames: List<String>,
        tagCount: Int
    ): List<RecordingWithTags>
}
