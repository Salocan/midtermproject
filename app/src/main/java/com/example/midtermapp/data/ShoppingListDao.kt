package com.example.midtermapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.OnConflictStrategy
@Dao
interface ShoppingListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shoppingList: ShoppingList): Long

    @Update
    suspend fun update(shoppingList: ShoppingList): Int

    @Delete
    suspend fun delete(shoppingList: ShoppingList): Int

    @Query("SELECT * FROM shopping_lists")
    fun getAllShoppingLists(): LiveData<List<ShoppingList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(shoppingLists: List<ShoppingList>)

    @Query("DELETE FROM shopping_lists")
    suspend fun deleteAll()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'shopping_lists'")
    suspend fun resetAutoIncrement()
}