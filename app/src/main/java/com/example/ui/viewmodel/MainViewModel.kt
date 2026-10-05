package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.InvestmentRepository
import com.example.ui.localization.AppLanguage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UiState(
    val user: UserAccountEntity = UserAccountEntity(),
    val plans: List<InvestmentPlanEntity> = emptyList(),
    val activeInvestments: List<ActiveInvestmentEntity> = emptyList(),
    val transactions: List<TransactionEntity> = emptyList(),
    val referrals: List<ReferralMemberEntity> = emptyList(),
    val dailyTasks: List<DailyTaskEntity> = emptyList(),
    val language: AppLanguage = AppLanguage.URDU,
    val currency: String = "PKR",
    val isDarkMode: Boolean = false,
    val selectedFilter: String = "ALL",
    val isDepositModalOpen: Boolean = false,
    val isWithdrawModalOpen: Boolean = false,
    val isCalculatorOpen: Boolean = false,
    val selectedPlanToInvest: InvestmentPlanEntity? = null,
    val isSpinDialogOpen: Boolean = false,
    val isKycDialogOpen: Boolean = false,
    val isLanguageModalOpen: Boolean = false,
    val selectedReceiptTx: TransactionEntity? = null,
    val snackbarMessage: String? = null,
    val isClaimingProfit: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    val repository = InvestmentRepository(database)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.checkAndSeedInitialUserIfEmpty()
        }

        viewModelScope.launch {
            repository.userAccount.collect { user ->
                if (user != null) {
                    val lang = if (user.selectedLanguage == "en") AppLanguage.ENGLISH else AppLanguage.URDU
                    _uiState.update {
                        it.copy(
                            user = user,
                            language = lang,
                            currency = user.selectedCurrency,
                            isDarkMode = user.isDarkMode
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            repository.allPlans.collect { plans ->
                _uiState.update { it.copy(plans = plans) }
            }
        }

        viewModelScope.launch {
            repository.activeInvestments.collect { active ->
                _uiState.update { it.copy(activeInvestments = active) }
            }
        }

        viewModelScope.launch {
            repository.allTransactions.collect { txs ->
                _uiState.update { it.copy(transactions = txs) }
            }
        }

        viewModelScope.launch {
            repository.referralMembers.collect { refs ->
                _uiState.update { it.copy(referrals = refs) }
            }
        }

        viewModelScope.launch {
            repository.dailyTasks.collect { tasks ->
                _uiState.update { it.copy(dailyTasks = tasks) }
            }
        }
    }

    fun setLanguage(lang: AppLanguage) {
        viewModelScope.launch {
            repository.setLanguage(lang.code)
            _uiState.update { it.copy(language = lang) }
            val confirmation = if (lang == AppLanguage.URDU) {
                "🇵🇰 زبان اردو میں تبدیل کر دی گئی ہے"
            } else {
                "🇺🇸 Language switched to English"
            }
            showSnackbar(confirmation)
        }
    }

    fun toggleLanguage() {
        val next = if (_uiState.value.language == AppLanguage.URDU) AppLanguage.ENGLISH else AppLanguage.URDU
        setLanguage(next)
    }

    fun openLanguageModal() { _uiState.update { it.copy(isLanguageModalOpen = true) } }
    fun closeLanguageModal() { _uiState.update { it.copy(isLanguageModalOpen = false) } }

    fun toggleCurrency() {
        val next = if (_uiState.value.currency == "PKR") "USD" else "PKR"
        viewModelScope.launch {
            repository.setCurrency(next)
            _uiState.update { it.copy(currency = next) }
        }
    }

    fun toggleDarkMode() {
        val next = !_uiState.value.isDarkMode
        viewModelScope.launch {
            repository.setDarkMode(next)
            _uiState.update { it.copy(isDarkMode = next) }
        }
    }

    fun setTransactionFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    // Modal Triggers
    fun openDepositModal() { _uiState.update { it.copy(isDepositModalOpen = true) } }
    fun closeDepositModal() { _uiState.update { it.copy(isDepositModalOpen = false) } }

    fun openWithdrawModal() { _uiState.update { it.copy(isWithdrawModalOpen = true) } }
    fun closeWithdrawModal() { _uiState.update { it.copy(isWithdrawModalOpen = false) } }

    fun openCalculator() { _uiState.update { it.copy(isCalculatorOpen = true) } }
    fun closeCalculator() { _uiState.update { it.copy(isCalculatorOpen = false) } }

    fun openInvestDialog(plan: InvestmentPlanEntity) {
        _uiState.update { it.copy(selectedPlanToInvest = plan) }
    }
    fun closeInvestDialog() {
        _uiState.update { it.copy(selectedPlanToInvest = null) }
    }

    fun openSpinDialog() { _uiState.update { it.copy(isSpinDialogOpen = true) } }
    fun closeSpinDialog() { _uiState.update { it.copy(isSpinDialogOpen = false) } }

    fun openKycDialog() { _uiState.update { it.copy(isKycDialogOpen = true) } }
    fun closeKycDialog() { _uiState.update { it.copy(isKycDialogOpen = false) } }

    fun showReceipt(tx: TransactionEntity) {
        _uiState.update { it.copy(selectedReceiptTx = tx) }
    }
    fun closeReceipt() {
        _uiState.update { it.copy(selectedReceiptTx = null) }
    }

    fun showSnackbar(msg: String) {
        _uiState.update { it.copy(snackbarMessage = msg) }
    }
    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    // Financial Actions
    fun submitDeposit(amount: Double, method: String, tid: String, accountDetail: String) {
        viewModelScope.launch {
            val result = repository.depositFunds(amount, method, tid, accountDetail, autoApprove = true)
            result.onSuccess {
                closeDepositModal()
                showSnackbar(if (_uiState.value.language == AppLanguage.URDU) "ڈپازٹ کامیابی سے والیٹ میں شامل کر دیا گیا!" else "Deposit successfully added to your wallet!")
            }.onFailure { err ->
                showSnackbar(err.message ?: "Deposit failed")
            }
        }
    }

    fun submitWithdrawal(amount: Double, method: String, accountNumber: String, accountHolder: String) {
        viewModelScope.launch {
            val result = repository.requestWithdrawal(amount, method, accountNumber, accountHolder)
            result.onSuccess {
                closeWithdrawModal()
                showSnackbar(if (_uiState.value.language == AppLanguage.URDU) "ودڈرا کی درخواست کامیاب! رقم جلد منتقل ہو جائے گی" else "Withdrawal requested! Funds will arrive shortly.")
            }.onFailure { err ->
                showSnackbar(err.message ?: "Withdrawal failed")
            }
        }
    }

    fun investInSelectedPlan(amount: Double) {
        val plan = _uiState.value.selectedPlanToInvest ?: return
        viewModelScope.launch {
            val result = repository.investInPlan(plan, amount)
            result.onSuccess {
                closeInvestDialog()
                showSnackbar(if (_uiState.value.language == AppLanguage.URDU) "پلان کامیابی سے ایکٹو ہو گیا! روزانہ منافع شروع۔" else "Plan activated successfully! Daily returns started.")
            }.onFailure { err ->
                showSnackbar(err.message ?: "Investment failed")
            }
        }
    }

    fun claimDailyProfit() {
        viewModelScope.launch {
            _uiState.update { it.copy(isClaimingProfit = true) }
            val result = repository.claimAllDailyProfit()
            _uiState.update { it.copy(isClaimingProfit = false) }
            result.onSuccess { profit ->
                showSnackbar(if (_uiState.value.language == AppLanguage.URDU) "مبارک ہو! $profit روپے روزانہ منافع والیٹ میں شامل کر دیا گیا!" else "Congratulations! ₨ $profit daily profit credited to your wallet!")
            }.onFailure { err ->
                showSnackbar(err.message ?: "Could not claim profit")
            }
        }
    }

    fun completeTask(task: DailyTaskEntity) {
        viewModelScope.launch {
            val result = repository.completeTask(task)
            result.onSuccess { reward ->
                showSnackbar(if (_uiState.value.language == AppLanguage.URDU) "ٹاسک مکمل! $reward روپے بونس والیٹ میں شامل ہو گیا۔" else "Task completed! ₨ $reward bonus added to wallet.")
            }.onFailure { err ->
                showSnackbar(err.message ?: "Task error")
            }
        }
    }

    fun spinWheelAndCollect(prize: Double) {
        viewModelScope.launch {
            val result = repository.spinWheelAndWin(prize)
            result.onSuccess {
                showSnackbar(if (_uiState.value.language == AppLanguage.URDU) "شاندار! آپ نے $prize روپے کیش انعام جیت لیا!" else "Awesome! You won ₨ $prize in Lucky Spin!")
            }.onFailure { err ->
                showSnackbar(err.message ?: "Spin failed")
            }
        }
    }

    fun formatCurrency(amount: Double): String {
        return if (_uiState.value.currency == "USD") {
            val usd = amount / 280.0
            String.format("$%.2f", usd)
        } else {
            String.format("₨ %,d", amount.toInt())
        }
    }
}
