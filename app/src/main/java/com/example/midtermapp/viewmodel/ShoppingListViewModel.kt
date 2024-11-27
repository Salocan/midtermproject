package com.example.midtermapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.midtermapp.data.*
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.database.sqlite.SQLiteConstraintException

class ShoppingListViewModel(application: Application) : AndroidViewModel(application) {

    private val shoppingListDao: ShoppingListDao = ShoppingListDatabase.getDatabase(application).shoppingListDao()
    private val shoppingListItemDao: ShoppingListItemDao = ShoppingListDatabase.getDatabase(application).shoppingListItemDao()
    val allShoppingLists: LiveData<List<ShoppingList>> = shoppingListDao.getAllShoppingLists()

    private val firebaseDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("shopping_lists")

    init {
        syncFromFirebase()
    }

    fun addShoppingList(shoppingList: ShoppingList) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newId = shoppingListDao.insert(shoppingList)
                shoppingList.id = newId.toInt()
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
                val existingItem = shoppingListItemDao.getItemById(item.id)
                if (existingItem == null) {
                    val newId = shoppingListItemDao.insert(item)
                    item.id = newId.toInt()
                } else {
                    shoppingListItemDao.update(item)
                }
                firebaseDatabase.child(item.listId.toString()).child("items").child(item.id.toString()).setValue(item)
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
            } catch (e: Exception) {
                Log.e("ShoppingListViewModel", "Error deleting shopping list item", e)
            }
        }
    }

    fun getItemsForList(listId: Int): LiveData<List<ShoppingListItem>> {
        return shoppingListItemDao.getItemsForList(listId)
    }

    private fun syncFromFirebase() {
        firebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch(Dispatchers.IO) {
                    val shoppingLists = mutableListOf<ShoppingList>()
                    try {
                        for (listSnapshot in snapshot.children) {
                            val shoppingList = listSnapshot.getValue(ShoppingList::class.java)
                            if (shoppingList != null) {
                                shoppingLists.add(shoppingList)
                            }
                        }
                        shoppingListDao.deleteAll()
                        shoppingListDao.insertAll(shoppingLists)
                    } catch (e: SQLiteConstraintException) {
                        Log.e("ShoppingListViewModel", "Error syncing from Firebase", e)
                        for (shoppingList in shoppingLists) {
                            shoppingListDao.update(shoppingList)
                        }
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
                    Log.d("Firebase", "Connected to Firebase Realtime Database")
                } else {
                    Log.d("Firebase", "Disconnected from Firebase Realtime Database")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error checking connection", error.toException())
            }
        })
    }

    fun resetDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                shoppingListDao.deleteAll()
                shoppingListDao.resetAutoIncrement()
            } catch (e: Exception) {
                Log.e("ShoppingListViewModel", "Error resetting database", e)
            }
        }
    }
}