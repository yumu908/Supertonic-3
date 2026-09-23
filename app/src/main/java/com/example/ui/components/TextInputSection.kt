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
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.Segment
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TextPresetType
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import com.example.ui.theme.SupertonicYellow

@Composable
fun TextInputSection(
    text: String,
    selectedPresetType: TextPresetType,
    onTextChanged: (String) -> Unit,
    onPresetTypeSelected: (TextPresetType) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val charCount = text.length
    val wordCount = if (text.isBlank()) 0 else text.trim().split(Regex("\\s+")).size

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(StudioBackground)
            .border(1.dp, StudioCardBorder, RoundedCornerShape(16.dp))
    ) {
        // Main Text Editor Area
        OutlinedTextField(
            value = text,
            onValueChange = onTextChanged,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .testTag("tts_text_input"),
            placeholder = {
                Text(
                    text = "Enter text to synthesize into high-quality speech...",
                    color = StudioTextMuted,
                    fontSize = 14.sp
                )
            },
            textStyle = TextStyle(
                color = StudioTextPrimary,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                fontFamily = FontFamily.Default
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = StudioBackground,
                unfocusedContainerColor = StudioBackground,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = SupertonicYellow
            ),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        )

        // Bottom Bar (matching the screenshot: "≡ Freeform  Quote  Paragraph  Script" on left, char count on right)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(StudioSurface)
                .border(
                    width = 1.dp,
                    color = StudioCardBorder.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Mode Selector Chips
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Segment,
                    contentDescription = null,
                    tint = StudioTextMuted,
                    modifier = Modifier.size(16.dp)
                )

                TextPresetType.values().forEach { preset ->
                    val isSelected = preset == selectedPresetType
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isSelected) SupertonicYellow.copy(alpha = 0.2f)
                                else Color.Transparent
                            )
                            .clickable { onPresetTypeSelected(preset) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("preset_${preset.name.lowercase()}")
                    ) {
                        Text(
                            text = preset.label,
                            color = if (isSelected) SupertonicYellow else StudioTextMuted,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // Right: Character count + quick action buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "$charCount chars",
                    color = StudioTextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )

                // Paste button
                IconButton(
                    onClick = {
                        val clip = clipboardManager.getText()?.text
                        if (!clip.isNullOrBlank()) {
                            onTextChanged(clip)
                        }
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = "Paste",
                        tint = StudioTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Copy button
                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(text))
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Text",
                        tint = StudioTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Clear button
                IconButton(
                    onClick = { onTextChanged("") },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = StudioTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
