package com.example.midtermapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_list_items")
data class ShoppingListItem(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "list_id")
    var listId: Int,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "quantity")
    var quantity: Int,

    @ColumnInfo(name = "category")
    var category: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ShoppingListItem) return false

        if (id != other.id) return false
        if (listId != other.listId) return false
        if (name != other.name) return false
        if (quantity != other.quantity) return false
        if (category != other.category) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + listId
        result = 31 * result + name.hashCode()
        result = 31 * result + quantity
        result = 31 * result + category.hashCode()
        return result
    }
}