package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_account WHERE id = 1 LIMIT 1")
    fun getUserAccount(): Flow<UserAccountEntity?>

    @Query("SELECT * FROM user_account WHERE id = 1 LIMIT 1")
    suspend fun getUserAccountDirect(): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserAccountEntity)

    @Query("UPDATE user_account SET balance = balance + :amount, totalProfitEarned = totalProfitEarned + :profitIncrement, todayProfit = todayProfit + :todayIncrement WHERE id = 1")
    suspend fun addProfit(amount: Double, profitIncrement: Double, todayIncrement: Double)

    @Query("UPDATE user_account SET balance = balance + :amount WHERE id = 1")
    suspend fun addBalance(amount: Double)

    @Query("UPDATE user_account SET balance = balance - :amount WHERE id = 1")
    suspend fun deductBalance(amount: Double)

    @Query("UPDATE user_account SET totalInvested = totalInvested + :amount WHERE id = 1")
    suspend fun addTotalInvested(amount: Double)

    @Query("UPDATE user_account SET selectedLanguage = :lang WHERE id = 1")
    suspend fun updateLanguage(lang: String)

    @Query("UPDATE user_account SET selectedCurrency = :curr WHERE id = 1")
    suspend fun updateCurrency(curr: String)

    @Query("UPDATE user_account SET isDarkMode = :darkMode WHERE id = 1")
    suspend fun updateDarkMode(darkMode: Boolean)
}

@Dao
interface InvestmentPlanDao {
    @Query("SELECT * FROM investment_plans ORDER BY sortOrder ASC")
    fun getAllPlans(): Flow<List<InvestmentPlanEntity>>

    @Query("SELECT * FROM investment_plans WHERE id = :planId LIMIT 1")
    suspend fun getPlanById(planId: String): InvestmentPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlans(plans: List<InvestmentPlanEntity>)
}

@Dao
interface ActiveInvestmentDao {
    @Query("SELECT * FROM active_investments ORDER BY startTimestampMillis DESC")
    fun getAllActiveInvestments(): Flow<List<ActiveInvestmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestment(investment: ActiveInvestmentEntity)

    @Update
    suspend fun updateInvestment(investment: ActiveInvestmentEntity)

    @Query("SELECT * FROM active_investments WHERE isCompleted = 0")
    suspend fun getOngoingInvestments(): List<ActiveInvestmentEntity>
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestampMillis DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY timestampMillis DESC")
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)
}

@Dao
interface ReferralDao {
    @Query("SELECT * FROM referral_members ORDER BY joinDateMillis DESC")
    fun getAllReferrals(): Flow<List<ReferralMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferrals(members: List<ReferralMemberEntity>)
}

@Dao
interface DailyTaskDao {
    @Query("SELECT * FROM daily_tasks ORDER BY rewardAmount ASC")
    fun getAllTasks(): Flow<List<DailyTaskEntity>>

    @Update
    suspend fun updateTask(task: DailyTaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<DailyTaskEntity>)
}
