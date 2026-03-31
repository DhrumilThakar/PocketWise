package com.example.pocketwise.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.pocketwise.data.dao.CategoriesDao
import com.example.pocketwise.data.dao.TransactionDao
import com.example.pocketwise.data.model.Category
import com.example.pocketwise.data.model.Transaction
import com.example.pocketwise.data.model.TransactionType
import com.example.pocketwise.utils.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Database(
    entities = [Transaction::class, Category::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoriesDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            INSTANCE?.let { database ->
                                // Pre-populate the database with default categories
                                CoroutineScope(Dispatchers.IO).launch {
                                    val categoriesDao = database.categoryDao()

                                    categoriesDao.insertCategory(
                                        Category(
                                            "Food",
                                            TransactionType.Expense,
                                            0xFFE57373.toInt()
                                        )
                                    )
                                    categoriesDao.insertCategory(
                                        Category(
                                            "Transport",
                                            TransactionType.Expense,
                                            0xFF64B5F6.toInt()
                                        )
                                    )
                                    categoriesDao.insertCategory(
                                        Category(
                                            "Entertainment",
                                            TransactionType.Expense,
                                            0xFFFFB74D.toInt()
                                        )
                                    )
                                    categoriesDao.insertCategory(
                                        Category(
                                            "Salary",
                                            TransactionType.Income,
                                            0xFF81C784.toInt()
                                        )
                                    )
                                    categoriesDao.insertCategory(
                                        Category(
                                            "Freelance",
                                            TransactionType.Income,
                                            0xFF4DB6AC.toInt()
                                        )
                                    )
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }

}