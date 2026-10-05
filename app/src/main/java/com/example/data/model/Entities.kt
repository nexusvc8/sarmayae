package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_account")
data class UserAccountEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Alam Khan",
    val phone: String = "+92 300 1234567",
    val email: String = "alamkhan@example.com",
    val balance: Double = 2450.0,
    val totalInvested: Double = 15000.0,
    val totalProfitEarned: Double = 6840.0,
    val referralEarnings: Double = 1850.0,
    val todayProfit: Double = 375.0,
    val referralCode: String = "PAK786",
    val isKycVerified: Boolean = true,
    val selectedLanguage: String = "ur", // default Urdu / English switchable
    val selectedCurrency: String = "PKR",
    val lastCheckInDateMillis: Long = 0L,
    val checkInStreak: Int = 3,
    val availableSpins: Int = 2,
    val securityPin: String = "1234",
    val isDarkMode: Boolean = false
)

@Entity(tableName = "investment_plans")
data class InvestmentPlanEntity(
    @PrimaryKey val id: String,
    val titleEn: String,
    val titleUr: String,
    val descriptionEn: String,
    val descriptionUr: String,
    val minDeposit: Double,
    val maxDeposit: Double,
    val dailyRoiPercent: Double,
    val durationDays: Int,
    val tagEn: String,
    val tagUr: String,
    val riskLevel: String, // LOW, MODERATE, HIGH
    val isCapitalReturned: Boolean = true,
    val sortOrder: Int = 0
)

@Entity(tableName = "active_investments")
data class ActiveInvestmentEntity(
    @PrimaryKey val id: String,
    val planId: String,
    val planTitleEn: String,
    val planTitleUr: String,
    val investedAmount: Double,
    val dailyRoiPercent: Double,
    val dailyProfitAmount: Double,
    val totalEarnedSoFar: Double,
    val totalExpectedProfit: Double,
    val durationDays: Int,
    val daysCompleted: Int,
    val startTimestampMillis: Long,
    val lastPayoutTimestampMillis: Long,
    val isCompleted: Boolean = false
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val type: String, // DEPOSIT, WITHDRAWAL, ROI_PROFIT, REFERRAL_BONUS, TASK_REWARD
    val amount: Double,
    val currency: String = "PKR",
    val status: String, // COMPLETED, PENDING, REJECTED
    val paymentMethod: String,
    val referenceId: String,
    val timestampMillis: Long,
    val accountDetail: String,
    val noteEn: String,
    val noteUr: String
)

@Entity(tableName = "referral_members")
data class ReferralMemberEntity(
    @PrimaryKey val id: String,
    val name: String,
    val level: Int, // 1, 2, 3
    val phoneMasked: String,
    val joinDateMillis: Long,
    val totalInvested: Double,
    val commissionEarned: Double,
    val isActive: Boolean = true
)

@Entity(tableName = "daily_tasks")
data class DailyTaskEntity(
    @PrimaryKey val id: String,
    val titleEn: String,
    val titleUr: String,
    val descriptionEn: String,
    val descriptionUr: String,
    val rewardAmount: Double,
    val taskType: String,
    val isCompletedToday: Boolean = false,
    val lastCompletedDateMillis: Long = 0L
)
