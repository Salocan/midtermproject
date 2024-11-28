// ShoppingListDao.kt
package com.example.midtermapp.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ShoppingListDao {
    @Insert
    suspend fun insert(shoppingList: ShoppingList): Long

    @Update
    suspend fun update(shoppingList: ShoppingList): Int

    @Delete
    suspend fun delete(shoppingList: ShoppingList): Int

    @Query("SELECT * FROM shopping_lists WHERE is_preset = 1")
    fun getAllPresets(): LiveData<List<ShoppingList>>

    @Query("SELECT * FROM shopping_lists WHERE is_preset = 0")
    fun getAllShoppingLists(): LiveData<List<ShoppingList>>
}