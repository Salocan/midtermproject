package com.example.midtermapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_lists")
data class ShoppingList(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,  // Changed from val to var

    @ColumnInfo(name = "name")
    var name: String
)