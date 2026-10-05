package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.TransactionEntity
import com.example.data.model.UserAccountEntity
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringsManager
import com.example.ui.theme.*

@Composable
fun WalletScreen(
    user: UserAccountEntity,
    transactions: List<TransactionEntity>,
    language: AppLanguage,
    currency: String,
    onDepositClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    onShowReceipt: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredTransactions = remember(selectedFilter, transactions) {
        when (selectedFilter) {
            "DEPOSIT" -> transactions.filter { it.type == "DEPOSIT" }
            "WITHDRAWAL" -> transactions.filter { it.type == "WITHDRAWAL" }
            "ROI" -> transactions.filter { it.type == "ROI_PROFIT" }
            "BONUS" -> transactions.filter { it.type == "REFERRAL_BONUS" || it.type == "TASK_REWARD" }
            else -> transactions
        }
    }

    val isUrdu = language == AppLanguage.URDU

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
                    text = StringsManager.get("wallet", language),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = if (isUrdu) "رقم جمع کروائیں، منافع نکلوائیں اور مکمل ٹرانزیکشن ہسٹری دیکھیں" else "Manage deposits, instant withdrawals & transaction ledger",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        // Wallet Balance Master Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Emerald800),
                elevation = CardDefaults.cardElevation(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = StringsManager.get("available_balance", language),
                        style = MaterialTheme.typography.labelMedium.copy(color = Color.White.copy(alpha = 0.8f))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatMoney(user.balance),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onDepositClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("wallet_deposit_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Slate950)
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(StringsManager.get("deposit", language), fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onWithdrawClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("wallet_withdraw_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Amber500, contentColor = Slate950)
                        ) {
                            Icon(Icons.Default.ArrowCircleUp, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(StringsManager.get("withdraw", language), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Sub-balances cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SubBalanceCard(
                    modifier = Modifier.weight(1f),
                    title = StringsManager.get("referral_bonus", language),
                    value = formatMoney(user.referralEarnings),
                    icon = Icons.Default.Group,
                    color = Color(0xFF0284C7)
                )
                SubBalanceCard(
                    modifier = Modifier.weight(1f),
                    title = StringsManager.get("total_profit", language),
                    value = formatMoney(user.totalProfitEarned),
                    icon = Icons.Default.MonetizationOn,
                    color = Emerald600
                )
            }
        }

        // Transactions Ledger Header & Filters
        item {
            Column {
                Text(
                    text = StringsManager.get("transaction_history", language),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text(StringsManager.get("filter_all", language), fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedFilter == "DEPOSIT",
                        onClick = { selectedFilter = "DEPOSIT" },
                        label = { Text(StringsManager.get("filter_deposit", language), fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedFilter == "WITHDRAWAL",
                        onClick = { selectedFilter = "WITHDRAWAL" },
                        label = { Text(StringsManager.get("filter_withdraw", language), fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedFilter == "ROI",
                        onClick = { selectedFilter = "ROI" },
                        label = { Text(StringsManager.get("filter_profit", language), fontSize = 11.sp) }
                    )
                }
            }
        }

        // Transaction rows
        items(filteredTransactions) { tx ->
            TransactionRowItem(
                tx = tx,
                language = language,
                currency = currency,
                onClick = { onShowReceipt(tx) }
            )
        }
    }
}

@Composable
private fun SubBalanceCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Slate200),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = title, style = MaterialTheme.typography.labelSmall.copy(color = Slate600, fontSize = 10.sp))
            Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Slate900))
        }
    }
}
