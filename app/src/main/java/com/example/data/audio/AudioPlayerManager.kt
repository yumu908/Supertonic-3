package com.example.data.audio

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.data.model.AudioClip
import com.example.data.model.PlaybackState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

class AudioPlayerManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    fun loadAndPlay(clip: AudioClip) {
        val file = File(clip.filePath)
        if (!file.exists()) {
            Log.e("AudioPlayerManager", "Audio file does not exist at ${clip.filePath}")
            return
        }

        stop()

        try {
            val player = MediaPlayer().apply {
                setDataSource(context, Uri.fromFile(file))
                prepare()
                setOnCompletionListener {
                    _playbackState.update { current ->
                        current.copy(isPlaying = false, currentPositionMs = 0)
                    }
                    stopProgressTracking()
                }
            }
            mediaPlayer = player

            val duration = player.duration
            _playbackState.update {
                PlaybackState(
                    isPlaying = true,
                    currentPositionMs = 0,
                    totalDurationMs = if (duration > 0) duration else clip.durationMs.toInt(),
                    activeClip = clip
                )
            }

            player.start()
            startProgressTracking()
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Failed to start audio playback: ${e.message}", e)
        }
    }

    fun togglePlayPause() {
        val player = mediaPlayer ?: return
        if (player.isPlaying) {
            player.pause()
            stopProgressTracking()
            _playbackState.update { it.copy(isPlaying = false) }
        } else {
            player.start()
            startProgressTracking()
            _playbackState.update { it.copy(isPlaying = true) }
        }
    }

    fun seekTo(positionMs: Int) {
        val player = mediaPlayer ?: return
        player.seekTo(positionMs)
        _playbackState.update { it.copy(currentPositionMs = positionMs) }
    }

    fun stop() {
        stopProgressTracking()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.w("AudioPlayerManager", "Error stopping player: ${e.message}")
        }
        mediaPlayer = null
        _playbackState.update { it.copy(isPlaying = false, currentPositionMs = 0) }
    }

    private fun startProgressTracking() {
        stopProgressTracking()
        progressJob = scope.launch {
            while (isActive) {
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        val current = player.currentPosition
                        val total = player.duration
                        _playbackState.update {
                            it.copy(
                                isPlaying = true,
                                currentPositionMs = current,
                                totalDurationMs = if (total > 0) total else it.totalDurationMs
                            )
                        }
                    }
                }
                delay(60)
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stop()
    }
}
