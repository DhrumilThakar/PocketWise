package com.example.pocketmaster.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.example.pocketmaster.data.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (sms in messages) {
                val body = sms.displayMessageBody
                val sender = sms.displayOriginatingAddress ?: "Unknown"
                
                val transaction = SmsParser.parseSms(body, sender)
                if (transaction != null) {
                    val db = AppDatabase.getDatabase(context)
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            db.transactionDao().insert(transaction)
                            Log.d("SmsReceiver", "Transaction saved: ${transaction.amount}")
                        } catch (e: Exception) {
                            Log.e("SmsReceiver", "Error saving transaction", e)
                        }
                    }
                }
            }
        }
    }
}