package com.example.midtermapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.midtermapp.data.ShoppingList
import com.example.midtermapp.data.ShoppingListDao
import com.example.midtermapp.data.ShoppingListDatabase
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShoppingListViewModel(application: Application) : AndroidViewModel(application) {

    private val shoppingListDao: ShoppingListDao =
        ShoppingListDatabase.getDatabase(application).shoppingListDao()
    val allShoppingLists: LiveData<List<ShoppingList>> = shoppingListDao.getAllShoppingLists()

    private val firebaseDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("shopping_lists")

    init {
        // Sync data from Firebase to Room on app start
        syncFromFirebase()
    }

    fun addShoppingList(shoppingList: ShoppingList) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val id = shoppingListDao.insert(shoppingList)
                shoppingList.id = id.toInt()
                firebaseDatabase.child(id.toString()).setValue(shoppingList)
                    .addOnSuccessListener {
                        Log.d("Firebase", "Data added successfully: $shoppingList")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firebase", "Failed to add data", e)
                    }
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

    private fun syncFromFirebase() {
        firebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val shoppingLists = mutableListOf<ShoppingList>()
                        for (data in snapshot.children) {
                            val shoppingList = data.getValue(ShoppingList::class.java)
                            if (shoppingList != null) {
                                shoppingLists.add(shoppingList)
                            }
                        }
                        shoppingListDao.deleteAll()
                        shoppingListDao.insertAll(shoppingLists)
                    } catch (e: Exception) {
                        Log.e("ShoppingListViewModel", "Error syncing from Firebase", e)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ShoppingListViewModel", "Firebase sync cancelled", error.toException())
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