package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.TransactionEntity
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringsManager
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReceiptDialog(
    transaction: TransactionEntity,
    language: AppLanguage,
    currency: String,
    onDismiss: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(transaction.timestampMillis))

    val isDeposit = transaction.type == "DEPOSIT"
    val isWithdraw = transaction.type == "WITHDRAWAL"
    val isProfit = transaction.type == "ROI_PROFIT"
    val isBonus = transaction.type == "REFERRAL_BONUS" || transaction.type == "TASK_REWARD"

    val headerColor = when {
        isDeposit || isProfit || isBonus -> Emerald700
        else -> Amber600
    }

    val note = if (language == AppLanguage.URDU) transaction.noteUr else transaction.noteEn

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
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Badge
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(headerColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isDeposit) Icons.Default.AddCircle else if (isWithdraw) Icons.Default.ArrowCircleUp else Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = headerColor,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = StringsManager.get("view_receipt", language),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Text(
                    text = "₨ %,d".format(transaction.amount.toInt()),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = headerColor
                    )
                )

                // Status Chip
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (transaction.status == "COMPLETED") Emerald100 else Amber100,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Text(
                        text = if (transaction.status == "COMPLETED") StringsManager.get("completed", language) else StringsManager.get("pending", language),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (transaction.status == "COMPLETED") Emerald800 else Amber700
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Divider(color = Slate200)

                Spacer(modifier = Modifier.height(12.dp))

                // Detail Rows
                ReceiptRow(
                    label = if (language == AppLanguage.URDU) "ٹرانزیکشن آئی ڈی" else "Reference ID",
                    value = transaction.referenceId
                )
                ReceiptRow(
                    label = if (language == AppLanguage.URDU) "ادائیگی طریقہ" else "Payment Method",
                    value = transaction.paymentMethod
                )
                ReceiptRow(
                    label = if (language == AppLanguage.URDU) "اکاؤنٹ تفصیل" else "Account Details",
                    value = transaction.accountDetail
                )
                ReceiptRow(
                    label = if (language == AppLanguage.URDU) "تاریخ و وقت" else "Date & Time",
                    value = dateStr
                )
                ReceiptRow(
                    label = if (language == AppLanguage.URDU) "تفصیل / نوٹ" else "Note / Description",
                    value = note
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text(if (language == AppLanguage.URDU) "بند کریں" else "Done")
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = Slate900
            ),
            maxLines = 1
        )
    }
}
