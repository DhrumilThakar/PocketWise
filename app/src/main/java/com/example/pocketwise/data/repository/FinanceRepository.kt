package com.example.pocketwise.data.repository

import com.example.pocketwise.data.dao.CategoriesDao
import com.example.pocketwise.data.dao.TransactionDao
import com.example.pocketwise.data.model.Category
import com.example.pocketwise.data.model.Transaction
import com.example.pocketwise.data.model.TransactionType

class FinanceRepository(private val TransactionDao: TransactionDao,private val CategoriesDao: CategoriesDao) {

    val allTransaction= TransactionDao.getAllTranasaction()

    val allCategory = CategoriesDao.getAllCategories()

    fun getTranasactionByType(type: TransactionType) = TransactionDao.getTransactionsByType(type)
    fun getCategoryTotals(type: TransactionType) = TransactionDao.getCategoryTotals(type)
    fun categoryTotals(type: TransactionType, start: Long=0) = TransactionDao.getCategoryTotals(type, start)
    suspend fun getTotalByType(type: TransactionType) = TransactionDao.getTotalByTypes(type)?:0.0
    suspend fun addTransaction(transaction: Transaction) = TransactionDao.insertTransaction(transaction)
    suspend fun deleteTransaction(transaction: Transaction) = TransactionDao.deleteTransaction(transaction)
    suspend fun addCategory(category: Category) = CategoriesDao.insertCategory(category)
}