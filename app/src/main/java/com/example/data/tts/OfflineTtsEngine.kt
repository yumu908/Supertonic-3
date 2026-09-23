package com.example.data.tts

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import com.example.data.model.LanguageItem
import com.example.data.model.SpeakerPersona
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.util.Locale
import kotlin.coroutines.resume

class OfflineTtsEngine(private val context: Context) {

    private var tts: TextToSpeech? = null
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    private val _availableVoices = MutableStateFlow<List<Voice>>(emptyList())
    val availableVoices: StateFlow<List<Voice>> = _availableVoices.asStateFlow()

    private val _currentUtteranceStatus = MutableStateFlow<String?>(null)
    val currentUtteranceStatus: StateFlow<String?> = _currentUtteranceStatus.asStateFlow()

    init {
        initialize()
    }

    private fun initialize() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.let { engine ->
                    try {
                        val voices = engine.voices?.toList() ?: emptyList()
                        _availableVoices.value = voices
                        Log.d("OfflineTtsEngine", "TTS initialized with ${voices.size} voices")
                    } catch (e: Exception) {
                        Log.w("OfflineTtsEngine", "Could not query voices: ${e.message}")
                    }
                    _isInitialized.value = true
                }
            } else {
                Log.e("OfflineTtsEngine", "TTS Initialization failed with code $status")
            }
        }
    }

    fun applySettings(
        language: LanguageItem,
        speaker: SpeakerPersona,
        speed: Float,
        pitch: Float
    ) {
        val engine = tts ?: return

        // Set Language / Locale
        try {
            val result = engine.setLanguage(language.locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w("OfflineTtsEngine", "Language ${language.code} has missing data or not supported")
            }
        } catch (e: Exception) {
            Log.e("OfflineTtsEngine", "Error setting language: ${e.message}")
        }

        // Find best matching voice for speaker persona and selected language
        try {
            val voices = engine.voices
            if (!voices.isNullOrEmpty()) {
                val matchedVoice = findVoiceForSpeaker(voices, language.locale, speaker)
                if (matchedVoice != null) {
                    engine.voice = matchedVoice
                }
            }
        } catch (e: Exception) {
            Log.w("OfflineTtsEngine", "Voice selection fallback: ${e.message}")
        }

        // Apply pitch (effective pitch = user pitch * persona pitchMultiplier)
        val effectivePitch = (pitch * speaker.pitchMultiplier).coerceIn(0.5f, 2.0f)
        engine.setPitch(effectivePitch)

        // Apply speed (effective speed = user speed * persona speedMultiplier)
        val effectiveSpeed = (speed * speaker.speedMultiplier).coerceIn(0.5f, 2.5f)
        engine.setSpeechRate(effectiveSpeed)
    }

    private fun findVoiceForSpeaker(
        voices: Collection<Voice>,
        targetLocale: Locale,
        speaker: SpeakerPersona
    ): Voice? {
        val localeVoices = voices.filter { voice ->
            voice.locale.language == targetLocale.language
        }
        if (localeVoices.isEmpty()) return null

        // Try to match preferred keywords (e.g. "male", "female")
        val isTargetFemale = speaker.gender.equals("female", ignoreCase = true)
        val genderFiltered = localeVoices.filter { voice ->
            val name = voice.name.lowercase()
            if (isTargetFemale) {
                name.contains("female") || name.contains("fem") || name.contains("woman") || name.contains("_f_") || name.contains("sfg#female")
            } else {
                name.contains("male") || name.contains("man") || name.contains("_m_") || name.contains("sfg#male")
            }
        }

        return genderFiltered.firstOrNull() ?: localeVoices.firstOrNull()
    }

    fun speakPreview(
        text: String,
        language: LanguageItem,
        speaker: SpeakerPersona,
        speed: Float,
        pitch: Float,
        onDone: () -> Unit = {}
    ) {
        val engine = tts ?: return
        applySettings(language, speaker, speed, pitch)

        val utteranceId = "preview_${System.currentTimeMillis()}"
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _currentUtteranceStatus.value = "Playing..."
            }

            override fun onDone(utteranceId: String?) {
                _currentUtteranceStatus.value = null
                onDone()
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _currentUtteranceStatus.value = null
                onDone()
            }
        })

        val params = Bundle()
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    fun stop() {
        tts?.stop()
        _currentUtteranceStatus.value = null
    }

    suspend fun synthesizeToFile(
        text: String,
        outputFile: File,
        language: LanguageItem,
        speaker: SpeakerPersona,
        speed: Float,
        pitch: Float,
        onProgress: (Float) -> Unit
    ): Result<File> = suspendCancellableCoroutine { continuation ->
        val engine = tts
        if (engine == null) {
            continuation.resume(Result.failure(IllegalStateException("TTS engine not initialized")))
            return@suspendCancellableCoroutine
        }

        applySettings(language, speaker, speed, pitch)

        val utteranceId = "synth_${System.currentTimeMillis()}"
        val params = Bundle()

        outputFile.parentFile?.mkdirs()
        if (outputFile.exists()) {
            outputFile.delete()
        }

        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(id: String?) {
                onProgress(0.2f)
            }

            override fun onDone(id: String?) {
                onProgress(1.0f)
                if (outputFile.exists() && outputFile.length() > 0) {
                    continuation.resume(Result.success(outputFile))
                } else {
                    continuation.resume(Result.failure(IllegalStateException("Generated audio file is empty")))
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(id: String?) {
                continuation.resume(Result.failure(RuntimeException("Speech synthesis failed in engine")))
            }

            override fun onError(id: String?, errorCode: Int) {
                continuation.resume(Result.failure(RuntimeException("Speech synthesis failed with error code $errorCode")))
            }
        })

        onProgress(0.1f)
        val result = engine.synthesizeToFile(text, params, outputFile, utteranceId)
        if (result != TextToSpeech.SUCCESS) {
            continuation.resume(Result.failure(RuntimeException("synthesizeToFile failed with status $result")))
        }

        continuation.invokeOnCancellation {
            engine.stop()
        }
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
