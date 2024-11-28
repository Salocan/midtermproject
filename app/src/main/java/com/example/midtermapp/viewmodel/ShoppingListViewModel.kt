// ShoppingListViewModel.kt
package com.example.midtermapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.midtermapp.data.*
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShoppingListViewModel(application: Application) : AndroidViewModel(application) {
    private val shoppingListDao: ShoppingListDao = ShoppingListDatabase.getDatabase(application).shoppingListDao()
    private val shoppingListItemDao: ShoppingListItemDao = ShoppingListDatabase.getDatabase(application).shoppingListItemDao()
    private val firebaseDatabase = FirebaseDatabase.getInstance().reference

    val allShoppingLists: LiveData<List<ShoppingList>> = shoppingListDao.getAllShoppingLists()
    val allPresets: LiveData<List<ShoppingList>> = shoppingListDao.getAllPresets()

    fun addShoppingList(shoppingList: ShoppingList) {
        viewModelScope.launch(Dispatchers.IO) {
            val listId = shoppingListDao.insert(shoppingList)
            shoppingList.id = listId.toInt()
            firebaseDatabase.child("shopping_lists").child(shoppingList.id.toString()).setValue(shoppingList)
        }
    }

    fun addShoppingListItem(item: ShoppingListItem) {
        viewModelScope.launch(Dispatchers.IO) {
            val itemId = shoppingListItemDao.insert(item)
            item.id = itemId.toInt()
            firebaseDatabase.child("shopping_list_items").child(item.id.toString()).setValue(item)
        }
    }

    fun updateShoppingListItem(item: ShoppingListItem) {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingListItemDao.update(item)
            firebaseDatabase.child("shopping_list_items").child(item.id.toString()).setValue(item)
        }
    }

    fun deleteShoppingListItem(item: ShoppingListItem) {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingListItemDao.delete(item)
            firebaseDatabase.child("shopping_list_items").child(item.id.toString()).removeValue()
        }
    }

    fun getItemsForList(listId: Int): LiveData<List<ShoppingListItem>> {
        return shoppingListItemDao.getItemsForList(listId)
    }

    fun addPreset(preset: ShoppingList) {
        viewModelScope.launch(Dispatchers.IO) {
            val presetId = shoppingListDao.insert(preset.copy(isPreset = true)) // Explicitly set isPreset
            preset.id = presetId.toInt()
            firebaseDatabase.child("presets").child(preset.id.toString()).setValue(preset)
        }
    }

    fun importPreset(preset: ShoppingList) {
        viewModelScope.launch(Dispatchers.IO) {
            // Copy the preset to a new shopping list
            val newList = preset.copy(id = 0, isPreset = false)
            val newListId = shoppingListDao.insert(newList)
            newList.id = newListId.toInt()
            firebaseDatabase.child("shopping_lists").child(newList.id.toString()).setValue(newList)

            // Fetch items associated with the preset
            val presetItems = shoppingListItemDao.getItemsForListSync(preset.id)

            // Copy items to the new shopping list
            val newItems = presetItems.map { it.copy(id = 0, listId = newList.id) }
            shoppingListItemDao.insertAll(newItems)
            newItems.forEach { item ->
                firebaseDatabase.child("shopping_list_items").child(item.id.toString()).setValue(item)
            }
        }
    }

    // Add the missing methods
    fun checkFirebaseConnection() {
        // Implement the logic to check Firebase connection
    }

    fun deleteShoppingList(shoppingList: ShoppingList) {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingListDao.delete(shoppingList)
            firebaseDatabase.child("shopping_lists").child(shoppingList.id.toString()).removeValue()
        }
    }

    fun updateShoppingList(shoppingList: ShoppingList) {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingListDao.update(shoppingList)
            firebaseDatabase.child("shopping_lists").child(shoppingList.id.toString()).setValue(shoppingList)
        }
    }

    fun deletePreset(preset: ShoppingList) {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingListDao.delete(preset)
            firebaseDatabase.child("presets").child(preset.id.toString()).removeValue()
        }
    }

    fun updatePreset(preset: ShoppingList) {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingListDao.update(preset)
            firebaseDatabase.child("presets").child(preset.id.toString()).setValue(preset)
        }
    }

    fun syncFromFirebase() {
        // Implement the logic to sync data from Firebase
    }
}