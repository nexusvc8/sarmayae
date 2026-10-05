package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.InvestmentPlanEntity
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringsManager
import com.example.ui.theme.*

@Composable
fun InvestModal(
    plan: InvestmentPlanEntity,
    walletBalance: Double,
    language: AppLanguage,
    currency: String,
    onDismiss: () -> Unit,
    onConfirmInvest: (Double) -> Unit,
    onNavigateDeposit: () -> Unit
) {
    var amountText by remember { mutableStateOf(plan.minDeposit.toInt().toString()) }

    val amount = amountText.toDoubleOrNull() ?: 0.0
    val hasEnoughBalance = walletBalance >= amount
    val isValidAmount = amount >= plan.minDeposit && amount <= plan.maxDeposit
    val canInvest = isValidAmount && hasEnoughBalance

    val dailyProfit = (amount * plan.dailyRoiPercent) / 100.0
    val totalProfit = dailyProfit * plan.durationDays

    fun formatMoney(num: Double): String {
        return if (currency == "USD") {
            String.format("$%.2f", num / 280.0)
        } else {
            String.format("₨ %,d", num.toInt())
        }
    }

    val title = if (language == AppLanguage.URDU) plan.titleUr else plan.titleEn

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = null,
                            tint = Emerald700,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Plan Highlight Badges
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Emerald50,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${plan.dailyRoiPercent}%",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald700
                                )
                            )
                            Text(StringsManager.get("daily_roi", language), fontSize = 11.sp, color = Slate600)
                        }
                        Divider(
                            modifier = Modifier
                                .height(32.dp)
                                .width(1.dp),
                            color = Slate300
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${plan.durationDays} ${StringsManager.get("days", language)}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                            )
                            Text(StringsManager.get("duration", language), fontSize = 11.sp, color = Slate600)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Wallet Balance Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${StringsManager.get("available_balance", language)}: ${formatMoney(walletBalance)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (hasEnoughBalance) Emerald700 else Rose600
                        )
                    )
                    if (!hasEnoughBalance) {
                        TextButton(
                            onClick = {
                                onDismiss()
                                onNavigateDeposit()
                            },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("+ ${StringsManager.get("deposit", language)}", color = Emerald700, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Amount Input
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("invest_amount_input"),
                    shape = RoundedCornerShape(12.dp),
                    label = { Text("${StringsManager.get("min_deposit", language)}: ${formatMoney(plan.minDeposit)}") },
                    leadingIcon = { Text("₨", fontWeight = FontWeight.Bold, color = Emerald700) },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Projected Profits
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Slate100,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(StringsManager.get("daily_profit", language), style = MaterialTheme.typography.bodySmall)
                            Text("+${formatMoney(dailyProfit)}", style = MaterialTheme.typography.bodySmall.copy(color = Emerald700, fontWeight = FontWeight.Bold))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(StringsManager.get("total_net_profit", language), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                            Text("+${formatMoney(totalProfit)}", style = MaterialTheme.typography.bodyMedium.copy(color = Amber700, fontWeight = FontWeight.ExtraBold))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Confirm Button
                Button(
                    onClick = { onConfirmInvest(amount) },
                    enabled = canInvest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_invest_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text(
                        text = if (!hasEnoughBalance) StringsManager.get("insufficient_balance", language) else StringsManager.get("invest_now", language),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
