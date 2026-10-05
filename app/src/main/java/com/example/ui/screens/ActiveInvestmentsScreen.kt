package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActiveInvestmentEntity
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringsManager
import com.example.ui.theme.*

@Composable
fun ActiveInvestmentsScreen(
    activeInvestments: List<ActiveInvestmentEntity>,
    language: AppLanguage,
    currency: String,
    onClaimProfit: () -> Unit,
    onExplorePlans: () -> Unit,
    isClaiming: Boolean,
    modifier: Modifier = Modifier
) {
    val isUrdu = language == AppLanguage.URDU
    val totalInvested = activeInvestments.sumOf { it.investedAmount }
    val totalEarned = activeInvestments.sumOf { it.totalEarnedSoFar }
    val dailyTotal = activeInvestments.sumOf { it.dailyProfitAmount }

    fun formatMoney(num: Double): String {
        return if (currency == "USD") {
            String.format("$%.2f", num / 280.0)
        } else {
            String.format("₨ %,d", num.toInt())
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = StringsManager.get("active_plans", language),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = if (isUrdu) "آپ کے تمام فعال انویسٹمنٹ پیکجز اور روزانہ منافع کی تفصیلات" else "Monitor active packages, daily yields and maturity progress",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        // Summary Hero Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Emerald800),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = StringsManager.get("active_invested", language),
                                style = MaterialTheme.typography.labelMedium.copy(color = Color.White.copy(alpha = 0.8f))
                            )
                            Text(
                                text = formatMoney(totalInvested),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = Amber400,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "+${formatMoney(dailyTotal)}/day",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = Amber400,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.2f))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = StringsManager.get("earned_so_far", language),
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f))
                            )
                            Text(
                                text = formatMoney(totalEarned),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald400
                                )
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (isUrdu) "ایکٹو پلانز" else "Active Plans Count",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f))
                            )
                            Text(
                                text = "${activeInvestments.size} ${StringsManager.get("plans", language)}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Claim Profit Button
                    Button(
                        onClick = onClaimProfit,
                        enabled = !isClaiming && activeInvestments.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("portfolio_claim_profit_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Amber500, contentColor = Slate950)
                    ) {
                        if (isClaiming) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Slate950)
                        } else {
                            Icon(Icons.Default.Bolt, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = StringsManager.get("claim_profit", language),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }

        // Empty state or list
        if (activeInvestments.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = null,
                            tint = Slate400,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = StringsManager.get("no_active_investments", language),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate700
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onExplorePlans,
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                        ) {
                            Text(StringsManager.get("explore_plans", language))
                        }
                    }
                }
            }
        } else {
            items(activeInvestments) { inv ->
                ActiveInvestmentCard(
                    investment = inv,
                    language = language,
                    currency = currency
                )
            }
        }
    }
}

@Composable
fun ActiveInvestmentCard(
    investment: ActiveInvestmentEntity,
    language: AppLanguage,
    currency: String
) {
    val isUrdu = language == AppLanguage.URDU
    val title = if (isUrdu) investment.planTitleUr else investment.planTitleEn
    val progress = (investment.daysCompleted.toFloat() / investment.durationDays.toFloat()).coerceIn(0f, 1f)

    fun formatMoney(num: Double): String {
        return if (currency == "USD") {
            String.format("$%.2f", num / 280.0)
        } else {
            String.format("₨ %,d", num.toInt())
        }
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Emerald100),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "${investment.dailyRoiPercent}% ${StringsManager.get("daily_roi", language)} • ${formatMoney(investment.dailyProfitAmount)}/day",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Emerald700,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (investment.isCompleted) Slate100 else Emerald50
                ) {
                    Text(
                        text = if (investment.isCompleted) StringsManager.get("completed_status", language) else StringsManager.get("active_status", language),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (investment.isCompleted) Slate700 else Emerald800,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Bar (Days Completed)
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${StringsManager.get("progress", language)}: ${investment.daysCompleted}/${investment.durationDays} ${StringsManager.get("days", language)}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate600)
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Emerald700)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Emerald600,
                    trackColor = Emerald100
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stats row (Earned so far vs Expected Target)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Slate50)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = StringsManager.get("earned_so_far", language),
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate500, fontSize = 10.sp)
                    )
                    Text(
                        text = formatMoney(investment.totalEarnedSoFar),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Emerald800
                        )
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = StringsManager.get("total_target", language),
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate500, fontSize = 10.sp)
                    )
                    Text(
                        text = formatMoney(investment.totalExpectedProfit),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Amber700
                        )
                    )
                }
            }
        }
    }
}

private val Emerald400 = Color(0xFF34D399)
