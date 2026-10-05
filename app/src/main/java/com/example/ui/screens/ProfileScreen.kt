package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserAccountEntity
import com.example.ui.components.LanguageSegmentedControl
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringsManager
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    user: UserAccountEntity,
    language: AppLanguage,
    currency: String,
    onLanguageChange: (AppLanguage) -> Unit,
    onCurrencyToggle: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onShowKycDialog: () -> Unit,
    onOpenLanguageDialog: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isUrdu = language == AppLanguage.URDU
    var isDisclaimerOpen by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Profile Header Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Emerald100),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(18.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Emerald700),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(2).uppercase(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Emerald600, modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = user.phone,
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
                        )
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.labelSmall.copy(color = Slate500, fontSize = 11.sp)
                        )
                    }
                }
            }
        }

        // Settings Category Header
        item {
            Text(
                text = StringsManager.get("security_settings", language),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }

        // Language Selector Setting Component
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Emerald100),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_language_setting_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Emerald50),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    tint = Emerald700,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = StringsManager.get("language_setting", language),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = if (language == AppLanguage.URDU) "قومی زبان اردو منتخب ہے" else "English is currently active",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Emerald700,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }

                        IconButton(
                            onClick = onOpenLanguageDialog,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = "Open Language Dialog",
                                tint = Emerald700,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Language Segmented Control Bar
                    LanguageSegmentedControl(
                        selectedLanguage = language,
                        onLanguageSelected = onLanguageChange,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Currency Setting
        item {
            SettingRowCard(
                icon = Icons.Default.CurrencyExchange,
                title = StringsManager.get("currency_setting", language),
                subtitle = if (currency == "PKR") "Pakistani Rupee (₨)" else "US Dollar ($)",
                trailing = {
                    Button(
                        onClick = onCurrencyToggle,
                        colors = ButtonDefaults.buttonColors(containerColor = Amber500, contentColor = Slate950),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(currency, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        // KYC Verification Setting
        item {
            SettingRowCard(
                icon = Icons.Default.Shield,
                title = "KYC Verification",
                subtitle = if (user.isKycVerified) "CNIC / Identity Verified" else "Submit ID Card",
                trailing = {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (user.isKycVerified) Emerald100 else Amber100
                    ) {
                        Text(
                            text = if (user.isKycVerified) "Verified ✓" else "Pending",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (user.isKycVerified) Emerald800 else Amber700,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            )
        }

        // Dark Mode Toggle
        item {
            SettingRowCard(
                icon = Icons.Default.DarkMode,
                title = StringsManager.get("dark_mode", language),
                subtitle = if (user.isDarkMode) "Enabled" else "Disabled",
                trailing = {
                    Switch(
                        checked = user.isDarkMode,
                        onCheckedChange = { onToggleDarkMode() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Emerald700)
                    )
                }
            )
        }

        // Financial Disclaimer & Terms
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate100),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isDisclaimerOpen = true }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Slate600)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = StringsManager.get("terms", language),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Slate500)
                }
            }
        }
    }

    if (isDisclaimerOpen) {
        AlertDialog(
            onDismissRequest = { isDisclaimerOpen = false },
            title = { Text(StringsManager.get("terms", language), fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = if (isUrdu) {
                        "Sarmaya Invest ایک شفاف آن لائن ارننگ اور فنانشل پورٹ فولیو مینیجر ہے۔ تمام سرمایہ کار اپنی صوابدید کے مطابق پلانز کا انتخاب کرتے ہیں۔ منافع جات خودکار الیکٹرانک ٹریڈنگ اور بزنس ریٹرنز سے تقسیم کیے جاتے ہیں۔"
                    } else {
                        "Sarmaya Invest provides transparent automated yield packages and digital portfolio management. All transactions and daily returns are processed with bank-grade encryption."
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(onClick = { isDisclaimerOpen = false }, colors = ButtonDefaults.buttonColors(containerColor = Emerald700)) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun SettingRowCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Slate200),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Emerald50),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = Emerald700, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface))
                    Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp))
                }
            }
            trailing()
        }
    }
}
