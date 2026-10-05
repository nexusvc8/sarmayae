package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserAccountEntity
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringsManager
import com.example.ui.theme.*

@Composable
fun BalanceCard(
    user: UserAccountEntity,
    language: AppLanguage,
    currency: String,
    onDepositClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    onInvestClick: () -> Unit,
    onCalculatorClick: () -> Unit,
    onClaimProfitClick: () -> Unit,
    isClaiming: Boolean,
    modifier: Modifier = Modifier
) {
    var isBalanceVisible by remember { mutableStateOf(true) }

    fun formatMoney(amount: Double): String {
        return if (currency == "USD") {
            String.format("$%.2f", amount / 280.0)
        } else {
            String.format("₨ %,d", amount.toInt())
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF064E3B), // Deep Emerald
                        Color(0xFF0A5C47),
                        Color(0xFF047857)
                    )
                )
            )
            .padding(20.dp)
    ) {
        // User Greeting & KYC tag
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (language == AppLanguage.URDU) "خوش آمدید، ${user.name}" else "Welcome back, ${user.name}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = "${StringsManager.get("member_id", language)}: #${user.id}992",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Emerald100.copy(alpha = 0.8f)
                    )
                )
            }

            // KYC Verified Pill
            Surface(
                color = if (user.isKycVerified) Emerald500.copy(alpha = 0.25f) else Amber500.copy(alpha = 0.25f),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (user.isKycVerified) Icons.Default.Verified else Icons.Default.Security,
                        contentDescription = null,
                        tint = if (user.isKycVerified) Emerald100 else Amber400,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (user.isKycVerified) StringsManager.get("kyc_verified", language) else StringsManager.get("kyc_pending", language),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Balance Display
        Text(
            text = StringsManager.get("total_balance", language),
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color.White.copy(alpha = 0.75f),
                fontWeight = FontWeight.Medium
            )
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isBalanceVisible) formatMoney(user.balance) else "••••••••",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(
                onClick = { isBalanceVisible = !isBalanceVisible },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle Balance",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Stats Mini Grid (Invested, Total Profit, Today's Return)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.2f))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BalanceSubStat(
                title = StringsManager.get("active_invested", language),
                value = if (isBalanceVisible) formatMoney(user.totalInvested) else "••••",
                icon = Icons.Default.Savings,
                iconColor = Cyan600
            )
            Divider(
                modifier = Modifier
                    .height(36.dp)
                    .width(1.dp),
                color = Color.White.copy(alpha = 0.15f)
            )
            BalanceSubStat(
                title = StringsManager.get("todays_profit", language),
                value = if (isBalanceVisible) "+${formatMoney(user.todayProfit)}" else "••••",
                icon = Icons.Default.TrendingUp,
                iconColor = Amber400
            )
            Divider(
                modifier = Modifier
                    .height(36.dp)
                    .width(1.dp),
                color = Color.White.copy(alpha = 0.15f)
            )
            BalanceSubStat(
                title = StringsManager.get("total_profit", language),
                value = if (isBalanceVisible) formatMoney(user.totalProfitEarned) else "••••",
                icon = Icons.Default.MonetizationOn,
                iconColor = Emerald400
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Claim Daily Profit Banner Button
        Button(
            onClick = onClaimProfitClick,
            enabled = !isClaiming,
            colors = ButtonDefaults.buttonColors(
                containerColor = Amber500,
                contentColor = Slate950
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("claim_profit_btn")
                .height(48.dp)
        ) {
            if (isClaiming) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Slate950,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = Slate950,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = StringsManager.get("claim_profit", language),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4 Action Buttons Row: Deposit, Withdraw, Invest, Calculator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickActionButton(
                title = StringsManager.get("deposit", language),
                icon = Icons.Default.AddCircle,
                bgGradient = listOf(Color(0xFF10B981), Color(0xFF059669)),
                onClick = onDepositClick,
                testTag = "action_deposit"
            )
            QuickActionButton(
                title = StringsManager.get("withdraw", language),
                icon = Icons.Default.ArrowCircleUp,
                bgGradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
                onClick = onWithdrawClick,
                testTag = "action_withdraw"
            )
            QuickActionButton(
                title = StringsManager.get("invest_now", language),
                icon = Icons.Default.AccountBalance,
                bgGradient = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)),
                onClick = onInvestClick,
                testTag = "action_invest"
            )
            QuickActionButton(
                title = StringsManager.get("calculator", language),
                icon = Icons.Default.Calculate,
                bgGradient = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
                onClick = onCalculatorClick,
                testTag = "action_calculator"
            )
        }
    }
}

@Composable
private fun BalanceSubStat(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color
) {
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        )
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    icon: ImageVector,
    bgGradient: List<Color>,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .testTag(testTag)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(bgGradient)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp
            )
        )
    }
}

private val Emerald400 = Color(0xFF34D399)
