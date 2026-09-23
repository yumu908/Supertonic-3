package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LanguageItem
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import com.example.ui.theme.SupertonicYellow

@Composable
fun LanguageSelector(
    languages: List<LanguageItem>,
    selectedLanguage: LanguageItem,
    onLanguageSelected: (LanguageItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPickerDialog by remember { mutableStateOf(false) }

    if (showPickerDialog) {
        LanguagePickerDialog(
            languages = languages,
            selectedLanguage = selectedLanguage,
            onLanguageSelected = onLanguageSelected,
            onDismiss = { showPickerDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(StudioSurface)
            .border(1.dp, StudioCardBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    tint = SupertonicYellow,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Language:",
                    color = StudioTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${selectedLanguage.flag} ${selectedLanguage.name}",
                    color = SupertonicYellow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            OutlinedButton(
                onClick = { showPickerDialog = true },
                modifier = Modifier.testTag("all_languages_button"),
                shape = RoundedCornerShape(8.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(StudioCardBorder)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = StudioTextSecondary
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "All (33)",
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.padding(top = 10.dp))

        // Horizontal language chip bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // First 12 popular languages as quick chips
            languages.take(12).forEach { lang ->
                val isSelected = lang.code == selectedLanguage.code
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) SupertonicYellow.copy(alpha = 0.18f)
                            else StudioSurfaceVariant
                        )
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) SupertonicYellow else StudioCardBorder.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onLanguageSelected(lang) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("quick_lang_${lang.code}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = lang.flag, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = lang.name,
                            color = if (isSelected) SupertonicYellow else StudioTextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
