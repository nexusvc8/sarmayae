package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyTaskEntity
import com.example.data.model.UserAccountEntity
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringsManager
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TasksRewardsScreen(
    user: UserAccountEntity,
    tasks: List<DailyTaskEntity>,
    language: AppLanguage,
    currency: String,
    onSpinClick: () -> Unit,
    onCompleteTask: (DailyTaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val isUrdu = language == AppLanguage.URDU
    val coroutineScope = rememberCoroutineScope()

    // Video ad watching simulation state
    var isWatchingAd by remember { mutableStateOf(false) }
    var adTimerSeconds by remember { mutableIntStateOf(15) }
    var activeAdTask by remember { mutableStateOf<DailyTaskEntity?>(null) }

    // Quiz State
    var isQuizOpen by remember { mutableStateOf(false) }
    var selectedQuizOption by remember { mutableIntStateOf(-1) }
    var quizSubmitted by remember { mutableStateOf(false) }

    fun startVideoTask(task: DailyTaskEntity) {
        if (task.isCompletedToday || isWatchingAd) return
        activeAdTask = task
        isWatchingAd = true
        adTimerSeconds = 15

        coroutineScope.launch {
            while (adTimerSeconds > 0) {
                delay(1000)
                adTimerSeconds--
            }
            isWatchingAd = false
            onCompleteTask(task)
            activeAdTask = null
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
                    text = StringsManager.get("daily_tasks_title", language),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = StringsManager.get("tasks_desc", language),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        // 7-Day Check-in Streak Tracker
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Emerald100),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Emerald700)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = StringsManager.get("daily_checkin", language),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Amber100
                        ) {
                            Text(
                                text = "Streak: ${user.checkInStreak} 🔥",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Amber700,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 7 days row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        (1..7).forEach { day ->
                            val isClaimed = day <= user.checkInStreak
                            val isCurrent = day == user.checkInStreak + 1
                            val dayReward = day * 10

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isClaimed) Emerald50 else if (isCurrent) Amber50 else Slate100
                                    )
                                    .border(
                                        width = if (isCurrent) 1.5.dp else 0.5.dp,
                                        color = if (isCurrent) Amber500 else if (isClaimed) Emerald500 else Slate300,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "D$day",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isClaimed) Emerald800 else if (isCurrent) Amber700 else Slate600,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Icon(
                                    imageVector = if (isClaimed) Icons.Default.CheckCircle else Icons.Default.CardGiftcard,
                                    contentDescription = null,
                                    tint = if (isClaimed) Emerald600 else if (isCurrent) Amber600 else Slate400,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "+$dayReward",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 9.sp,
                                        color = if (isClaimed) Emerald800 else Slate700
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val checkinTask = tasks.find { it.taskType == "CHECKIN" }
                    Button(
                        onClick = { checkinTask?.let { onCompleteTask(it) } },
                        enabled = checkinTask != null && !checkinTask.isCompletedToday,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                    ) {
                        Text(
                            text = if (checkinTask?.isCompletedToday == true) StringsManager.get("claimed", language) else StringsManager.get("checkin_btn", language),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Lucky Spin Wheel Trigger Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Amber50),
                border = BorderStroke(1.dp, Amber400),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSpinClick() }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Amber500),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Casino, contentDescription = null, tint = Slate950, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = StringsManager.get("lucky_spin", language),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Amber700)
                            )
                            Text(
                                text = "${StringsManager.get("spins_left", language)}: ${user.availableSpins}",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate700)
                            )
                        }
                    }
                    Button(
                        onClick = onSpinClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Amber500, contentColor = Slate950),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(StringsManager.get("spin_now", language), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Video Ad Watching In-Progress Card (if active)
        if (isWatchingAd) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Emerald400, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isUrdu) "اسپانسر ویڈیو چل رہی ہے..." else "Playing Sponsor Video Ad...",
                            style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (isUrdu) "انعام حاصل کرنے کیلئے $adTimerSeconds سیکنڈ انتظار کریں" else "Please wait $adTimerSeconds seconds to claim ₨ 30 reward",
                            style = MaterialTheme.typography.bodySmall.copy(color = Amber400)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { (15 - adTimerSeconds) / 15f },
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = Emerald500
                        )
                    }
                }
            }
        }

        // Daily Tasks List
        item {
            Text(
                text = if (isUrdu) "دیگر روزانہ ٹاسکس" else "Available Daily Tasks",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }

        items(tasks.filter { it.taskType != "CHECKIN" && it.taskType != "SPIN" }) { task ->
            TaskItemCard(
                task = task,
                language = language,
                onStartTask = {
                    when (task.taskType) {
                        "WATCH_AD" -> startVideoTask(task)
                        "QUIZ" -> isQuizOpen = true
                        else -> onCompleteTask(task)
                    }
                }
            )
        }
    }

    // Daily Quiz Dialog
    if (isQuizOpen) {
        val quizTask = tasks.find { it.taskType == "QUIZ" }
        AlertDialog(
            onDismissRequest = { isQuizOpen = false },
            title = {
                Text(
                    text = StringsManager.get("daily_quiz", language),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isUrdu) "سوال: Sarmaya Invest پر روزانہ کم از کم کتنا منافع ملتا ہے؟" else "Question: What is the starting daily ROI on Sarmaya Invest?",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )

                    val options = if (isUrdu) listOf("0.5%", "2.5%", "0.1%", "کوئی منافع نہیں") else listOf("0.5%", "2.5%", "0.1%", "Zero")
                    options.forEachIndexed { idx, opt ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedQuizOption == idx) Emerald100 else Slate100,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedQuizOption = idx }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = selectedQuizOption == idx, onClick = { selectedQuizOption = idx })
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(opt, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedQuizOption == 1) { // correct answer is 2.5%
                            quizTask?.let { onCompleteTask(it) }
                        }
                        isQuizOpen = false
                    },
                    enabled = selectedQuizOption != -1,
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text(if (isUrdu) "جواب جمع کروائیں" else "Submit Answer")
                }
            },
            dismissButton = {
                TextButton(onClick = { isQuizOpen = false }) {
                    Text(if (isUrdu) "کینسل" else "Cancel")
                }
            }
        )
    }
}

@Composable
fun TaskItemCard(
    task: DailyTaskEntity,
    language: AppLanguage,
    onStartTask: () -> Unit
) {
    val isUrdu = language == AppLanguage.URDU
    val title = if (isUrdu) task.titleUr else task.titleEn
    val desc = if (isUrdu) task.descriptionUr else task.descriptionEn

    val icon = when (task.taskType) {
        "WATCH_AD" -> Icons.Default.PlayCircle
        "TELEGRAM" -> Icons.Default.Send
        "WHATSAPP" -> Icons.Default.Chat
        "QUIZ" -> Icons.Default.Quiz
        else -> Icons.Default.Star
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, Slate200),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Emerald50),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = Emerald700, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Slate900))
                    Text(text = desc, style = MaterialTheme.typography.bodySmall.copy(color = Slate600, fontSize = 11.sp), maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (task.isCompletedToday) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Emerald50
                ) {
                    Text(
                        text = StringsManager.get("claimed", language),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(color = Emerald700, fontWeight = FontWeight.Bold)
                    )
                }
            } else {
                Button(
                    onClick = onStartTask,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("+₨ ${task.rewardAmount.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private val Emerald400 = Color(0xFF34D399)
