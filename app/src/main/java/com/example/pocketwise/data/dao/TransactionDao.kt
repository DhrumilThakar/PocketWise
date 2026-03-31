package com.example.pocketwise.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.pocketwise.data.model.Transaction
import com.example.pocketwise.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTranasaction(): Flow<List<Transaction>>


    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type ")
    fun getTotalByTypes(type: TransactionType): Double?

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>>

    @Query("""SELECT category,SUM(amount) as total FROM transactions WHERE type= :type AND date>=:Start""")
    fun getCategoryTotals(type: TransactionType, Start: Long=0): Flow<Map<String, Double>>

    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transactions: Transaction)

}