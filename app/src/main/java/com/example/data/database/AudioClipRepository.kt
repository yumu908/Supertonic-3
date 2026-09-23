package com.example.data.database

import com.example.data.model.AudioClip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File

class AudioClipRepository(private val dao: AudioClipDao) {

    val allClips: Flow<List<AudioClip>> = dao.getAllClips().map { entities ->
        entities.map { it.toAudioClip() }
    }

    suspend fun saveClip(clip: AudioClip) {
        dao.insertClip(AudioClipEntity.fromAudioClip(clip))
    }

    suspend fun deleteClip(clip: AudioClip) {
        dao.deleteClipById(clip.id)
        try {
            val file = File(clip.filePath)
            if (file.exists()) {
                file.delete()
            }
        } catch (_: Exception) {}
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}
