package com.example.pocketwise.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketwise.data.database.AppDatabase
import com.example.pocketwise.data.model.Transaction
import com.example.pocketwise.data.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import com.example.pocketwise.data.model.Category
import com.example.pocketwise.data.model.TransactionType
import kotlinx.coroutines.launch

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FinanceRepository
    val allTransactions: Flow<List<Transaction>>
    val allCategories: Flow<List<Category>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = FinanceRepository(database.transactionDao(), database.categoryDao())

        // Assign the flows from the repository
        allTransactions = repository.allTransaction
        allCategories = repository.allCategory
    }

    // Flow-based queries (can be called directly as they are not suspend)
    fun getTransactionsByType(type: TransactionType) = repository.getTranasactionByType(type)

    fun getCategoryTotals(type: TransactionType) = repository.getCategoryTotals(type)

    // Suspend functions must be called within a coroutine scope (viewModelScope)
    fun addTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.addTransaction(transaction)
    }

    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.deleteTransaction(transaction)
    }

    /**
     * Note: Since getTotalByType is a suspend function that returns a value (Double),
     * you usually observe this via a LiveData or StateFlow if you want to show it in UI.
     */
    suspend fun getTotalByType(type: TransactionType): Double {
        return repository.getTotalByType(type)
    }
}