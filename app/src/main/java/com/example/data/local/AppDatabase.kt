package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
    entities = [
        UserAccountEntity::class,
        InvestmentPlanEntity::class,
        ActiveInvestmentEntity::class,
        TransactionEntity::class,
        ReferralMemberEntity::class,
        DailyTaskEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun planDao(): InvestmentPlanDao
    abstract fun activeInvestmentDao(): ActiveInvestmentDao
    abstract fun transactionDao(): TransactionDao
    abstract fun referralDao(): ReferralDao
    abstract fun taskDao(): DailyTaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sarmaya_invest_db"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }
    }
}

suspend fun populateInitialData(db: AppDatabase) {
    // 1. Initial User
    val user = UserAccountEntity(
        id = 1,
        name = "Alam Khan",
        phone = "+92 300 7860123",
        email = "alamkhanshahani786@gmail.com",
        balance = 5250.0,
        totalInvested = 20000.0,
        totalProfitEarned = 8450.0,
        referralEarnings = 3200.0,
        todayProfit = 650.0,
        referralCode = "SARMAYA786",
        isKycVerified = true,
        selectedLanguage = "ur",
        selectedCurrency = "PKR",
        checkInStreak = 4,
        availableSpins = 3,
        securityPin = "7860",
        isDarkMode = false
    )
    db.userDao().insertOrUpdate(user)

    // 2. Investment Plans
    val plans = listOf(
        InvestmentPlanEntity(
            id = "starter_30",
            titleEn = "Starter Spark Plan",
            titleUr = "سٹارٹر اسپارک پلان",
            descriptionEn = "Ideal for beginners. Earn guaranteed 2.5% daily with full principal safety.",
            descriptionUr = "شروع کرنے والوں کے لیے بہترین۔ 2.5% روزانہ منافع اور اصل رقم محفوظ۔",
            minDeposit = 1000.0,
            maxDeposit = 10000.0,
            dailyRoiPercent = 2.5,
            durationDays = 30,
            tagEn = "Beginner Friendly",
            tagUr = "آسان اور محفوظ",
            riskLevel = "LOW",
            isCapitalReturned = true,
            sortOrder = 1
        ),
        InvestmentPlanEntity(
            id = "silver_45",
            titleEn = "Silver Growth Plan",
            titleUr = "سلور گروتھ پلان",
            descriptionEn = "Consistent 3.5% daily returns with fast daily withdrawals and bonus rewards.",
            descriptionUr = "3.5% مستقل روزانہ منافع، تیز رفتار ودڈرا اور اضافی بونس انعامات۔",
            minDeposit = 10000.0,
            maxDeposit = 50000.0,
            dailyRoiPercent = 3.5,
            durationDays = 45,
            tagEn = "Most Popular ⭐",
            tagUr = "سب سے مقبول ⭐",
            riskLevel = "LOW",
            isCapitalReturned = true,
            sortOrder = 2
        ),
        InvestmentPlanEntity(
            id = "gold_60",
            titleEn = "Gold Wealth Matrix",
            titleUr = "گولڈ ویلتھ میٹرکس",
            descriptionEn = "High-yield wealth creation at 5.0% daily return. Priority 24/7 account manager.",
            descriptionUr = "5.0% روزانہ زبردست منافع۔ فوری 24 گھنٹے ترجیحی کسٹمر مینیجر سپورٹ۔",
            minDeposit = 50000.0,
            maxDeposit = 200000.0,
            dailyRoiPercent = 5.0,
            durationDays = 60,
            tagEn = "High Return 🚀",
            tagUr = "شاندار منافع 🚀",
            riskLevel = "MODERATE",
            isCapitalReturned = true,
            sortOrder = 3
        ),
        InvestmentPlanEntity(
            id = "crypto_vip_90",
            titleEn = "VIP Diamond Crypto",
            titleUr = "وی آئی پی ڈائمنڈ کرپٹو",
            descriptionEn = "Maximum 6.5% daily ROI leveraging algorithmic crypto & forex arbitrage.",
            descriptionUr = "6.5% روزانہ زیادہ سے زیادہ منافع۔ کرپٹو اور فاریکس خودکار ارننگ سسٹم۔",
            minDeposit = 100000.0,
            maxDeposit = 1000000.0,
            dailyRoiPercent = 6.5,
            durationDays = 90,
            tagEn = "VIP Exclusive 💎",
            tagUr = "وی آئی پی ڈائمنڈ 💎",
            riskLevel = "HIGH",
            isCapitalReturned = true,
            sortOrder = 4
        ),
        InvestmentPlanEntity(
            id = "express_7",
            titleEn = "7-Day Express Yield",
            titleUr = "7 روزہ ایکسپریس پلان",
            descriptionEn = "Quick 7-day turnaround with total 125% payout at maturity.",
            descriptionUr = "صرف 7 دن کا تیز ترین پلان۔ 7 دن میں کل 125% منافع واپسی۔",
            minDeposit = 2000.0,
            maxDeposit = 100000.0,
            dailyRoiPercent = 3.6,
            durationDays = 7,
            tagEn = "Short Term ⚡",
            tagUr = "مختصر مدت ⚡",
            riskLevel = "LOW",
            isCapitalReturned = true,
            sortOrder = 5
        )
    )
    db.planDao().insertPlans(plans)

    // 3. Active Investments for initial demo
    val now = System.currentTimeMillis()
    val dayMillis = 86400000L
    val activeList = listOf(
        ActiveInvestmentEntity(
            id = "act_1",
            planId = "silver_45",
            planTitleEn = "Silver Growth Plan (₨ 15,000)",
            planTitleUr = "سلور گروتھ پلان (15,000 روپے)",
            investedAmount = 15000.0,
            dailyRoiPercent = 3.5,
            dailyProfitAmount = 525.0,
            totalEarnedSoFar = 4200.0,
            totalExpectedProfit = 23625.0,
            durationDays = 45,
            daysCompleted = 8,
            startTimestampMillis = now - (8 * dayMillis),
            lastPayoutTimestampMillis = now - (2 * 3600000L),
            isCompleted = false
        ),
        ActiveInvestmentEntity(
            id = "act_2",
            planId = "starter_30",
            planTitleEn = "Starter Spark Plan (₨ 5,000)",
            planTitleUr = "سٹارٹر اسپارک پلان (5,000 روپے)",
            investedAmount = 5000.0,
            dailyRoiPercent = 2.5,
            dailyProfitAmount = 125.0,
            totalEarnedSoFar = 1875.0,
            totalExpectedProfit = 3750.0,
            durationDays = 30,
            daysCompleted = 15,
            startTimestampMillis = now - (15 * dayMillis),
            lastPayoutTimestampMillis = now - (5 * 3600000L),
            isCompleted = false
        )
    )
    for (inv in activeList) {
        db.activeInvestmentDao().insertInvestment(inv)
    }

    // 4. Sample Transactions
    val sampleTransactions = listOf(
        TransactionEntity(
            id = UUID.randomUUID().toString(),
            type = "DEPOSIT",
            amount = 15000.0,
            currency = "PKR",
            status = "COMPLETED",
            paymentMethod = "EasyPaisa",
            referenceId = "EP789234101",
            timestampMillis = now - (9 * dayMillis),
            accountDetail = "0345******* (EasyPaisa)",
            noteEn = "Deposit approved for Silver Plan",
            noteUr = "سلور پلان کیلئے ڈپازٹ منظور کر لیا گیا"
        ),
        TransactionEntity(
            id = UUID.randomUUID().toString(),
            type = "ROI_PROFIT",
            amount = 650.0,
            currency = "PKR",
            status = "COMPLETED",
            paymentMethod = "System Auto-Credit",
            referenceId = "ROI-DAILY-${System.currentTimeMillis() % 100000}",
            timestampMillis = now - (3 * 3600000L),
            accountDetail = "Wallet Balance",
            noteEn = "Daily profit payout credited",
            noteUr = "روزانہ منافع والیٹ میں شامل"
        ),
        TransactionEntity(
            id = UUID.randomUUID().toString(),
            type = "WITHDRAWAL",
            amount = 3500.0,
            currency = "PKR",
            status = "COMPLETED",
            paymentMethod = "JazzCash",
            referenceId = "JC991204882",
            timestampMillis = now - (3 * dayMillis),
            accountDetail = "0300******* (JazzCash)",
            noteEn = "Instant withdrawal processed",
            noteUr = "فوری ودڈرا اکاؤنٹ میں ٹرانسفر مکمل"
        ),
        TransactionEntity(
            id = UUID.randomUUID().toString(),
            type = "REFERRAL_BONUS",
            amount = 1500.0,
            currency = "PKR",
            status = "COMPLETED",
            paymentMethod = "Affiliate Level 1",
            referenceId = "REF-COMM-449",
            timestampMillis = now - (4 * dayMillis),
            accountDetail = "Wallet Balance",
            noteEn = "10% Referral commission from Usman",
            noteUr = "عثمان کے انویسٹمنٹ سے 10% ریفرل بونس"
        ),
        TransactionEntity(
            id = UUID.randomUUID().toString(),
            type = "TASK_REWARD",
            amount = 100.0,
            currency = "PKR",
            status = "COMPLETED",
            paymentMethod = "Lucky Spin Reward",
            referenceId = "SPIN-88912",
            timestampMillis = now - (12 * 3600000L),
            accountDetail = "Wallet Balance",
            noteEn = "Won Lucky Spin Cash Reward",
            noteUr = "لکی اسپن کیش انعام موصول ہوا"
        )
    )
    for (tx in sampleTransactions) {
        db.transactionDao().insertTransaction(tx)
    }

    // 5. Referral Team Members
    val team = listOf(
        ReferralMemberEntity(
            id = "ref_1",
            name = "Usman Tariq",
            level = 1,
            phoneMasked = "+92 301 ***4812",
            joinDateMillis = now - (6 * dayMillis),
            totalInvested = 25000.0,
            commissionEarned = 2500.0,
            isActive = true
        ),
        ReferralMemberEntity(
            id = "ref_2",
            name = "Bilal Ahmed",
            level = 1,
            phoneMasked = "+92 345 ***9014",
            joinDateMillis = now - (10 * dayMillis),
            totalInvested = 10000.0,
            commissionEarned = 1000.0,
            isActive = true
        ),
        ReferralMemberEntity(
            id = "ref_3",
            name = "Hamza Raza",
            level = 2,
            phoneMasked = "+92 321 ***3391",
            joinDateMillis = now - (4 * dayMillis),
            totalInvested = 15000.0,
            commissionEarned = 600.0,
            isActive = true
        ),
        ReferralMemberEntity(
            id = "ref_4",
            name = "Kashif Ali",
            level = 3,
            phoneMasked = "+92 333 ***7711",
            joinDateMillis = now - (2 * dayMillis),
            totalInvested = 5000.0,
            commissionEarned = 100.0,
            isActive = true
        )
    )
    db.referralDao().insertReferrals(team)

    // 6. Daily Tasks
    val tasks = listOf(
        DailyTaskEntity(
            id = "task_checkin",
            titleEn = "Daily Check-In Bonus",
            titleUr = "ڈیلی چیک ان بونس",
            descriptionEn = "Collect your daily streak reward of 50 PKR",
            descriptionUr = "اپنا روزانہ 50 روپے کا چیک ان انعام وصول کریں",
            rewardAmount = 50.0,
            taskType = "CHECKIN",
            isCompletedToday = false
        ),
        DailyTaskEntity(
            id = "task_spin",
            titleEn = "Lucky Spin Wheel",
            titleUr = "لکی اسپن وہیل",
            descriptionEn = "Spin the wheel for a chance to win up to 1000 PKR",
            descriptionUr = "پہیہ گھمائیں اور 1000 روپے تک نقد انعام حاصل کریں",
            rewardAmount = 100.0,
            taskType = "SPIN",
            isCompletedToday = false
        ),
        DailyTaskEntity(
            id = "task_video_1",
            titleEn = "Watch Sponsor Video #1",
            titleUr = "اسپانسر ویڈیو #1 دیکھیں",
            descriptionEn = "Watch 15-second crypto sponsor video for 30 PKR",
            descriptionUr = "15 سیکنڈ کی ویڈیو دیکھیں اور 30 روپے حاصل کریں",
            rewardAmount = 30.0,
            taskType = "WATCH_AD",
            isCompletedToday = false
        ),
        DailyTaskEntity(
            id = "task_video_2",
            titleEn = "Watch Sponsor Video #2",
            titleUr = "اسپانسر ویڈیو #2 دیکھیں",
            descriptionEn = "Watch 15-second trading sponsor video for 30 PKR",
            descriptionUr = "15 سیکنڈ کا پروموشن دیکھیں اور 30 روپے حاصل کریں",
            rewardAmount = 30.0,
            taskType = "WATCH_AD",
            isCompletedToday = false
        ),
        DailyTaskEntity(
            id = "task_tg",
            titleEn = "Join Telegram Global Channel",
            titleUr = "آفیشل ٹیلیگرام چینل جوائن کریں",
            descriptionEn = "Get instant 50 PKR bonus for joining official community",
            descriptionUr = "آفیشل کمیونٹی جوائن کرنے پر فوری 50 روپے بونس",
            rewardAmount = 50.0,
            taskType = "TELEGRAM",
            isCompletedToday = false
        ),
        DailyTaskEntity(
            id = "task_wa",
            titleEn = "Join VIP WhatsApp Group",
            titleUr = "وی آئی پی واٹس ایپ گروپ جوائن کریں",
            descriptionEn = "Stay updated with daily withdrawal proofs & get 50 PKR",
            descriptionUr = "روزانہ ودڈرا ثبوت اور اپڈیٹس کیلئے واٹس ایپ جوائن کریں اور 50 روپے پائیں",
            rewardAmount = 50.0,
            taskType = "WHATSAPP",
            isCompletedToday = false
        ),
        DailyTaskEntity(
            id = "task_quiz",
            titleEn = "Daily Financial Quiz",
            titleUr = "روزانہ فنانشل کوئز",
            descriptionEn = "Answer 1 simple question to win 40 PKR",
            descriptionUr = "ایک آسان سوال کا صحیح جواب دیں اور 40 روپے جیتیں",
            rewardAmount = 40.0,
            taskType = "QUIZ",
            isCompletedToday = false
        )
    )
    db.taskDao().insertTasks(tasks)
}
