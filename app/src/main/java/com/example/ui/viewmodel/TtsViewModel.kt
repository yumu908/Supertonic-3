package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.audio.AudioPlayerManager
import com.example.data.database.AppDatabase
import com.example.data.database.AudioClipRepository
import com.example.data.model.AudioClip
import com.example.data.model.LanguageItem
import com.example.data.model.PlaybackState
import com.example.data.model.QualityStep
import com.example.data.model.SpeakerPersona
import com.example.data.model.SynthesisStatus
import com.example.data.model.TextPresetType
import com.example.data.model.TtsPresets
import com.example.data.storage.AudioExportHelper
import com.example.data.tts.OfflineTtsEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

data class TtsUiState(
    val selectedPresetType: TextPresetType = TextPresetType.SCRIPT,
    val text: String = TtsPresets.DEFAULT_SCRIPT_TEXT,
    val selectedSpeaker: SpeakerPersona = TtsPresets.SPEAKERS[2], // Robert by default (as highlighted in screenshot!)
    val selectedLanguage: LanguageItem = TtsPresets.LANGUAGES[0], // English by default
    val speed: Float = 1.00f,
    val pitch: Float = 1.00f,
    val selectedQuality: QualityStep = TtsPresets.QUALITY_OPTIONS[1], // 8 Steps by default
    val synthesisStatus: SynthesisStatus = SynthesisStatus.Idle,
    val isEngineReady: Boolean = false,
    val notificationMessage: String? = null
)

class TtsViewModel(application: Application) : AndroidViewModel(application) {

    private val ttsEngine = OfflineTtsEngine(application)
    private val playerManager = AudioPlayerManager(application)
    private val repository = AudioClipRepository(AppDatabase.getInstance(application).audioClipDao())

    private val _uiState = MutableStateFlow(TtsUiState())
    val uiState: StateFlow<TtsUiState> = _uiState.asStateFlow()

    val playbackState: StateFlow<PlaybackState> = playerManager.playbackState

