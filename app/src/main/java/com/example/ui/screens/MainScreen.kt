package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.*
import com.example.ui.localization.AppLanguage
import com.example.ui.viewmodel.MainViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentRoute by remember { mutableStateOf("home") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopNavBar(
                language = uiState.language,
                currency = uiState.currency,
                onLanguageToggle = { viewModel.openLanguageModal() },
                onCurrencyToggle = { viewModel.toggleCurrency() },
                onSupportClick = { currentRoute = "support" },
                onProfileClick = { currentRoute = "profile" }
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route -> currentRoute = route },
                language = uiState.language
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentRoute,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScreenTransition"
            ) { route ->
                when (route) {
                    "home" -> HomeScreen(
                        user = uiState.user,
                        plans = uiState.plans,
                        transactions = uiState.transactions,
                        language = uiState.language,
                        currency = uiState.currency,
                        onNavigate = { currentRoute = it },
                        onDepositClick = { viewModel.openDepositModal() },
                        onWithdrawClick = { viewModel.openWithdrawModal() },
                        onInvestClick = { currentRoute = "plans" },
                        onCalculatorClick = { viewModel.openCalculator() },
                        onClaimProfitClick = { viewModel.claimDailyProfit() },
                        onSelectPlan = { plan -> viewModel.openInvestDialog(plan) },
                        onSpinClick = { viewModel.openSpinDialog() },
                        onShowReceipt = { tx -> viewModel.showReceipt(tx) },
                        onLanguageChange = { lang -> viewModel.setLanguage(lang) },
                        isClaimingProfit = uiState.isClaimingProfit
                    )
                    "plans" -> PlansScreen(
                        plans = uiState.plans,
                        language = uiState.language,
                        currency = uiState.currency,
                        onSelectPlan = { plan -> viewModel.openInvestDialog(plan) },
                        onOpenCalculator = { viewModel.openCalculator() }
                    )
                    "portfolio" -> ActiveInvestmentsScreen(
                        activeInvestments = uiState.activeInvestments,
                        language = uiState.language,
                        currency = uiState.currency,
                        onClaimProfit = { viewModel.claimDailyProfit() },
                        onExplorePlans = { currentRoute = "plans" },
                        isClaiming = uiState.isClaimingProfit
                    )
                    "wallet" -> WalletScreen(
                        user = uiState.user,
                        transactions = uiState.transactions,
                        language = uiState.language,
                        currency = uiState.currency,
                        onDepositClick = { viewModel.openDepositModal() },
                        onWithdrawClick = { viewModel.openWithdrawModal() },
                        onShowReceipt = { tx -> viewModel.showReceipt(tx) }
                    )
                    "rewards" -> TasksRewardsScreen(
                        user = uiState.user,
                        tasks = uiState.dailyTasks,
                        language = uiState.language,
                        currency = uiState.currency,
                        onSpinClick = { viewModel.openSpinDialog() },
                        onCompleteTask = { task -> viewModel.completeTask(task) }
                    )
                    "referrals" -> ReferralScreen(
                        user = uiState.user,
                        referrals = uiState.referrals,
                        language = uiState.language,
                        currency = uiState.currency
                    )
                    "support" -> HelpSupportScreen(
                        language = uiState.language
                    )
                    "profile" -> ProfileScreen(
                        user = uiState.user,
                        language = uiState.language,
                        currency = uiState.currency,
                        onLanguageChange = { lang -> viewModel.setLanguage(lang) },
                        onCurrencyToggle = { viewModel.toggleCurrency() },
                        onToggleDarkMode = { viewModel.toggleDarkMode() },
                        onShowKycDialog = { viewModel.openKycDialog() },
                        onOpenLanguageDialog = { viewModel.openLanguageModal() }
                    )
                }
            }
        }
    }

    // Modals & Dialogs
    if (uiState.isLanguageModalOpen) {
        LanguageSelectionDialog(
            currentLanguage = uiState.language,
            onLanguageSelected = { lang ->
                viewModel.setLanguage(lang)
            },
            onDismiss = { viewModel.closeLanguageModal() }
        )
    }

    if (uiState.isDepositModalOpen) {
        DepositModal(
            language = uiState.language,
            currency = uiState.currency,
            onDismiss = { viewModel.closeDepositModal() },
            onSubmitDeposit = { amount, method, tid, detail ->
                viewModel.submitDeposit(amount, method, tid, detail)
            }
        )
    }

    if (uiState.isWithdrawModalOpen) {
        WithdrawModal(
            availableBalance = uiState.user.balance,
            language = uiState.language,
            currency = uiState.currency,
            onDismiss = { viewModel.closeWithdrawModal() },
            onSubmitWithdraw = { amount, method, accountNo, holder ->
                viewModel.submitWithdrawal(amount, method, accountNo, holder)
            }
        )
    }

    if (uiState.isCalculatorOpen) {
        ProfitCalculatorModal(
            plans = uiState.plans,
            language = uiState.language,
            currency = uiState.currency,
            onDismiss = { viewModel.closeCalculator() },
            onInvestNow = { plan, amount ->
                viewModel.closeCalculator()
                viewModel.openInvestDialog(plan)
            }
        )
    }

    uiState.selectedPlanToInvest?.let { plan ->
        InvestModal(
            plan = plan,
            walletBalance = uiState.user.balance,
            language = uiState.language,
            currency = uiState.currency,
            onDismiss = { viewModel.closeInvestDialog() },
            onConfirmInvest = { amount ->
                viewModel.investInSelectedPlan(amount)
            },
            onNavigateDeposit = {
                viewModel.openDepositModal()
            }
        )
    }

    if (uiState.isSpinDialogOpen) {
        SpinWheelDialog(
            availableSpins = uiState.user.availableSpins,
            language = uiState.language,
            onDismiss = { viewModel.closeSpinDialog() },
            onSpinCollect = { prize ->
                viewModel.spinWheelAndCollect(prize)
            }
        )
    }

    uiState.selectedReceiptTx?.let { tx ->
        ReceiptDialog(
            transaction = tx,
            language = uiState.language,
            currency = uiState.currency,
            onDismiss = { viewModel.closeReceipt() }
        )
    }
}
