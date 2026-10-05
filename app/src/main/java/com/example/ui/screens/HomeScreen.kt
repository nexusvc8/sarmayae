package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.components.BalanceCard
import com.example.ui.components.LanguageBannerPrompt
import com.example.ui.components.PlanCard
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringsManager
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    user: UserAccountEntity,
    plans: List<InvestmentPlanEntity>,
    transactions: List<TransactionEntity>,
    language: AppLanguage,
    currency: String,
    onNavigate: (String) -> Unit,
    onDepositClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    onInvestClick: () -> Unit,
    onCalculatorClick: () -> Unit,
    onClaimProfitClick: () -> Unit,
    onSelectPlan: (InvestmentPlanEntity) -> Unit,
    onSpinClick: () -> Unit,
    onShowReceipt: (TransactionEntity) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit = {},
    isClaimingProfit: Boolean,
    modifier: Modifier = Modifier
) {
    val isUrdu = language == AppLanguage.URDU

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Quick Language Switcher Banner
        LanguageBannerPrompt(
            currentLanguage = language,
            onLanguageSelected = onLanguageChange
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Hero Visual Banner with image
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.img_invest_hero),
                    contentDescription = "Investment Hero",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.75f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(16.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Amber500
                    ) {
                        Text(
                            text = if (isUrdu) "آٹو ارننگ سسٹم" else "Automated ROI System",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate950,
                                fontSize = 10.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isUrdu) "روزانہ 2.5% سے 6.5% منافع کمائیں" else "Earn 2.5% to 6.5% Daily ROI",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Text(
                        text = if (isUrdu) "جاز کیش، ایزی پیسہ اور بینک کے ذریعے فوری ٹرانسفر" else "Instant payouts to JazzCash, EasyPaisa & Bank",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Balance & Financial Overview Card
        BalanceCard(
            user = user,
            language = language,
            currency = currency,
            onDepositClick = onDepositClick,
            onWithdrawClick = onWithdrawClick,
            onInvestClick = onInvestClick,
            onCalculatorClick = onCalculatorClick,
            onClaimProfitClick = onClaimProfitClick,
            isClaiming = isClaimingProfit
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Live Community Activity Ticker
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Slate100,
            border = ButtonDefaults.outlinedButtonBorder,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Emerald600)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isUrdu) "🔴 لائیو: کامران نے ₨ 15,000 ودڈرا کیا • عائشہ نے گولڈ پلان جوائن کیا" else "🔴 Live: Kamran withdrew ₨ 15,000 • Ayesha joined Gold Plan",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Slate700,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Lucky Spin & Daily Rewards Banner
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Amber50),
            border = BorderStroke(1.dp, Amber400),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSpinClick() }
        ) {
            Row(
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Amber500),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = null,
                            tint = Slate950,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = StringsManager.get("lucky_spin", language),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Amber700
                            )
                        )
                        Text(
                            text = if (isUrdu) "مفت اسپن کریں اور 1000 روپے تک جیتیں" else "Spin & win up to ₨ 1,000 cash daily",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Slate700,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
                Button(
                    onClick = onSpinClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Amber500, contentColor = Slate950),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(StringsManager.get("spin_now", language), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Popular Investment Plans Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = StringsManager.get("investment_plans", language),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            TextButton(onClick = { onNavigate("plans") }) {
                Text(
                    text = if (isUrdu) "تمام پلانز دیکھیں" else "View All",
                    color = Emerald700,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            plans.take(3).forEach { plan ->
                PlanCard(
                    plan = plan,
                    language = language,
                    currency = currency,
                    onSelectPlan = onSelectPlan
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Referral Promo Banner with Image
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigate("referrals") },
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Box(modifier = Modifier.height(110.dp).fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.img_rewards_gift),
                    contentDescription = "Affiliate Program",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Emerald900.copy(alpha = 0.85f), Color.Transparent)
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = StringsManager.get("referral_program", language),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = if (isUrdu) "دوستوں کو انوائٹ کریں اور 10% کمیشن پائیں" else "Invite friends & get 10% instant commission",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp
                            )
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Amber400
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recent Transactions Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = StringsManager.get("transaction_history", language),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            TextButton(onClick = { onNavigate("wallet") }) {
                Text(
                    text = if (isUrdu) "والیٹ ہسٹری" else "See All",
                    color = Emerald700,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            transactions.take(3).forEach { tx ->
                TransactionRowItem(
                    tx = tx,
                    language = language,
                    currency = currency,
                    onClick = { onShowReceipt(tx) }
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun TransactionRowItem(
    tx: TransactionEntity,
    language: AppLanguage,
    currency: String,
    onClick: () -> Unit
) {
    val isDeposit = tx.type == "DEPOSIT"
    val isWithdraw = tx.type == "WITHDRAWAL"
    val isProfit = tx.type == "ROI_PROFIT"
    val isBonus = tx.type == "REFERRAL_BONUS" || tx.type == "TASK_REWARD"

    val icon = when {
        isDeposit -> Icons.Default.AddCircle
        isWithdraw -> Icons.Default.ArrowCircleUp
        isProfit -> Icons.Default.TrendingUp
        else -> Icons.Default.CardGiftcard
    }

    val iconColor = when {
        isDeposit -> Emerald600
        isWithdraw -> Amber600
        isProfit -> Color(0xFF0284C7)
        else -> Color(0xFF7C3AED)
    }

    val prefix = if (isDeposit || isProfit || isBonus) "+" else "-"
    val note = if (language == AppLanguage.URDU) tx.noteUr else tx.noteEn

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = ButtonDefaults.outlinedButtonBorder,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = note,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = tx.paymentMethod,
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$prefix₨ %,d".format(tx.amount.toInt()),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (prefix == "+") Emerald700 else Rose600
                    )
                )
                Text(
                    text = if (tx.status == "COMPLETED") StringsManager.get("completed", language) else StringsManager.get("pending", language),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (tx.status == "COMPLETED") Emerald600 else Amber600,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}
