package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringsManager
import com.example.ui.theme.*

data class ChatMessage(
    val sender: String, // "USER" or "AGENT"
    val text: String,
    val time: String
)

data class FaqData(
    val id: String,
    val questionEn: String,
    val questionUr: String,
    val answerEn: String,
    val answerUr: String
)

@Composable
fun HelpSupportScreen(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isUrdu = language == AppLanguage.URDU

    var userMessageText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                sender = "AGENT",
                text = if (isUrdu) "السلام علیکم! Sarmaya Invest سپورٹ میں خوش آمدید۔ ہم آپ کی کیا مدد کر سکتے ہیں؟" else "Hello! Welcome to Sarmaya Invest Support. How can we help you today?",
                time = "Just now"
            )
        )
    }

    val faqs = listOf(
        FaqData(
            id = "faq_1",
            questionEn = "How do I deposit money using EasyPaisa or JazzCash?",
            questionUr = "ایزی پیسہ یا جاز کیش سے رقم کیسے ڈپازٹ کریں؟",
            answerEn = "Go to Wallet -> Click Deposit -> Choose EasyPaisa or JazzCash -> Copy the account number -> Transfer the amount from your app -> Copy the 10-12 digit TID from SMS -> Paste TID and click Submit.",
            answerUr = "والیٹ میں جائیں -> ڈپازٹ پر کلک کریں -> ایزی پیسہ یا جازکیش منتخب کریں -> اکاؤنٹ نمبر کاپی کریں -> رقم بھیجنے کے بعد میسج سے ٹرانزیکشن آئی ڈی (TID) کاپی کر کے فارم میں درج کریں اور سبمٹ کریں۔"
        ),
        FaqData(
            id = "faq_2",
            questionEn = "How long does withdrawal processing take?",
            questionUr = "ودڈرا (رقم نکلوانے) میں کتنا وقت لگتا ہے؟",
            answerEn = "Withdrawals are processed automatically 24/7. Most payouts arrive in your EasyPaisa, JazzCash, or Bank within 5 to 15 minutes.",
            answerUr = "ودڈرا خودکار سسٹم کے ذریعے 24 گھنٹے پروسیس ہوتے ہیں۔ عام طور پر رقم 5 سے 15 منٹ کے اندر آپ کے ایزی پیسہ، جاز کیش یا بینک میں پہنچ جاتی ہے۔"
        ),
        FaqData(
            id = "faq_3",
            questionEn = "When is daily ROI profit credited?",
            questionUr = "روزانہ کا منافع کس وقت ملتا ہے؟",
            answerEn = "Daily returns generate every 24 hours. You can also tap 'Claim Daily ROI' anytime on your dashboard or portfolio to collect earned profits instantly.",
            answerUr = "پلان فعال ہونے کے بعد ہر 24 گھنٹے بعد منافع جمع ہوتا ہے۔ آپ ڈیش بورڈ پر موجود 'روزانہ منافع وصول کریں' بٹن دبا کر بھی کسی بھی وقت فوری منافع جمع کر سکتے ہیں۔"
        ),
        FaqData(
            id = "faq_4",
            questionEn = "Is my principal capital returned after plan expiry?",
            questionUr = "کیا پلان کی مدت ختم ہونے پر اصل رقم واپس ملتی ہے؟",
            answerEn = "Yes! All standard investment packages include 100% Capital Return guarantee. Your principal is refunded directly into your wallet at maturity.",
            answerUr = "جی ہاں! تمام پیکجز میں 100% اصل رقم کی واپسی کی گارنٹی ہے۔ مدت مکمل ہوتے ہی اصل رقم خودکار طریقے سے والیٹ میں واپس آ جاتی ہے۔"
        )
    )

    var expandedFaqId by remember { mutableStateOf<String?>(null) }

    fun sendUserMessage(text: String) {
        if (text.isBlank()) return
        messages.add(ChatMessage(sender = "USER", text = text, time = "Just now"))
        userMessageText = ""

        // Automatic smart reply
        val reply = when {
            text.contains("deposit", ignoreCase = true) || text.contains("ڈپازٹ") -> {
                if (isUrdu) "ڈپازٹ کیلئے والیٹ سیکشن میں جائیں اور مطلوبہ طریقہ منتخب کریں۔ کم از کم ڈپازٹ 500 روپے ہے۔"
                else "For deposits, navigate to Wallet -> Deposit. Minimum deposit is ₨ 500."
            }
            text.contains("withdraw", ignoreCase = true) || text.contains("ودڈرا") || text.contains("نکلوانا") -> {
                if (isUrdu) "ودڈرا 24/7 دستیاب ہے۔ کم از کم ودڈرا رقم 200 روپے ہے اور 15 منٹ میں موصول ہو جاتی ہے۔"
                else "Withdrawals are available 24/7 with a ₨ 200 minimum limit."
            }
            else -> {
                if (isUrdu) "آپ کے پیغام کا شکریہ! ہمارا لائیو کسٹمر سپورٹ ایجنٹ آپ سے فوری رابطہ کرے گا۔ یا آپ آفیشل واٹس ایپ جوائن کر سکتے ہیں۔"
                else "Thank you for reaching out! A senior support representative has been notified. You can also contact us via WhatsApp VIP."
            }
        }
        messages.add(ChatMessage(sender = "AGENT", text = reply, time = "Just now"))
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
                    text = StringsManager.get("customer_support", language),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = StringsManager.get("support_desc", language),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        // Official Social Links (WhatsApp & Telegram)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chat.whatsapp.com/sample_sarmaya_invest"))
                        try { context.startActivity(intent) } catch (e: Exception) {}
                    },
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(StringsManager.get("whatsapp_community", language), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/sample_sarmaya_invest"))
                        try { context.startActivity(intent) } catch (e: Exception) {}
                    },
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0088CC))
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(StringsManager.get("telegram_community", language), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        // Live Chat Box
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Emerald100),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = StringsManager.get("live_chat", language),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Text("Online 24/7", fontSize = 11.sp, color = Emerald700, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Messages Container
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate100)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        messages.forEach { msg ->
                            val isAgent = msg.sender == "AGENT"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isAgent) Arrangement.Start else Arrangement.End
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isAgent) Color.White else Emerald700,
                                    modifier = Modifier.widthIn(max = 260.dp)
                                ) {
                                    Text(
                                        text = msg.text,
                                        modifier = Modifier.padding(10.dp),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isAgent) Slate900 else Color.White,
                                            lineHeight = 16.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Suggestion Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            if (isUrdu) "ڈپازٹ کا طریقہ؟" else "Deposit help",
                            if (isUrdu) "ودڈرا ٹائم؟" else "Withdrawal time",
                            if (isUrdu) "کمیشن ریٹ؟" else "Affiliate rate"
                        ).forEach { quickQ ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Emerald50,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { sendUserMessage(quickQ) }
                            ) {
                                Text(
                                    text = quickQ,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(color = Emerald800, fontSize = 10.sp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Input Box
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = userMessageText,
                            onValueChange = { userMessageText = it },
                            placeholder = { Text(StringsManager.get("ask_question", language), fontSize = 12.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("support_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { sendUserMessage(userMessageText) },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Emerald700)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }
                }
            }
        }

        // FAQ Section
        item {
            Text(
                text = StringsManager.get("faq_title", language),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }

        items(faqs) { faq ->
            val isExpanded = expandedFaqId == faq.id
            val question = if (isUrdu) faq.questionUr else faq.questionEn
            val answer = if (isUrdu) faq.answerUr else faq.answerEn

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Slate200),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expandedFaqId = if (isExpanded) null else faq.id
                    }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = question,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = Emerald700
                        )
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = Slate200)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = answer,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Slate700,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
