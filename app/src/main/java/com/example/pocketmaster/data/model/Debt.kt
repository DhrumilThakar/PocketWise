package com.example.pocketmaster.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "debts",
    foreignKeys = [
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Debt(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val personId: Int,
    val amount: Double,
    val isLent: Boolean, // true if user lent money, false if user borrowed
    val description: String,
    val date: Long = System.currentTimeMillis()
)
