package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class InvestmentRepository(private val database: AppDatabase) {
    val userAccount: Flow<UserAccountEntity?> = database.userDao().getUserAccount()
    val allPlans: Flow<List<InvestmentPlanEntity>> = database.planDao().getAllPlans()
    val activeInvestments: Flow<List<ActiveInvestmentEntity>> = database.activeInvestmentDao().getAllActiveInvestments()
    val allTransactions: Flow<List<TransactionEntity>> = database.transactionDao().getAllTransactions()
    val referralMembers: Flow<List<ReferralMemberEntity>> = database.referralDao().getAllReferrals()
    val dailyTasks: Flow<List<DailyTaskEntity>> = database.taskDao().getAllTasks()

    suspend fun checkAndSeedInitialUserIfEmpty() {
        val user = database.userDao().getUserAccountDirect()
        if (user == null) {
            com.example.data.local.populateInitialData(database)
        }
    }

    suspend fun setLanguage(lang: String) {
        database.userDao().updateLanguage(lang)
    }

    suspend fun setCurrency(currency: String) {
        database.userDao().updateCurrency(currency)
    }

    suspend fun setDarkMode(darkMode: Boolean) {
        database.userDao().updateDarkMode(darkMode)
    }

    suspend fun updateUser(user: UserAccountEntity) {
        database.userDao().insertOrUpdate(user)
    }

    suspend fun depositFunds(
        amount: Double,
        method: String,
        referenceTid: String,
        accountDetail: String,
        autoApprove: Boolean = true
    ): Result<TransactionEntity> {
        if (amount < 500.0) {
            return Result.failure(Exception("Minimum deposit is 500 PKR"))
        }

        val tx = TransactionEntity(
            id = UUID.randomUUID().toString(),
            type = "DEPOSIT",
            amount = amount,
            currency = "PKR",
            status = if (autoApprove) "COMPLETED" else "PENDING",
            paymentMethod = method,
            referenceId = referenceTid.ifEmpty { "TXN-${System.currentTimeMillis() % 1000000}" },
            timestampMillis = System.currentTimeMillis(),
            accountDetail = accountDetail,
            noteEn = "Deposit via $method",
            noteUr = "$method کے ذریعے ڈپازٹ"
        )
        database.transactionDao().insertTransaction(tx)

        if (autoApprove) {
            database.userDao().addBalance(amount)
        }
        return Result.success(tx)
    }

    suspend fun requestWithdrawal(
        amount: Double,
        method: String,
        accountNumber: String,
        accountHolder: String
    ): Result<TransactionEntity> {
        val user = database.userDao().getUserAccountDirect() ?: return Result.failure(Exception("User not found"))
        if (amount < 200.0) {
            return Result.failure(Exception("Minimum withdrawal is 200 PKR"))
        }
        if (user.balance < amount) {
            return Result.failure(Exception("Insufficient balance"))
        }

        database.userDao().deductBalance(amount)

        val tx = TransactionEntity(
            id = UUID.randomUUID().toString(),
            type = "WITHDRAWAL",
            amount = amount,
            currency = "PKR",
            status = "COMPLETED",
            paymentMethod = method,
            referenceId = "WD-${System.currentTimeMillis() % 1000000}",
            timestampMillis = System.currentTimeMillis(),
            accountDetail = "$accountHolder ($accountNumber)",
            noteEn = "Withdrawal to $method ($accountNumber)",
            noteUr = "$method ($accountNumber) پر ودڈرا ٹرانسفر"
        )
        database.transactionDao().insertTransaction(tx)
        return Result.success(tx)
    }

    suspend fun investInPlan(
        plan: InvestmentPlanEntity,
        amount: Double
    ): Result<ActiveInvestmentEntity> {
        val user = database.userDao().getUserAccountDirect() ?: return Result.failure(Exception("User not found"))
        if (amount < plan.minDeposit) {
            return Result.failure(Exception("Amount is less than minimum requirement (${plan.minDeposit})"))
        }
        if (amount > plan.maxDeposit) {
            return Result.failure(Exception("Amount exceeds maximum limit (${plan.maxDeposit})"))
        }
        if (user.balance < amount) {
            return Result.failure(Exception("Insufficient wallet balance. Please deposit funds first."))
        }

        // Deduct balance, add to totalInvested
        database.userDao().deductBalance(amount)
        database.userDao().addTotalInvested(amount)

        val dailyProfit = (amount * plan.dailyRoiPercent) / 100.0
        val totalExpected = dailyProfit * plan.durationDays

        val newInvestment = ActiveInvestmentEntity(
            id = UUID.randomUUID().toString(),
            planId = plan.id,
            planTitleEn = "${plan.titleEn} (₨ ${amount.toInt()})",
            planTitleUr = "${plan.titleUr} (${amount.toInt()} روپے)",
            investedAmount = amount,
            dailyRoiPercent = plan.dailyRoiPercent,
            dailyProfitAmount = dailyProfit,
            totalEarnedSoFar = 0.0,
            totalExpectedProfit = totalExpected,
            durationDays = plan.durationDays,
            daysCompleted = 0,
            startTimestampMillis = System.currentTimeMillis(),
            lastPayoutTimestampMillis = System.currentTimeMillis(),
            isCompleted = false
        )
        database.activeInvestmentDao().insertInvestment(newInvestment)

        val tx = TransactionEntity(
            id = UUID.randomUUID().toString(),
            type = "INVESTMENT",
            amount = amount,
            currency = "PKR",
            status = "COMPLETED",
            paymentMethod = "Wallet Balance",
            referenceId = "INV-${System.currentTimeMillis() % 1000000}",
            timestampMillis = System.currentTimeMillis(),
            accountDetail = plan.titleEn,
            noteEn = "Invested in ${plan.titleEn}",
            noteUr = "${plan.titleUr} میں سرمایہ کاری"
        )
        database.transactionDao().insertTransaction(tx)

        return Result.success(newInvestment)
    }

    suspend fun claimAllDailyProfit(): Result<Double> {
        val ongoing = database.activeInvestmentDao().getOngoingInvestments()
        if (ongoing.isEmpty()) {
            return Result.failure(Exception("No active investments to claim profit from."))
        }

        var totalProfitClaimed = 0.0
        for (inv in ongoing) {
            val daily = inv.dailyProfitAmount
            val newDays = inv.daysCompleted + 1
            val newEarned = inv.totalEarnedSoFar + daily
            val isCompleted = newDays >= inv.durationDays

            totalProfitClaimed += daily

            val updated = inv.copy(
                daysCompleted = newDays,
                totalEarnedSoFar = newEarned,
                lastPayoutTimestampMillis = System.currentTimeMillis(),
                isCompleted = isCompleted
            )
            database.activeInvestmentDao().updateInvestment(updated)
        }

        if (totalProfitClaimed > 0) {
            database.userDao().addProfit(
                amount = totalProfitClaimed,
                profitIncrement = totalProfitClaimed,
                todayIncrement = totalProfitClaimed
            )

            val tx = TransactionEntity(
                id = UUID.randomUUID().toString(),
                type = "ROI_PROFIT",
                amount = totalProfitClaimed,
                currency = "PKR",
                status = "COMPLETED",
                paymentMethod = "System Auto Payout",
                referenceId = "ROI-${System.currentTimeMillis() % 1000000}",
                timestampMillis = System.currentTimeMillis(),
                accountDetail = "Active Plans (${ongoing.size})",
                noteEn = "Claimed daily ROI for ${ongoing.size} active plan(s)",
                noteUr = "${ongoing.size} ایکٹو پلانز سے روزانہ منافع وصول ہوا"
            )
            database.transactionDao().insertTransaction(tx)
        }

        return Result.success(totalProfitClaimed)
    }

    suspend fun completeTask(task: DailyTaskEntity): Result<Double> {
        if (task.isCompletedToday) {
            return Result.failure(Exception("Task already completed today."))
        }

        val updated = task.copy(
            isCompletedToday = true,
            lastCompletedDateMillis = System.currentTimeMillis()
        )
        database.taskDao().updateTask(updated)
        database.userDao().addBalance(task.rewardAmount)

        val tx = TransactionEntity(
            id = UUID.randomUUID().toString(),
            type = "TASK_REWARD",
            amount = task.rewardAmount,
            currency = "PKR",
            status = "COMPLETED",
            paymentMethod = task.titleEn,
            referenceId = "TASK-${System.currentTimeMillis() % 100000}",
            timestampMillis = System.currentTimeMillis(),
            accountDetail = "Wallet Balance",
            noteEn = "Reward for ${task.titleEn}",
            noteUr = "${task.titleUr} مکمل کرنے پر انعام"
        )
        database.transactionDao().insertTransaction(tx)
        return Result.success(task.rewardAmount)
    }

    suspend fun spinWheelAndWin(prizeAmount: Double): Result<Double> {
        val user = database.userDao().getUserAccountDirect() ?: return Result.failure(Exception("User not found"))
        if (user.availableSpins <= 0) {
            return Result.failure(Exception("No spins remaining!"))
        }

        val updatedUser = user.copy(
            availableSpins = user.availableSpins - 1,
            balance = user.balance + prizeAmount
        )
        database.userDao().insertOrUpdate(updatedUser)

        val tx = TransactionEntity(
            id = UUID.randomUUID().toString(),
            type = "TASK_REWARD",
            amount = prizeAmount,
            currency = "PKR",
            status = "COMPLETED",
            paymentMethod = "Lucky Spin",
            referenceId = "SPIN-${System.currentTimeMillis() % 100000}",
            timestampMillis = System.currentTimeMillis(),
            accountDetail = "Wallet Balance",
            noteEn = "Lucky Spin Reward won: ₨ ${prizeAmount.toInt()}",
            noteUr = "لکی اسپن انعام: ${prizeAmount.toInt()} روپے"
        )
        database.transactionDao().insertTransaction(tx)
        return Result.success(prizeAmount)
    }
}
