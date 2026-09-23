package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QualityStep
import com.example.data.model.SynthesisStatus
import com.example.data.model.TtsPresets
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import com.example.ui.theme.SupertonicAmberGlow
import com.example.ui.theme.SupertonicGold
import com.example.ui.theme.SupertonicYellow
import java.util.Locale

@Composable
fun FineTuningControls(
    quality: QualityStep,
    speed: Float,
    pitch: Float,
    synthesisStatus: SynthesisStatus,
    onQualitySelected: (QualityStep) -> Unit,
    onSpeedChanged: (Float) -> Unit,
    onPitchChanged: (Float) -> Unit,
    onGenerateSpeech: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showQualityDropdown by remember { mutableStateOf(false) }
    val isSynthesizing = synthesisStatus is SynthesisStatus.Processing

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(StudioSurface)
            .border(1.dp, StudioCardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Controls Row 1: Quality & Speed Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quality Dropdown Chip
            Column(modifier = Modifier.width(130.dp)) {
                Text(
                    text = "Quality",
                    color = StudioTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(StudioSurfaceVariant)
                            .border(1.dp, StudioCardBorder, RoundedCornerShape(8.dp))
                            .clickable { showQualityDropdown = true }
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                            .testTag("quality_selector"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = quality.label,
                            color = StudioTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "▼",
                            color = StudioTextMuted,
                            fontSize = 9.sp
                        )
                    }

                    DropdownMenu(
                        expanded = showQualityDropdown,
                        onDismissRequest = { showQualityDropdown = false },
                        modifier = Modifier
                            .background(StudioSurface)
                            .border(1.dp, StudioCardBorder, RoundedCornerShape(8.dp))
                    ) {
                        TtsPresets.QUALITY_OPTIONS.forEach { opt ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = opt.label,
                                            color = if (opt.steps == quality.steps) SupertonicYellow else StudioTextPrimary,
                                            fontWeight = if (opt.steps == quality.steps) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text(
                                            text = opt.description,
                                            color = StudioTextMuted,
                                            fontSize = 10.sp
                                        )
                                    }
                                },
                                onClick = {
                                    onQualitySelected(opt)
                                    showQualityDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            // Speech Speed Slider
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = StudioTextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Speech Speed",
                            color = StudioTextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = String.format(Locale.US, "%.2fx", speed),
                            color = SupertonicYellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (speed != 1.00f) {
                            IconButton(
                                onClick = { onSpeedChanged(1.00f) },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RestartAlt,
                                    contentDescription = "Reset Speed",
                                    tint = StudioTextMuted,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }

                Slider(
                    value = speed,
                    onValueChange = onSpeedChanged,
                    valueRange = 0.5f..2.0f,
                    steps = 5,
                    colors = SliderDefaults.colors(
                        thumbColor = SupertonicYellow,
                        activeTrackColor = SupertonicYellow,
                        inactiveTrackColor = StudioCardBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .testTag("speed_slider")
                )
            }
        }

        // Controls Row 2: Pitch Slider (音调调节)
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = StudioTextSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Pitch (音调)",
                        color = StudioTextMuted,
                        fontSize = 11.sp
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val pitchLabel = when {
                        pitch < 0.85f -> "Deep"
                        pitch > 1.2f -> "High"
                        else -> "Normal"
                    }
                    Text(
                        text = "${String.format(Locale.US, "%.2fx", pitch)} ($pitchLabel)",
                        color = SupertonicYellow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (pitch != 1.00f) {
                        IconButton(
                            onClick = { onPitchChanged(1.00f) },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = "Reset Pitch",
                                tint = StudioTextMuted,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }

            Slider(
                value = pitch,
                onValueChange = onPitchChanged,
                valueRange = 0.5f..2.0f,
                steps = 5,
                colors = SliderDefaults.colors(
                    thumbColor = SupertonicYellow,
                    activeTrackColor = SupertonicYellow,
                    inactiveTrackColor = StudioCardBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .testTag("pitch_slider")
            )
        }

        // Big Primary Action: "⚡ Generate Speech" (exactly matches screenshot)
        Button(
            onClick = onGenerateSpeech,
            enabled = !isSynthesizing,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("generate_speech_button"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = StudioSurfaceVariant
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    listOf(SupertonicYellow, SupertonicGold, SupertonicAmberGlow)
                )
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                SupertonicYellow.copy(alpha = 0.15f),
                                SupertonicGold.copy(alpha = 0.25f),
                                SupertonicYellow.copy(alpha = 0.15f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSynthesizing) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = SupertonicYellow,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = (synthesisStatus as? SynthesisStatus.Processing)?.step ?: "Synthesizing...",
                            color = SupertonicYellow,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = SupertonicYellow,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Generate Speech",
                            color = SupertonicYellow,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}
