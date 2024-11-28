package com.example.midtermapp.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ShoppingListItemDao {
    @Insert
    suspend fun insert(item: ShoppingListItem): Long

    @Update
    suspend fun update(item: ShoppingListItem): Int

    @Delete
    suspend fun delete(item: ShoppingListItem): Int

    @Query("SELECT * FROM shopping_list_items WHERE list_id = :listId")
    fun getItemsForList(listId: Int): LiveData<List<ShoppingListItem>>

    @Query("SELECT * FROM shopping_list_items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: Int): ShoppingListItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ShoppingListItem>)

    @Query("DELETE FROM shopping_list_items")
    suspend fun deleteAll()
    @Query("SELECT * FROM shopping_list_items WHERE list_id = :listId")
    suspend fun getItemsForListSync(listId: Int): List<ShoppingListItem>
}