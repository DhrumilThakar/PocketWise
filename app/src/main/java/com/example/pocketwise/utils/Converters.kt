package com.example.pocketwise.utils

import androidx.room.TypeConverter
import com.example.pocketwise.data.model.TransactionType

class Converters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)}