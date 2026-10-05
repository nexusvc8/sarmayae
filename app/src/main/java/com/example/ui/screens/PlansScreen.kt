package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InvestmentPlanEntity
import com.example.ui.components.PlanCard
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringsManager
import com.example.ui.theme.*

@Composable
fun PlansScreen(
    plans: List<InvestmentPlanEntity>,
    language: AppLanguage,
    currency: String,
    onSelectPlan: (InvestmentPlanEntity) -> Unit,
    onOpenCalculator: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("ALL") }

    val filteredPlans = remember(selectedCategory, plans) {
        when (selectedCategory) {
            "LOW" -> plans.filter { it.riskLevel == "LOW" }
            "VIP" -> plans.filter { it.riskLevel == "HIGH" || it.id.contains("gold") }
            else -> plans
        }
    }

    val isUrdu = language == AppLanguage.URDU

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
                    text = StringsManager.get("investment_plans", language),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = StringsManager.get("plans_subtitle", language),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        // Profit Calculator Banner
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Emerald50),
                border = ButtonDefaults.outlinedButtonBorder,
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
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Emerald700),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = StringsManager.get("profit_calculator", language),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald900
                                )
                            )
                            Text(
                                text = if (isUrdu) "اپنی رقم کا درست منافع خود معلوم کریں" else "Check exact daily & total net return",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Slate600,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                    Button(
                        onClick = onOpenCalculator,
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(if (isUrdu) "کھولیں" else "Open", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Filter Category Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == "ALL",
                    onClick = { selectedCategory = "ALL" },
                    label = { Text(if (isUrdu) "تمام پلانز" else "All Plans", fontWeight = FontWeight.Bold) }
                )
                FilterChip(
                    selected = selectedCategory == "LOW",
                    onClick = { selectedCategory = "LOW" },
                    label = { Text(if (isUrdu) "محفوظ (Low Risk)" else "Starter / Low Risk", fontWeight = FontWeight.Bold) }
                )
                FilterChip(
                    selected = selectedCategory == "VIP",
                    onClick = { selectedCategory = "VIP" },
                    label = { Text(if (isUrdu) "وی آئی پی ڈائمنڈ" else "VIP & Crypto", fontWeight = FontWeight.Bold) }
                )
            }
        }

        // List of Plans
        items(filteredPlans) { plan ->
            PlanCard(
                plan = plan,
                language = language,
                currency = currency,
                onSelectPlan = onSelectPlan
            )
        }

        // Security & Capital Back Guarantee
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Slate100,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Emerald700,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isUrdu) "100% اصل رقم کی واپسی کی گارنٹی" else "100% Principal Protection Guarantee",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = if (isUrdu) "پلان کی مدت مکمل ہونے پر آپ کی اصل رقم خودکار طریقے سے والیٹ میں واپس آ جاتی ہے۔" else "Your initial capital is automatically credited back to your wallet upon plan maturity.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Slate600,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
