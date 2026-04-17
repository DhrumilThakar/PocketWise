package com.example.pocketmaster.utils

import com.example.pocketmaster.data.model.Transaction
import com.example.pocketmaster.data.model.TransactionType

object SmsParser {
    private val AMOUNT_REGEX = Regex("(?i)(?:rs|inr|amt)\\.?\\s*([\\d,]+\\.?\\d*)")
    private val DEBIT_KEYWORDS = listOf("debited", "spent", "paid", "sent to", "withdrawal")
    private val CREDIT_KEYWORDS = listOf("credited", "received", "added to", "deposit")

    fun parseSms(message: String, sender: String): Transaction? {
        val amountMatch = AMOUNT_REGEX.find(message) ?: return null
        val amount = amountMatch.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null

        val type = when {
            DEBIT_KEYWORDS.any { message.contains(it, ignoreCase = true) } -> TransactionType.EXPENSE
            CREDIT_KEYWORDS.any { message.contains(it, ignoreCase = true) } -> TransactionType.INCOME
            else -> return null
        }

        // Simple categorization based on common keywords
        val category = when {
            message.contains("swiggy", ignoreCase = true) || message.contains("zomato", ignoreCase = true) -> "Food"
            message.contains("uber", ignoreCase = true) || message.contains("ola", ignoreCase = true) -> "Transport"
            message.contains("recharge", ignoreCase = true) || message.contains("jio", ignoreCase = true) -> "Bills"
            else -> "Auto-Detected"
        }

        return Transaction(
            amount = amount,
            description = "SMS from $sender: ${message.take(30)}...",
            category = category,
            type = type,
            date = System.currentTimeMillis()
        )
    }
}