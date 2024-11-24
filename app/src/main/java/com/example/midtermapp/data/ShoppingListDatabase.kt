package com.example.midtermapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ShoppingList::class], version = 1, exportSchema = false)
abstract class ShoppingListDatabase : RoomDatabase() {
    abstract fun shoppingListDao(): ShoppingListDao

    companion object {
        @Volatile
        private var INSTANCE: ShoppingListDatabase? = null

        fun getDatabase(context: Context): ShoppingListDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShoppingListDatabase::class.java,
                    "shopping_list_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}