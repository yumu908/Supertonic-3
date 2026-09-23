package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AudioClip

@Entity(tableName = "audio_clips")
data class AudioClipEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val text: String,
    val filePath: String,
    val languageCode: String,
    val speakerName: String,
    val speed: Float,
    val pitch: Float,
    val durationMs: Long,
    val fileSizeBytes: Long,
    val createdAt: Long
) {
    fun toAudioClip(): AudioClip = AudioClip(
        id = id,
        title = title,
        text = text,
        filePath = filePath,
        languageCode = languageCode,
        speakerName = speakerName,
        speed = speed,
        pitch = pitch,
        durationMs = durationMs,
        fileSizeBytes = fileSizeBytes,
        createdAt = createdAt
    )

    companion object {
        fun fromAudioClip(clip: AudioClip): AudioClipEntity = AudioClipEntity(
            id = clip.id,
            title = clip.title,
            text = clip.text,
            filePath = clip.filePath,
            languageCode = clip.languageCode,
            speakerName = clip.speakerName,
            speed = clip.speed,
            pitch = clip.pitch,
            durationMs = clip.durationMs,
            fileSizeBytes = clip.fileSizeBytes,
            createdAt = clip.createdAt
        )
    }
}
