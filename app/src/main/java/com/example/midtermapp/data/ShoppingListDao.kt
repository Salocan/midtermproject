package com.example.midtermapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ShoppingListDao {
    @Insert
    suspend fun insert(shoppingList: ShoppingList): Long

    @Update
    suspend fun update(shoppingList: ShoppingList): Int

    @Delete
    suspend fun delete(shoppingList: ShoppingList): Int

    @Query("SELECT * FROM shopping_lists")
    fun getAllShoppingLists(): LiveData<List<ShoppingList>>

    @Insert
    suspend fun insertAll(shoppingLists: List<ShoppingList>)

    @Query("DELETE FROM shopping_lists")
    suspend fun deleteAll()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'shopping_lists'")
    suspend fun resetAutoIncrement()
}