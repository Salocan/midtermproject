package com.example.midtermapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.midtermapp.R
import com.example.midtermapp.data.ShoppingList
import com.example.midtermapp.viewmodel.ShoppingListViewModel
import android.app.AlertDialog
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity : AppCompatActivity() {

    private lateinit var shoppingListViewModel: ShoppingListViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ShoppingListAdapter
    private lateinit var etListName: EditText
    private lateinit var btnAdd: Button
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        recyclerView = findViewById(R.id.rvShoppingLists)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ShoppingListAdapter(
            onItemClick = { shoppingList ->
                val intent = Intent(this, ShoppingListDetailActivity::class.java)
                intent.putExtra("listId", shoppingList.id)
                intent.putExtra("listName", shoppingList.name)
                startActivity(intent)
            },
            onDeleteClick = { shoppingListItem ->
                deleteItem(shoppingListItem)
            },
            onEditClick = { shoppingListItem ->
                showRenameDialog(shoppingListItem)
            }
        )
        recyclerView.adapter = adapter

        shoppingListViewModel = ViewModelProvider(this).get(ShoppingListViewModel::class.java)

        shoppingListViewModel.allShoppingLists.observe(this) { shoppingLists ->
            shoppingLists?.let {
                adapter.submitList(it)
            }
        }

        etListName = findViewById(R.id.etListName)
        btnAdd = findViewById(R.id.btnAdd)

        btnAdd.setOnClickListener {
            val listName = etListName.text.toString()
            if (listName.isNotEmpty()) {
                val shoppingList = ShoppingList(name = listName)
                shoppingListViewModel.addShoppingList(shoppingList)
                etListName.text.clear()

                // Log an event to Firebase Analytics
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, listName)
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
            }
        }

        // Check Firebase connection
        shoppingListViewModel.checkFirebaseConnection()
    }

    private fun deleteItem(shoppingListItem: ShoppingList) {
        shoppingListViewModel.deleteShoppingList(shoppingListItem)
    }

    private fun showRenameDialog(shoppingListItem: ShoppingList) {
        val dialogView = layoutInflater.inflate(R.layout.rename, null)
        val etNewName = dialogView.findViewById<EditText>(R.id.etNewName)
        etNewName.setText(shoppingListItem.name) // Set the current name in the EditText

        AlertDialog.Builder(this)
            .setTitle("Rename List")
            .setView(dialogView)
            .setPositiveButton("Rename") { _, _ ->
                val newName = etNewName.text.toString()
                if (newName.isNotEmpty()) {
                    shoppingListItem.name = newName
                    shoppingListViewModel.updateShoppingList(shoppingListItem)

                    // Log an event to Firebase Analytics
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, newName)
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}