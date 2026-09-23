package com.example.ui.components

import android.content.Context
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioClip
import com.example.data.model.PlaybackState
import com.example.data.storage.AudioExportHelper
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import com.example.ui.theme.SupertonicCyan
import com.example.ui.theme.SupertonicYellow

@Composable
fun AudioPlayerCard(
    clip: AudioClip,
    playbackState: PlaybackState,
    onTogglePlayPause: () -> Unit,
    onStop: () -> Unit,
    onSeek: (Int) -> Unit,
    onExport: (AudioClip, Context) -> Unit,
    onShare: (AudioClip, Context) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isPlaying = playbackState.isPlaying
    val currentPosition = playbackState.currentPositionMs
    val totalDuration = if (playbackState.totalDurationMs > 0) playbackState.totalDurationMs else clip.durationMs.toInt()

    // Dynamic wave animation when playing
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "waveScale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(StudioSurface)
            .border(1.dp, SupertonicYellow.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag("audio_player_card")
    ) {
        // Top row: Title and format badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Generated Master Audio",
                    color = SupertonicYellow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${clip.speakerName} · ${clip.languageCode} (${AudioExportHelper.formatFileSize(clip.fileSizeBytes)})",
                    color = StudioTextSecondary,
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "WAV PCM",
                    color = SupertonicCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Animated Soundwave frequency visualizer bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(StudioBackground)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val barCount = 32
            for (i in 0 until barCount) {
                val progressFraction = if (totalDuration > 0) currentPosition.toFloat() / totalDuration else 0f
                val isPlayed = (i.toFloat() / barCount) <= progressFraction

                // Pseudo pattern for wave heights
                val basePattern = kotlin.math.sin(i * 0.4).toFloat() * 0.5f + 0.5f
                val barHeight = if (isPlaying) {
                    ((basePattern * 0.7f + waveScale * 0.3f) * 36).dp.coerceIn(6.dp, 38.dp)
                } else {
                    (basePattern * 24).dp.coerceIn(4.dp, 30.dp)
                }

                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(barHeight)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (isPlayed) SupertonicYellow
                            else StudioCardBorder
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Seekbar slider
        Slider(
            value = currentPosition.toFloat().coerceIn(0f, totalDuration.toFloat().coerceAtLeast(1f)),
            onValueChange = { onSeek(it.toInt()) },
            valueRange = 0f..totalDuration.toFloat().coerceAtLeast(1f),
            colors = SliderDefaults.colors(
                thumbColor = SupertonicYellow,
                activeTrackColor = SupertonicYellow,
                inactiveTrackColor = StudioCardBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .testTag("audio_seekbar")
        )

        // Time indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = AudioExportHelper.formatDuration(currentPosition),
                color = StudioTextPrimary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = AudioExportHelper.formatDuration(totalDuration),
                color = StudioTextMuted,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Playback buttons & Export actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Play/Pause and Stop
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledIconButton(
                    onClick = onTogglePlayPause,
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("play_pause_button"),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = SupertonicYellow,
                        contentColor = Color(0xFF0F172A)
                    )
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(26.dp)
                    )
                }

                FilledIconButton(
                    onClick = onStop,
                    modifier = Modifier.size(38.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = StudioSurfaceVariant,
                        contentColor = StudioTextSecondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Export & Share buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { onExport(clip, context) },
                    modifier = Modifier.testTag("export_button"),
                    shape = RoundedCornerShape(10.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(StudioCardBorder)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = StudioSurfaceVariant,
                        contentColor = StudioTextPrimary
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Save to Music",
                        modifier = Modifier.size(16.dp),
                        tint = SupertonicYellow
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Save WAV", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { onShare(clip, context) },
                    modifier = Modifier.testTag("share_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SupertonicYellow.copy(alpha = 0.2f),
                        contentColor = SupertonicYellow
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SupertonicYellow.copy(alpha = 0.5f)),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Audio",
                        modifier = Modifier.size(16.dp),
                        tint = SupertonicYellow
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Share", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
