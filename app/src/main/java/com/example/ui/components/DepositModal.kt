package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringsManager
import com.example.ui.theme.*

data class PaymentMethodInfo(
    val id: String,
    val name: String,
    val accountTitle: String,
    val accountNumber: String,
    val instructions: String,
    val iconColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositModal(
    language: AppLanguage,
    currency: String,
    onDismiss: () -> Unit,
    onSubmitDeposit: (amount: Double, method: String, tid: String, accountDetail: String) -> Unit
) {
    val context = LocalContext.current
    var selectedMethodId by remember { mutableStateOf("easypaisa") }
    var amountText by remember { mutableStateOf("5000") }
    var tidText by remember { mutableStateOf("") }
    var senderAccountText by remember { mutableStateOf("") }

    val methods = listOf(
        PaymentMethodInfo(
            id = "easypaisa",
            name = if (language == AppLanguage.URDU) "ایزی پیسہ (EasyPaisa)" else "EasyPaisa",
            accountTitle = "Sarmaya Official Finance",
            accountNumber = "03458901234",
            instructions = "Send Money -> EasyPaisa Transfer -> Enter Number -> Copy TID",
            iconColor = Color(0xFF00B04F)
        ),
        PaymentMethodInfo(
            id = "jazzcash",
            name = if (language == AppLanguage.URDU) "جاز کیش (JazzCash)" else "JazzCash",
            accountTitle = "Sarmaya Invest Merchant",
            accountNumber = "03007865432",
            instructions = "Send Money -> Mobile Account -> Enter Number -> Copy TID",
            iconColor = Color(0xFFED1C24)
        ),
        PaymentMethodInfo(
            id = "bank",
            name = if (language == AppLanguage.URDU) "میزان بینک (Meezan Bank)" else "Meezan Bank",
            accountTitle = "Sarmaya Technologies Pvt Ltd",
            accountNumber = "PK36MEZN0001090283718291",
            instructions = "Inter-Bank Transfer (IBFT) to Meezan Bank IBAN -> Copy Reference",
            iconColor = Color(0xFF6B21A8)
        ),
        PaymentMethodInfo(
            id = "usdt",
            name = "USDT (TRC-20 Crypto)",
            accountTitle = "Binance / TrustWallet TRC20",
            accountNumber = "TXn92K81PqLmZvaBxJ77QwrTy5Vbm88",
            instructions = "Send USDT (TRC20 network only) -> Enter TX Hash ID",
            iconColor = Color(0xFF26A17B)
        )
    )

    val currentMethod = methods.find { it.id == selectedMethodId } ?: methods.first()

    fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Payment Info", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, StringsManager.get("copied", language), Toast.LENGTH_SHORT).show()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
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
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = null,
                            tint = Emerald700,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = StringsManager.get("deposit_funds", language),
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
                    text = StringsManager.get("deposit_desc", language),
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Method Selector
                Text(
                    text = StringsManager.get("payment_method", language),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    methods.forEach { method ->
                        val isSelected = method.id == selectedMethodId
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Emerald50 else Slate100,
                            border = if (isSelected) ButtonDefaults.outlinedButtonBorder else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedMethodId = method.id }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedMethodId = method.id },
                                    colors = RadioButtonDefaults.colors(selectedColor = Emerald700)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = method.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Emerald900 else Slate800
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Account Details Card with 1-Click Copy
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Emerald50,
                    border = ButtonDefaults.outlinedButtonBorder,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = StringsManager.get("account_title", language),
                                    style = MaterialTheme.typography.labelSmall.copy(color = Slate600)
                                )
                                Text(
                                    text = currentMethod.accountTitle,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald900
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = StringsManager.get("account_number", language),
                                    style = MaterialTheme.typography.labelSmall.copy(color = Slate600)
                                )
                                Text(
                                    text = currentMethod.accountNumber,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Emerald800,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                            }
                            Button(
                                onClick = { copyToClipboard(currentMethod.accountNumber) },
                                colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(StringsManager.get("copy", language), fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Deposit Amount Input
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
                        .testTag("deposit_amount_input"),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Text("₨", fontWeight = FontWeight.Bold, color = Emerald700) },
                    singleLine = true
                )

                // Quick Amount Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("1000", "5000", "10000", "25000").forEach { quick ->
                        FilterChip(
                            selected = amountText == quick,
                            onClick = { amountText = quick },
                            label = { Text("₨ $quick", fontSize = 10.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Transaction ID Input
                Text(
                    text = StringsManager.get("transaction_id", language),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = tidText,
                    onValueChange = { tidText = it },
                    placeholder = { Text(StringsManager.get("enter_tid", language), fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("deposit_tid_input"),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Slate500) },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Instructions Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Amber50,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = StringsManager.get("deposit_instructions", language),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Amber700,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        ),
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Submit Button
                val depositAmount = amountText.toDoubleOrNull() ?: 0.0
                val canSubmit = depositAmount >= 500.0

                Button(
                    onClick = {
                        val finalTid = tidText.ifEmpty { "TXN-${System.currentTimeMillis() % 1000000}" }
                        onSubmitDeposit(
                            depositAmount,
                            currentMethod.name,
                            finalTid,
                            "${currentMethod.name} ($finalTid)"
                        )
                    },
                    enabled = canSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_deposit_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = StringsManager.get("submit_deposit", language),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
