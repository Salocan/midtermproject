// ShoppingListViewModel.kt
package com.example.midtermapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.midtermapp.data.*
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShoppingListViewModel(application: Application) : AndroidViewModel(application) {

    private val shoppingListDao: ShoppingListDao = ShoppingListDatabase.getDatabase(application).shoppingListDao()
    private val shoppingListItemDao: ShoppingListItemDao = ShoppingListDatabase.getDatabase(application).shoppingListItemDao()
    val allShoppingLists: LiveData<List<ShoppingList>> = shoppingListDao.getAllShoppingLists()

    private val firebaseDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("shopping_lists")

    private val _itemsForList = MutableLiveData<List<ShoppingListItem>>()
    val itemsForList: LiveData<List<ShoppingListItem>> get() = _itemsForList

    init {
        syncFromFirebase()
    }

    fun addShoppingList(shoppingList: ShoppingList) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val id = shoppingListDao.insert(shoppingList)
                shoppingList.id = id.toInt()
                firebaseDatabase.child(shoppingList.id.toString()).setValue(shoppingList)
            } catch (e: Exception) {
                Log.e("ShoppingListViewModel", "Error adding shopping list", e)
            }
        }
    }

    fun updateShoppingList(shoppingList: ShoppingList) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                shoppingListDao.update(shoppingList)
                firebaseDatabase.child(shoppingList.id.toString()).setValue(shoppingList)
            } catch (e: Exception) {
                Log.e("ShoppingListViewModel", "Error updating shopping list", e)
            }
        }
    }

    fun deleteShoppingList(shoppingList: ShoppingList) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                shoppingListDao.delete(shoppingList)
                firebaseDatabase.child(shoppingList.id.toString()).removeValue()
            } catch (e: Exception) {
                Log.e("ShoppingListViewModel", "Error deleting shopping list", e)
            }
        }
    }

    fun addShoppingListItem(item: ShoppingListItem) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val id = shoppingListItemDao.insert(item)
                item.id = id.toInt()
                firebaseDatabase.child(item.listId.toString()).child("items").child(item.id.toString()).setValue(item)
                updateItemsForList(item.listId)
            } catch (e: Exception) {
                Log.e("ShoppingListViewModel", "Error adding shopping list item", e)
            }
        }
    }

    fun updateShoppingListItem(item: ShoppingListItem) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                shoppingListItemDao.update(item)
                firebaseDatabase.child(item.listId.toString()).child("items").child(item.id.toString()).setValue(item)
                updateItemsForList(item.listId)
            } catch (e: Exception) {
                Log.e("ShoppingListViewModel", "Error updating shopping list item", e)
            }
        }
    }

    fun deleteShoppingListItem(item: ShoppingListItem) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                shoppingListItemDao.delete(item)
                firebaseDatabase.child(item.listId.toString()).child("items").child(item.id.toString()).removeValue()
                updateItemsForList(item.listId)
            } catch (e: Exception) {
                Log.e("ShoppingListViewModel", "Error deleting shopping list item", e)
            }
        }
    }

    fun getItemsForList(listId: Int): LiveData<List<ShoppingListItem>> {
        return shoppingListItemDao.getItemsForList(listId)
    }

    private fun updateItemsForList(listId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val items = shoppingListItemDao.getItemsForList(listId).value
            withContext(Dispatchers.Main) {
                _itemsForList.value = items
            }
        }
    }

    fun syncFromFirebase() {
        firebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch(Dispatchers.IO) {
                    val shoppingLists = mutableListOf<ShoppingList>()
                    val shoppingListItems = mutableListOf<ShoppingListItem>()

                    try {
                        for (listSnapshot in snapshot.children) {
                            val shoppingList = listSnapshot.getValue(ShoppingList::class.java)
                            shoppingList?.let { shoppingLists.add(it) }

                            val itemsSnapshot = listSnapshot.child("items")
                            for (itemSnapshot in itemsSnapshot.children) {
                                val shoppingListItem = itemSnapshot.getValue(ShoppingListItem::class.java)
                                shoppingListItem?.let { shoppingListItems.add(it) }
                            }
                        }

                        shoppingListDao.insertAll(shoppingLists)
                        shoppingListItemDao.insertAll(shoppingListItems)
                    } catch (e: Exception) {
                        Log.e("ShoppingListViewModel", "Error syncing from Firebase", e)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ShoppingListViewModel", "Error syncing from Firebase", error.toException())
            }
        })
    }

    fun checkFirebaseConnection() {
        firebaseDatabase.root.child(".info/connected").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    Log.d("Firebase", "Connected to Firebase")
                } else {
                    Log.d("Firebase", "Disconnected from Firebase")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error checking connection", error.toException())
            }
        })
    }

    private fun updateProgress() {
        // Implement the logic to update the progress in the UI
    }
}