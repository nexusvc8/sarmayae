package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringsManager
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun SpinWheelDialog(
    availableSpins: Int,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSpinCollect: (Double) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isSpinning by remember { mutableStateOf(false) }
    var wonAmount by remember { mutableStateOf<Double?>(null) }
    val rotationAnim = remember { Animatable(0f) }

    val prizes = listOf(50.0, 100.0, 200.0, 50.0, 500.0, 100.0, 1000.0, 25.0)
    val colors = listOf(
        Color(0xFF047857), Color(0xFFD97706),
        Color(0xFF0284C7), Color(0xFF7C3AED),
        Color(0xFF059669), Color(0xFFE11D48),
        Color(0xFFF59E0B), Color(0xFF2563EB)
    )

    fun startSpin() {
        if (isSpinning || availableSpins <= 0) return
        isSpinning = true
        wonAmount = null

        coroutineScope.launch {
            val randomPrizeIndex = Random.nextInt(prizes.size)
            val selectedPrize = prizes[randomPrizeIndex]
            val wedgeDegrees = 360f / prizes.size
            val targetDegrees = 360f * 5 + (prizes.size - 1 - randomPrizeIndex) * wedgeDegrees + (wedgeDegrees / 2)

            rotationAnim.animateTo(
                targetValue = targetDegrees,
                animationSpec = tween(durationMillis = 3500, easing = FastOutSlowInEasing)
            )

            delay(300)
            isSpinning = false
            wonAmount = selectedPrize
            onSpinCollect(selectedPrize)
        }
    }

    Dialog(onDismissRequest = { if (!isSpinning) onDismiss() }) {
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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = StringsManager.get("lucky_spin", language),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    IconButton(onClick = onDismiss, enabled = !isSpinning) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = StringsManager.get("spin_desc", language),
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate600),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Available Spins Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Amber100
                ) {
                    Text(
                        text = "${StringsManager.get("spins_left", language)}: $availableSpins",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Amber700
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Wheel Canvas
                Box(
                    modifier = Modifier.size(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Pointer at top
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-12).dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Rose600,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // Rotating Wheel
                    Canvas(
                        modifier = Modifier
                            .size(220.dp)
                            .rotate(rotationAnim.value)
                            .shadow(8.dp, CircleShape)
                            .clip(CircleShape)
                    ) {
                        val sweepAngle = 360f / prizes.size
                        prizes.forEachIndexed { index, prize ->
                            drawArc(
                                color = colors[index % colors.size],
                                startAngle = index * sweepAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true
                            )
                        }

                        // Outer ring
                        drawCircle(
                            color = Color(0xFFF59E0B),
                            radius = size.width / 2,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8f)
                        )
                    }

                    // Center Logo Button
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Amber500,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Result Banner if won
                if (wonAmount != null) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Emerald100,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Celebration, contentDescription = null, tint = Emerald700)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == AppLanguage.URDU) "مبارک ہو! آپ نے ₨ ${wonAmount?.toInt()} جیت لیے!" else "Won ₨ ${wonAmount?.toInt()} Cash Reward!",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Emerald900
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Spin Action Button
                Button(
                    onClick = { startSpin() },
                    enabled = !isSpinning && availableSpins > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("spin_wheel_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Amber500, contentColor = Slate950)
                ) {
                    if (isSpinning) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Slate950)
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = StringsManager.get("spin_now", language),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}
