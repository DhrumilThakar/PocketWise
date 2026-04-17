package com.example.pocketmaster.data.repository

import com.example.pocketmaster.data.dao.CategoryDao
import com.example.pocketmaster.data.dao.TransactionDao
import com.example.pocketmaster.data.dao.PersonDao
import com.example.pocketmaster.data.dao.DebtDao
import com.example.pocketmaster.data.model.*
import kotlinx.coroutines.flow.Flow

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val personDao: PersonDao,
    private val debtDao: DebtDao
) {
    val allTransactions = transactionDao.getAllTransactions()
    val allCategories = categoryDao.getAllCategories()
    val allPersons = personDao.getAllPersons()
    val allDebts = debtDao.getAllDebts()
    val totalLent = debtDao.getTotalLent()
    val totalBorrowed = debtDao.getTotalBorrowed()

    fun getTransactionsByType(type: TransactionType) =
        transactionDao.getTransactionsByType(type)

    fun getCategoriesByType(type: TransactionType) =
        categoryDao.getCategoriesByType(type)
        
    suspend fun getAllTransactions(): List<Transaction> {
        return transactionDao.getAllTransactionsList()
    }
    
    fun getCategoryTotals(type: TransactionType, startDate: Long = 0): Flow<List<CategoryTotal>> =
        categoryDao.getCategoryTotals(type, startDate)

    suspend fun getTotalByType(type: TransactionType) =
        transactionDao.getTotalByType(type) ?: 0.0

    suspend fun addTransaction(transaction: Transaction) {
        transactionDao.insert(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction)
    }

    suspend fun addCategory(category: Category) {
        categoryDao.insert(category)
    }

    // Person & Debt methods
    suspend fun addPerson(person: Person) = personDao.insert(person)
    
    suspend fun deletePerson(person: Person) = personDao.delete(person)
    
    suspend fun getPersonById(id: Int) = personDao.getPersonById(id)
    
    suspend fun addDebt(debt: Debt) = debtDao.insert(debt)
    
    suspend fun deleteDebt(debt: Debt) = debtDao.delete(debt)
    
    fun getDebtsByPerson(personId: Int) = debtDao.getDebtsByPerson(personId)
    
    fun getBalanceForPerson(personId: Int) = debtDao.getBalanceForPerson(personId)
}
