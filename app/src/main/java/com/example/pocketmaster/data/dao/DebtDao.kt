package com.example.pocketmaster.data.dao

import androidx.room.*
import com.example.pocketmaster.data.model.Debt
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {
    @Query("SELECT * FROM debts WHERE personId = :personId ORDER BY date DESC")
    fun getDebtsByPerson(personId: Int): Flow<List<Debt>>

    @Query("SELECT * FROM debts ORDER BY date DESC")
    fun getAllDebts(): Flow<List<Debt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(debt: Debt)

    @Delete
    suspend fun delete(debt: Debt)

    @Query("SELECT SUM(CASE WHEN isLent = 1 THEN amount ELSE -amount END) FROM debts WHERE personId = :personId")
    fun getBalanceForPerson(personId: Int): Flow<Double?>

    @Query("SELECT SUM(CASE WHEN isLent = 1 THEN amount ELSE 0 END) FROM debts")
    fun getTotalLent(): Flow<Double?>

    @Query("SELECT SUM(CASE WHEN isLent = 0 THEN amount ELSE 0 END) FROM debts")
    fun getTotalBorrowed(): Flow<Double?>
}
