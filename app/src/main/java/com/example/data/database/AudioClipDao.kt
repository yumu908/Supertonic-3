package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioClipDao {

    @Query("SELECT * FROM audio_clips ORDER BY createdAt DESC")
    fun getAllClips(): Flow<List<AudioClipEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClip(clip: AudioClipEntity)

    @Query("DELETE FROM audio_clips WHERE id = :id")
    suspend fun deleteClipById(id: String)

    @Query("DELETE FROM audio_clips")
    suspend fun clearAll()
}
