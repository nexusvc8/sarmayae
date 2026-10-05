package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringsManager
import com.example.ui.theme.*

/**
 * 1. LanguageSegmentedControl
 * High-end sliding segmented pill switch between English and Urdu.
 * Perfect for embedding in App Bars, Settings, Profile, or Banner headers.
 */
@Composable
fun LanguageSegmentedControl(
    selectedLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    val languages = listOf(AppLanguage.ENGLISH, AppLanguage.URDU)

    Surface(
        modifier = modifier
            .testTag("language_segmented_control")
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            languages.forEach { lang ->
                val isSelected = selectedLanguage == lang
                val testTag = if (lang == AppLanguage.ENGLISH) "lang_btn_en" else "lang_btn_ur"

                val animatedBgColor by animateColorAsState(
                    targetValue = if (isSelected) Emerald700 else Color.Transparent,
                    animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
                    label = "bg_color"
                )
                val animatedTextColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
                    label = "text_color"
                )

                Box(
                    modifier = Modifier
                        .testTag(testTag)
                        .clip(RoundedCornerShape(20.dp))
                        .background(animatedBgColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true)
                        ) {
                            if (!isSelected) {
                                onLanguageSelected(lang)
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = lang.flag,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = lang.displayName,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = animatedTextColor,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * 2. LanguageSwitcherCompactPill
 * Compact, tactile action pill designed specifically for the Top App Bar.
 * Displays current flag + language name with subtle chevron / quick action.
 */
@Composable
fun LanguageSwitcherCompactPill(
    currentLanguage: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .testTag("lang_compact_pill")
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true)
            ) { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = Emerald50,
        border = BorderStroke(1.2.dp, Emerald600.copy(alpha = 0.5f)),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = currentLanguage.flag,
                fontSize = 13.sp
            )
            Text(
                text = if (currentLanguage == AppLanguage.URDU) "اردو" else "EN",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Emerald900,
                    letterSpacing = 0.3.sp
                )
            )
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = "Switch Language",
                tint = Emerald700,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

/**
 * 3. LanguageSelectionDialog
 * Comprehensive modal dialog for selecting between English and Urdu.
 * Provides clear explanations, native naming, direction indicators, and instant selection.
 */
@Composable
fun LanguageSelectionDialog(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    var tempSelectedLanguage by remember { mutableStateOf(currentLanguage) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("language_selection_dialog"),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Globe Icon
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Emerald800, Emerald600)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = Amber400,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = StringsManager.get("language_switcher_title", tempSelectedLanguage),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = StringsManager.get("language_switcher_subtitle", tempSelectedLanguage),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Language Option Cards
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    LanguageOptionCard(
                        language = AppLanguage.URDU,
                        isSelected = tempSelectedLanguage == AppLanguage.URDU,
                        title = StringsManager.get("urdu_title", tempSelectedLanguage),
                        subtitle = StringsManager.get("urdu_subtitle", tempSelectedLanguage),
                        onClick = {
                            tempSelectedLanguage = AppLanguage.URDU
                        },
                        testTag = "lang_card_urdu"
                    )

                    LanguageOptionCard(
                        language = AppLanguage.ENGLISH,
                        isSelected = tempSelectedLanguage == AppLanguage.ENGLISH,
                        title = StringsManager.get("english_title", tempSelectedLanguage),
                        subtitle = StringsManager.get("english_subtitle", tempSelectedLanguage),
                        onClick = {
                            tempSelectedLanguage = AppLanguage.ENGLISH
                        },
                        testTag = "lang_card_english"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (tempSelectedLanguage == AppLanguage.URDU) "منسوخ کریں" else "Cancel",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Button(
                        onClick = {
                            onLanguageSelected(tempSelectedLanguage)
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("apply_language_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = StringsManager.get("apply_language", tempSelectedLanguage),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageOptionCard(
    language: AppLanguage,
    isSelected: Boolean,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Emerald600 else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
        label = "border_color"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Emerald50.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surface,
        label = "bg_color"
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true)
            ) { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Flag Container
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Emerald100 else Slate100),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = language.flag,
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (isSelected) Emerald900 else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Emerald700
                            ) {
                                Text(
                                    text = if (language == AppLanguage.URDU) "فعال" else "Active",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isSelected) Emerald800.copy(alpha = 0.85f) else Slate500,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Emerald700,
                    unselectedColor = Slate400
                )
            )
        }
    }
}

/**
 * 4. LanguageBannerPrompt
 * A welcoming, non-intrusive card that allows quick language switching directly
 * within main views with 1 tap.
 */
@Composable
fun LanguageBannerPrompt(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Emerald50,
        border = BorderStroke(1.dp, Emerald200),
        modifier = modifier
            .fillMaxWidth()
            .testTag("language_banner_prompt")
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Translate,
                    contentDescription = null,
                    tint = Emerald700,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (currentLanguage == AppLanguage.URDU) "زبان منتخب کریں" else "Language Preference",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Emerald900
                        )
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.URDU) "انگریزی یا اردو میں استعمال کریں" else "Switch between English & Urdu anytime",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Slate600,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            LanguageSegmentedControl(
                selectedLanguage = currentLanguage,
                onLanguageSelected = onLanguageSelected
            )
        }
    }
}

private val Emerald200 = Color(0xFFA7F3D0)