    val clipHistory: StateFlow<List<AudioClip>> = repository.allClips
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            ttsEngine.isInitialized.collect { ready ->
                _uiState.update { it.copy(isEngineReady = ready) }
            }
        }
    }

    fun selectPresetType(type: TextPresetType) {
        val sample = TtsPresets.getSampleFor(type, _uiState.value.selectedLanguage.code)
        _uiState.update {
            it.copy(
                selectedPresetType = type,
                text = sample
            )
        }
    }

    fun updateText(newText: String) {
        _uiState.update { it.copy(text = newText) }
    }

    fun selectSpeaker(speaker: SpeakerPersona) {
        _uiState.update { it.copy(selectedSpeaker = speaker) }
    }

    fun selectLanguage(language: LanguageItem) {
        _uiState.update {
            it.copy(selectedLanguage = language)
        }
    }

    fun updateSpeed(speed: Float) {
        _uiState.update { it.copy(speed = (speed * 100).toInt() / 100f) }
    }

    fun updatePitch(pitch: Float) {
        _uiState.update { it.copy(pitch = (pitch * 100).toInt() / 100f) }
    }

    fun selectQuality(quality: QualityStep) {
        _uiState.update { it.copy(selectedQuality = quality) }
    }

    fun previewVoice() {
        val state = _uiState.value
        val previewText = when {
            state.selectedLanguage.code.startsWith("zh") ->
                "您好，我是 ${state.selectedSpeaker.name}，这是我的本地合成人声。"
            state.selectedLanguage.code.startsWith("ja") ->
                "こんにちは、${state.selectedSpeaker.name}です。音声のテストです。"
            state.selectedLanguage.code.startsWith("ko") ->
                "안녕하세요, ${state.selectedSpeaker.name}입니다. 음성 미리보기입니다."
            else ->
                "Hello, I am ${state.selectedSpeaker.name}. This is a preview of my voice."
        }

        ttsEngine.speakPreview(
            text = previewText,
            language = state.selectedLanguage,
            speaker = state.selectedSpeaker,
            speed = state.speed,
            pitch = state.pitch
        )
    }

    fun generateSpeech() {
        val state = _uiState.value
        val textToSpeak = state.text.trim()
        if (textToSpeak.isBlank()) {
            _uiState.update { it.copy(notificationMessage = "Please enter some text to synthesize") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(synthesisStatus = SynthesisStatus.Processing("Synthesizing audio on-device...", 0.1f))
            }

            val audioDir = File(getApplication<Application>().filesDir, "audio")
            val clipId = UUID.randomUUID().toString()
            val fileName = "tts_${System.currentTimeMillis()}.wav"
            val targetFile = File(audioDir, fileName)

            val result = ttsEngine.synthesizeToFile(
                text = textToSpeak,
                outputFile = targetFile,
                language = state.selectedLanguage,
                speaker = state.selectedSpeaker,
                speed = state.speed,
                pitch = state.pitch,
                onProgress = { progress ->
                    _uiState.update {
                        it.copy(synthesisStatus = SynthesisStatus.Processing("Generating waveform...", progress))
                    }
                }
            )

            result.onSuccess { file ->
                val duration = AudioExportHelper.getAudioDuration(file)
                val titleExcerpt = textToSpeak.take(30).replace("\n", " ").trim()
                val clip = AudioClip(
                    id = clipId,
                    title = if (titleExcerpt.isNotEmpty()) titleExcerpt else "Speech_${state.selectedSpeaker.name}",
                    text = textToSpeak,
                    filePath = file.absolutePath,
                    languageCode = state.selectedLanguage.code,
                    speakerName = state.selectedSpeaker.name,
                    speed = state.speed,
                    pitch = state.pitch,
                    durationMs = duration,
                    fileSizeBytes = file.length(),
                    createdAt = System.currentTimeMillis()
                )

                repository.saveClip(clip)
                _uiState.update {
                    it.copy(
                        synthesisStatus = SynthesisStatus.Success(clip, "Speech generated successfully!"),
                        notificationMessage = "Speech generated on-device (${AudioExportHelper.formatDuration(duration.toInt())})"
                    )
                }

                // Autoload generated clip into player
                playerManager.loadAndPlay(clip)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        synthesisStatus = SynthesisStatus.Error(error.message ?: "Failed to synthesize speech"),
                        notificationMessage = "Synthesis failed: ${error.message}"
                    )
                }
            }
        }
    }

    fun playClip(clip: AudioClip) {
        playerManager.loadAndPlay(clip)
    }

    fun togglePlayPause() {
        playerManager.togglePlayPause()
    }

    fun seekTo(positionMs: Int) {
        playerManager.seekTo(positionMs)
    }

    fun stopAudio() {
        playerManager.stop()
        ttsEngine.stop()
    }

    fun exportClipToStorage(clip: AudioClip, context: Context) {
        val result = AudioExportHelper.exportToPublicDirectory(context, clip)
        result.onSuccess { path ->
            _uiState.update { it.copy(notificationMessage = "Exported: $path") }
        }.onFailure { error ->
            _uiState.update { it.copy(notificationMessage = "Export failed: ${error.message}") }
        }
    }

    fun shareClip(clip: AudioClip, context: Context) {
        val intent = AudioExportHelper.createShareIntent(context, clip)
        if (intent != null) {
            val chooser = android.content.Intent.createChooser(intent, "Share Audio Recording")
            chooser.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } else {
            _uiState.update { it.copy(notificationMessage = "Could not prepare audio file for sharing") }
        }
    }

    fun deleteClip(clip: AudioClip) {
        viewModelScope.launch {
            if (playbackState.value.activeClip?.id == clip.id) {
                playerManager.stop()
            }
            repository.deleteClip(clip)
            _uiState.update { it.copy(notificationMessage = "Clip deleted") }
        }
    }

    fun dismissNotification() {
        _uiState.update { it.copy(notificationMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
        ttsEngine.release()
    }
}
