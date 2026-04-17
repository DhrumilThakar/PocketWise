package com.example.pocketmaster.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketmaster.data.database.AppDatabase
import com.example.pocketmaster.data.model.*
import com.example.pocketmaster.data.repository.FinanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FinanceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FinanceRepository

    private val _transactionUpdateTrigger = MutableSharedFlow<Unit>()
    private val _transactionFilter = MutableStateFlow(TransactionFilter.ALL)

    init {
        val database = AppDatabase.getDatabase(application)
        repository = FinanceRepository(
            database.transactionDao(),
            database.categoryDao(),
            database.personDao(),
            database.debtDao()
        )
    }

    val filteredTransactions = combine(
        _transactionFilter,
        _transactionUpdateTrigger.onStart { emit(Unit) }
    ) { filter, _ ->
        when (filter) {
            TransactionFilter.ALL -> repository.allTransactions
            TransactionFilter.INCOME -> repository.getTransactionsByType(TransactionType.INCOME)
            TransactionFilter.EXPENSE -> repository.getTransactionsByType(TransactionType.EXPENSE)
        }
    }.flatMapLatest { it }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val dashboardState: StateFlow<DashboardState> = combine(
        repository.getTransactionsByType(TransactionType.INCOME),
        repository.getTransactionsByType(TransactionType.EXPENSE),
        repository.getCategoryTotals(TransactionType.EXPENSE),
        _transactionUpdateTrigger.onStart { emit(Unit) }
    ) { incomeTransactions, expenseTransactions, categoryTotals, _ ->
        val totalIncome = incomeTransactions.sumOf { it.amount }
        val totalExpense = expenseTransactions.sumOf { it.amount }

        DashboardState(
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            balance = totalIncome - totalExpense,
            expenseCategories = categoryTotals.map { categoryTotal ->
                ExpenseCategoryData(
                    category = categoryTotal.name,
                    amount = categoryTotal.total,
                    percentage = if (totalExpense > 0) (categoryTotal.total / totalExpense) * 100 else 0.0
                )
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardState()
    )

    // Debt Summaries
    val debtSummary = combine(
        repository.totalLent,
        repository.totalBorrowed
    ) { lent, borrowed ->
        Pair(lent ?: 0.0, borrowed ?: 0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(0.0, 0.0))

    val allCategories = repository.allCategories
    fun getCategoriesByType(type: TransactionType) = repository.getCategoriesByType(type)
    fun getCategoryTotals(type: TransactionType, startDate: Long = 0) = repository.getCategoryTotals(type, startDate)

    fun addTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.addTransaction(transaction)
        _transactionUpdateTrigger.emit(Unit)
    }

    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.deleteTransaction(transaction)
        _transactionUpdateTrigger.emit(Unit)
    }

    fun setTransactionFilter(filter: TransactionFilter) {
        _transactionFilter.value = filter
    }

    fun addCategory(category: Category) = viewModelScope.launch {
        repository.addCategory(category)
    }

    // --- Person & Debt Operations ---
    val allPersons = repository.allPersons

    fun addPerson(name: String) = viewModelScope.launch {
        repository.addPerson(Person(name = name))
    }

    fun addDebt(personId: Int, amount: Double, isLent: Boolean, description: String) = viewModelScope.launch {
        repository.addDebt(Debt(personId = personId, amount = amount, isLent = isLent, description = description))
    }

    fun getBalanceForPerson(personId: Int) = repository.getBalanceForPerson(personId)
    fun getDebtsByPerson(personId: Int) = repository.getDebtsByPerson(personId)
}