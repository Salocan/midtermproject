package com.example.midtermapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.midtermapp.data.*
import com.google.firebase.database.*
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
            val presetId = shoppingListDao.insert(preset.copy(isPreset = true))
            preset.id = presetId.toInt()
            firebaseDatabase.child("presets").child(preset.id.toString()).setValue(preset)
        }
    }

    fun importPreset(preset: ShoppingList) {
        viewModelScope.launch(Dispatchers.IO) {
            val newList = preset.copy(id = 0, isPreset = false)
            val newListId = shoppingListDao.insert(newList)
            newList.id = newListId.toInt()
            firebaseDatabase.child("shopping_lists").child(newList.id.toString()).setValue(newList)

            val presetItems = shoppingListItemDao.getItemsForListSync(preset.id)

            val newItems = presetItems.map { it.copy(id = 0, listId = newList.id) }
            shoppingListItemDao.insertAll(newItems)
            newItems.forEach { item ->
                firebaseDatabase.child("shopping_list_items").child(item.id.toString()).setValue(item)
            }
        }
    }

    fun checkFirebaseConnection() {
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
    }


    fun listenForDeletions() {
        firebaseDatabase.child("shopping_lists").addChildEventListener(object : ChildEventListener {
            override fun onChildRemoved(snapshot: DataSnapshot) {
                val shoppingList = snapshot.getValue(ShoppingList::class.java)
                shoppingList?.let {
                    viewModelScope.launch(Dispatchers.IO) {
                        shoppingListDao.delete(it)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

        firebaseDatabase.child("shopping_list_items").addChildEventListener(object : ChildEventListener {
            override fun onChildRemoved(snapshot: DataSnapshot) {
                val shoppingListItem = snapshot.getValue(ShoppingListItem::class.java)
                shoppingListItem?.let {
                    viewModelScope.launch(Dispatchers.IO) {
                        shoppingListItemDao.delete(it)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

        firebaseDatabase.child("presets").addChildEventListener(object : ChildEventListener {
            override fun onChildRemoved(snapshot: DataSnapshot) {
                val preset = snapshot.getValue(ShoppingList::class.java)
                preset?.let {
                    viewModelScope.launch(Dispatchers.IO) {
                        shoppingListDao.delete(it)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}