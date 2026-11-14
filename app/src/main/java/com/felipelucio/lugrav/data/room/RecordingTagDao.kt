package com.felipelucio.lugrav.data.room

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query

@Dao
interface RecordingTagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(crossRef: RecordingTagCrossRef)

    @Query("DELETE FROM recording_tags WHERE recordingId = :recordingId")
    suspend fun deleteForRecording(recordingId: Int)

    @Query("SELECT * FROM recording_tags WHERE recordingId = :recordingId")
    suspend fun getForRecording(recordingId: Int): List<RecordingTagCrossRef>
}
