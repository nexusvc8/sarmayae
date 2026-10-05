package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfitCalculatorModal(
    plans: List<InvestmentPlanEntity>,
    language: AppLanguage,
    currency: String,
    onDismiss: () -> Unit,
    onInvestNow: (InvestmentPlanEntity, Double) -> Unit
) {
    var selectedPlanIndex by remember { mutableIntStateOf(0) }
    var amountText by remember { mutableStateOf("10000") }

    val currentPlan = plans.getOrNull(selectedPlanIndex) ?: plans.firstOrNull()

    val investAmount = amountText.toDoubleOrNull() ?: 0.0
    val dailyRate = currentPlan?.dailyRoiPercent ?: 3.0
    val duration = currentPlan?.durationDays ?: 30

    val dailyProfit = (investAmount * dailyRate) / 100.0
    val weeklyProfit = dailyProfit * 7
    val totalNetProfit = dailyProfit * duration
    val totalPayout = totalNetProfit + investAmount
    val totalRoiPercent = if (investAmount > 0) ((totalNetProfit / investAmount) * 100).toInt() else 0

    fun formatMoney(amount: Double): String {
        return if (currency == "USD") {
            String.format("$%.2f", amount / 280.0)
        } else {
            String.format("₨ %,d", amount.toInt())
        }
    }

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
                    .verticalScroll(rememberScrollState())
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
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = Emerald700,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = StringsManager.get("profit_calculator", language),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = StringsManager.get("calc_desc", language),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Plan Selector Chips
                Text(
                    text = StringsManager.get("select_plan", language),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    plans.forEachIndexed { index, plan ->
                        val isSelected = index == selectedPlanIndex
                        val planName = if (language == AppLanguage.URDU) plan.titleUr else plan.titleEn
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Emerald100 else Slate100,
                            border = if (isSelected) ButtonDefaults.outlinedButtonBorder else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedPlanIndex = index }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = planName,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Emerald900 else Slate800
                                    )
                                )
                                Text(
                                    text = "${plan.dailyRoiPercent}% / ${plan.durationDays}d",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) Emerald700 else Slate600,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Investment Amount Input
                Text(
                    text = StringsManager.get("enter_amount", language),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("calc_amount_input"),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Text(
                            text = if (currency == "USD") "$" else "₨",
                            fontWeight = FontWeight.Bold,
                            color = Emerald700
                        )
                    },
                    singleLine = true
                )

                // Quick Amount Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("1000", "5000", "15000", "50000").forEach { quick ->
                        FilterChip(
                            selected = amountText == quick,
                            onClick = { amountText = quick },
                            label = { Text("₨ $quick", fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Results Summary Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Emerald50,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalcResultRow(
                            label = StringsManager.get("daily_profit", language),
                            value = "+${formatMoney(dailyProfit)} (${dailyRate}%)",
                            highlightColor = Emerald700
                        )
                        CalcResultRow(
                            label = StringsManager.get("weekly_profit", language),
                            value = "+${formatMoney(weeklyProfit)}",
                            highlightColor = Emerald700
                        )
                        CalcResultRow(
                            label = StringsManager.get("total_net_profit", language),
                            value = "+${formatMoney(totalNetProfit)}",
                            highlightColor = Amber700
                        )
                        Divider(color = Slate300)
                        CalcResultRow(
                            label = StringsManager.get("total_payout", language),
                            value = formatMoney(totalPayout),
                            highlightColor = Emerald900,
                            isBold = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Invest Button
                Button(
                    onClick = {
                        currentPlan?.let { onInvestNow(it, investAmount) }
                    },
                    enabled = investAmount >= (currentPlan?.minDeposit ?: 0.0),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text(
                        text = StringsManager.get("invest_now", language),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
private fun CalcResultRow(
    label: String,
    value: String,
    highlightColor: Color,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Slate700,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = highlightColor,
                fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Bold
            )
        )
    }
}
