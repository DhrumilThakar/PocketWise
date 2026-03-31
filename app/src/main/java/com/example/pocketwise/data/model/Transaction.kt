package com.example.pocketwise.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "transactions")
data class Transaction(
                        @PrimaryKey(autoGenerate = true)
                        val ID: Int = 0,
                        val amount: Double = 0.0,
                        val type: TransactionType,
                        val category: String = "",
                        val date: Long = System.currentTimeMillis(),
                        val description: String = "")
