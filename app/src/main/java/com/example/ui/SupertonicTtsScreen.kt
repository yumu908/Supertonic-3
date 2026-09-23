package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SynthesisStatus
import com.example.data.model.TtsPresets
import com.example.ui.components.AudioHistoryDialog
import com.example.ui.components.AudioPlayerCard
import com.example.ui.components.FineTuningControls
import com.example.ui.components.LanguageSelector
import com.example.ui.components.SpeakerSelector
import com.example.ui.components.SupertonicHeader
import com.example.ui.components.TextInputSection
import com.example.ui.theme.StudioBackground
import com.example.ui.viewmodel.TtsViewModel

@Composable
fun SupertonicTtsScreen(
    viewModel: TtsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val clipHistory by viewModel.clipHistory.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var showHistoryDialog by remember { mutableStateOf(false) }

    // Notify user of actions
    LaunchedEffect(uiState.notificationMessage) {
        uiState.notificationMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.dismissNotification()
        }
    }

    if (showHistoryDialog) {
        AudioHistoryDialog(
            clips = clipHistory,
            onPlayClip = { clip ->
                viewModel.playClip(clip)
                showHistoryDialog = false
            },
            onExportClip = { clip, ctx -> viewModel.exportClipToStorage(clip, ctx) },
            onShareClip = { clip, ctx -> viewModel.shareClip(clip, ctx) },
            onDeleteClip = { clip -> viewModel.deleteClip(clip) },
            onDismiss = { showHistoryDialog = false }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = StudioBackground,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(StudioBackground),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 760.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Header with Lightning Brand & Saved Clips Count
                SupertonicHeader(
                    savedClipsCount = clipHistory.size,
                    onOpenHistory = { showHistoryDialog = true }
                )

                // 2. Speaker Selector Chips + Voice Audition
                SpeakerSelector(
                    speakers = TtsPresets.SPEAKERS,
                    selectedSpeaker = uiState.selectedSpeaker,
                    onSpeakerSelected = { viewModel.selectSpeaker(it) },
                    onPreviewVoice = { viewModel.previewVoice() }
                )

                // 3. Language Selector Quick Bar + Full Picker
                LanguageSelector(
                    languages = TtsPresets.LANGUAGES,
                    selectedLanguage = uiState.selectedLanguage,
                    onLanguageSelected = { viewModel.selectLanguage(it) }
                )

                // 4. Text Editor with Mode Tabs (Freeform, Quote, Paragraph, Script) & Char Counter
                TextInputSection(
                    text = uiState.text,
                    selectedPresetType = uiState.selectedPresetType,
                    onTextChanged = { viewModel.updateText(it) },
                    onPresetTypeSelected = { viewModel.selectPresetType(it) }
                )

                // 5. Fine Tuning: Quality Steps, Speed Slider, Pitch Slider & "⚡ Generate Speech" Button
                FineTuningControls(
                    quality = uiState.selectedQuality,
                    speed = uiState.speed,
                    pitch = uiState.pitch,
                    synthesisStatus = uiState.synthesisStatus,
                    onQualitySelected = { viewModel.selectQuality(it) },
                    onSpeedChanged = { viewModel.updateSpeed(it) },
                    onPitchChanged = { viewModel.updatePitch(it) },
                    onGenerateSpeech = { viewModel.generateSpeech() }
                )

                // 6. Active Audio Player with Dynamic Waveform & Export Actions
                val activeClip = playbackState.activeClip ?: (uiState.synthesisStatus as? SynthesisStatus.Success)?.clip
                if (activeClip != null) {
                    AudioPlayerCard(
                        clip = activeClip,
                        playbackState = playbackState,
                        onTogglePlayPause = { viewModel.togglePlayPause() },
                        onStop = { viewModel.stopAudio() },
                        onSeek = { viewModel.seekTo(it) },
                        onExport = { clip, ctx -> viewModel.exportClipToStorage(clip, ctx) },
                        onShare = { clip, ctx -> viewModel.shareClip(clip, ctx) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
