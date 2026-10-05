package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ReferralMemberEntity
import com.example.data.model.UserAccountEntity
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringsManager
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReferralScreen(
    user: UserAccountEntity,
    referrals: List<ReferralMemberEntity>,
    language: AppLanguage,
    currency: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isUrdu = language == AppLanguage.URDU
    val referralLink = "https://sarmayainvest.com/join?ref=${user.referralCode}"

    fun copyToClipboard(text: String, msg: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Referral", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun shareReferral() {
        val shareText = if (isUrdu) {
            "🌟 سرمایہ انویسٹ (Sarmaya Invest) پر روزانہ 2.5% سے 6.5% منافع کمائیں! میرا ریفرل کوڈ استعمال کریں: ${user.referralCode}\nجوائن لنک: $referralLink"
        } else {
            "🌟 Join Sarmaya Invest & earn 2.5% to 6.5% daily returns! Use my invite code: ${user.referralCode}\nSign up link: $referralLink"
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(intent, "Share Referral Link"))
    }

    val totalTeamCount = referrals.size
    val activeTeamCount = referrals.count { it.isActive }
    val totalComm = referrals.sumOf { it.commissionEarned }

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
                    text = StringsManager.get("referral_program", language),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = StringsManager.get("referral_desc", language),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        // Hero Graphic Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_rewards_gift),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Emerald900.copy(alpha = 0.9f), Color.Transparent)
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(16.dp)
                    ) {
                        Surface(shape = RoundedCornerShape(6.dp), color = Amber500) {
                            Text(
                                text = "3-TIER AFFILIATE",
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
                            text = if (isUrdu) "10% لیول 1 + 4% لیول 2 + 2% لیول 3" else "10% Level 1 + 4% Level 2 + 2% Level 3",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = if (isUrdu) "ہر ڈپازٹ پر خودکار فوری کمیشن" else "Instant passive commission on every team deposit",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // Referral Code & Link Box
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Emerald200),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = StringsManager.get("your_referral_code", language),
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate600)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Emerald50)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = user.referralCode,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Emerald800,
                                letterSpacing = 2.sp
                            )
                        )
                        Button(
                            onClick = { copyToClipboard(user.referralCode, StringsManager.get("copied", language)) },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(StringsManager.get("copy_code", language), fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Share on WhatsApp & Socials
                    Button(
                        onClick = { shareReferral() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("share_referral_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = StringsManager.get("share_whatsapp", language),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // Commission Tiers Summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CommissionTierPill(
                    modifier = Modifier.weight(1f),
                    level = "Level 1",
                    rate = "10%",
                    color = Emerald700,
                    bgColor = Emerald50
                )
                CommissionTierPill(
                    modifier = Modifier.weight(1f),
                    level = "Level 2",
                    rate = "4%",
                    color = Color(0xFF0284C7),
                    bgColor = Color(0xFFF0F9FF)
                )
                CommissionTierPill(
                    modifier = Modifier.weight(1f),
                    level = "Level 3",
                    rate = "2%",
                    color = Color(0xFF7C3AED),
                    bgColor = Color(0xFFF5F3FF)
                )
            }
        }

        // Team Metrics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = StringsManager.get("total_invited", language),
                    value = "$totalTeamCount Members",
                    icon = Icons.Default.Group
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = StringsManager.get("total_commission", language),
                    value = "₨ %,d".format(totalComm.toInt()),
                    icon = Icons.Default.MonetizationOn
                )
            }
        }

        // Team List Header
        item {
            Text(
                text = StringsManager.get("team_list", language),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }

        // Team items
        items(referrals) { member ->
            TeamMemberCard(member = member, language = language)
        }
    }
}

@Composable
private fun CommissionTierPill(
    modifier: Modifier = Modifier,
    level: String,
    rate: String,
    color: Color,
    bgColor: Color
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = level, style = MaterialTheme.typography.labelSmall.copy(color = Slate600, fontSize = 10.sp))
            Text(text = rate, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = color))
            Text(text = "Instant", style = MaterialTheme.typography.labelSmall.copy(color = Slate500, fontSize = 9.sp))
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Slate200),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = Emerald700, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, style = MaterialTheme.typography.labelSmall.copy(color = Slate600, fontSize = 10.sp))
            Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Slate900))
        }
    }
}

@Composable
private fun TeamMemberCard(
    member: ReferralMemberEntity,
    language: AppLanguage
) {
    val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(member.joinDateMillis))

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, Slate200),
        modifier = Modifier.fillMaxWidth()
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
                        .clip(CircleShape)
                        .background(Emerald100),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "L${member.level}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Emerald800
                        )
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = member.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Slate900)
                    )
                    Text(
                        text = "${member.phoneMasked} • Joined $dateStr",
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate500, fontSize = 10.sp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+₨ %,d".format(member.commissionEarned.toInt()),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Emerald700)
                )
                Text(
                    text = "Invested: ₨ %,d".format(member.totalInvested.toInt()),
                    style = MaterialTheme.typography.labelSmall.copy(color = Slate600, fontSize = 10.sp)
                )
            }
        }
    }
}

private val Emerald200 = Color(0xFFA7F3D0)
