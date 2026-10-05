package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.example.data.model.InvestmentPlanEntity
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringsManager
import com.example.ui.theme.*

@Composable
fun PlanCard(
    plan: InvestmentPlanEntity,
    language: AppLanguage,
    currency: String,
    onSelectPlan: (InvestmentPlanEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    fun formatMoney(amount: Double): String {
        return if (currency == "USD") {
            String.format("$%.0f", amount / 280.0)
        } else {
            String.format("₨ %,d", amount.toInt())
        }
    }

    val totalProfitPercent = (plan.dailyRoiPercent * plan.durationDays).toInt()
    val totalPayoutPercent = if (plan.isCapitalReturned) totalProfitPercent + 100 else totalProfitPercent

    val isUrdu = language == AppLanguage.URDU
    val title = if (isUrdu) plan.titleUr else plan.titleEn
    val desc = if (isUrdu) plan.descriptionUr else plan.descriptionEn
    val tag = if (isUrdu) plan.tagUr else plan.tagEn

    val isVip = plan.id.contains("vip") || plan.id.contains("gold")
    val isPopular = plan.id.contains("silver")

    val borderColor = when {
        isVip -> Amber500
        isPopular -> Emerald500
        else -> Slate200
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (isVip || isPopular) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row with Title and Badge
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
                        text = desc,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        ),
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Tag Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isVip) Amber100 else if (isPopular) Emerald100 else Slate100
                ) {
                    Text(
                        text = tag,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isVip) Amber700 else if (isPopular) Emerald800 else Slate700,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ROI Highlight Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isVip) Amber50 else if (isPopular) Emerald50 else Slate100
                    )
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${plan.dailyRoiPercent}%",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isVip) Amber700 else if (isPopular) Emerald700 else Slate900
                        )
                    )
                    Text(
                        text = StringsManager.get("daily_roi", language),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate600,
                            fontSize = 11.sp
                        )
                    )
                }

                Divider(
                    modifier = Modifier
                        .height(30.dp)
                        .width(1.dp),
                    color = Slate300
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${plan.durationDays} ${StringsManager.get("days", language)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = StringsManager.get("duration", language),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate600,
                            fontSize = 11.sp
                        )
                    )
                }

                Divider(
                    modifier = Modifier
                        .height(30.dp)
                        .width(1.dp),
                    color = Slate300
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$totalPayoutPercent%",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Emerald700
                        )
                    )
                    Text(
                        text = StringsManager.get("total_return", language),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate600,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Limits and Features
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = StringsManager.get("min_deposit", language),
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                    )
                    Text(
                        text = formatMoney(plan.minDeposit),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = StringsManager.get("max_deposit", language),
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                    )
                    Text(
                        text = formatMoney(plan.maxDeposit),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Button
            Button(
                onClick = { onSelectPlan(plan) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("select_plan_${plan.id}")
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isVip) Amber600 else Emerald700,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.RocketLaunch,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = StringsManager.get("choose_plan", language),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
