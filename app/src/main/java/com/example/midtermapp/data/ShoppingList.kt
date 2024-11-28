
package com.example.midtermapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_lists")
data class ShoppingList(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "is_preset")
    var isPreset: Boolean = false
) {
    constructor() : this(0, "", false)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ShoppingList) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (isPreset != other.isPreset) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + isPreset.hashCode()
        return result
    }
}