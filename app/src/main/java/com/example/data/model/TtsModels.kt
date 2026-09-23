package com.example.data.model

import java.util.Locale

data class LanguageItem(
    val code: String,
    val name: String,
    val nativeName: String,
    val flag: String,
    val locale: Locale
)

data class SpeakerPersona(
    val id: String,
    val name: String,
    val gender: String,
    val tag: String,
    val pitchMultiplier: Float = 1.0f,
    val speedMultiplier: Float = 1.0f,
    val preferredVoiceKeywords: List<String> = emptyList()
)

enum class TextPresetType(val label: String) {
    FREEFORM("Freeform"),
    QUOTE("Quote"),
    PARAGRAPH("Paragraph"),
    SCRIPT("Script")
}

data class QualityStep(
    val steps: Int,
    val label: String,
    val description: String
)

data class AudioClip(
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
)

sealed interface SynthesisStatus {
    object Idle : SynthesisStatus
    data class Processing(val step: String, val progress: Float) : SynthesisStatus
    data class Success(val clip: AudioClip, val message: String) : SynthesisStatus
    data class Error(val message: String) : SynthesisStatus
}

data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentPositionMs: Int = 0,
    val totalDurationMs: Int = 0,
    val activeClip: AudioClip? = null
)
