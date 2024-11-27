package com.example.midtermapp.ui

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Button
import android.widget.TextView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.midtermapp.R
import com.example.midtermapp.data.ShoppingListItem
import com.example.midtermapp.viewmodel.ShoppingListViewModel
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText

class ShoppingListDetailActivity : AppCompatActivity() {

    private lateinit var shoppingListViewModel: ShoppingListViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ShoppingListItemAdapter
    private lateinit var btnAddItem: Button
    private lateinit var btnBack: ImageButton
    private lateinit var tvListName: TextView
    private var listId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list_detail)

        listId = intent.getIntExtra("listId", 0)
        val listName = intent.getStringExtra("listName")

        tvListName = findViewById(R.id.tvListName)
        tvListName.text = listName

        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.rvShoppingListItems)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ShoppingListItemAdapter(
            onItemClick = { item ->
                showItemDialog(item)
            },
            onDeleteClick = { item ->
                deleteShoppingListItem(item)
            },
            onEditClick = { item ->
                showItemDialog(item)
            }
        )
        recyclerView.adapter = adapter

        shoppingListViewModel = ViewModelProvider(this).get(ShoppingListViewModel::class.java)

        shoppingListViewModel.getItemsForList(listId).observe(this) { items ->
            items?.let {
                adapter.submitList(it)
                adapter.notifyDataSetChanged() // Notify the adapter of data changes
            }
        }

        btnAddItem = findViewById(R.id.btnAddItem)
        btnAddItem.setOnClickListener {
            showItemDialog(null)
        }
    }

    private fun showItemDialog(item: ShoppingListItem?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_item, null)
        val etItemName = dialogView.findViewById<EditText>(R.id.etDialogItemName)
        val spinnerItemCategory = dialogView.findViewById<Spinner>(R.id.spinnerDialogItemCategory)
        val tvItemQuantity = dialogView.findViewById<TextView>(R.id.tvDialogItemQuantity)
        val btnIncreaseQuantity = dialogView.findViewById<Button>(R.id.btnDialogIncreaseQuantity)
        val btnDecreaseQuantity = dialogView.findViewById<Button>(R.id.btnDialogDecreaseQuantity)

        var itemQuantity = item?.quantity ?: 1

        // Set up the category spinner
        val categories = arrayOf("Fruits", "Vegetables", "Dairy", "Meat", "Bakery", "Beverages", "Snacks", "Household", "Personal Care", "Other")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerItemCategory.adapter = spinnerAdapter

        if (item != null) {
            etItemName.setText(item.name)
            spinnerItemCategory.setSelection(categories.indexOf(item.category))
            tvItemQuantity.text = "x${item.quantity}"
        }

        btnIncreaseQuantity.setOnClickListener {
            itemQuantity++
            tvItemQuantity.text = "x$itemQuantity"
        }

        btnDecreaseQuantity.setOnClickListener {
            if (itemQuantity > 1) {
                itemQuantity--
                tvItemQuantity.text = "x$itemQuantity"
            }
        }

        AlertDialog.Builder(this)
            .setTitle(if (item == null) "Add Item" else "Edit Item")
            .setView(dialogView)
            .setPositiveButton(if (item == null) "Add" else "Save") { _, _ ->
                val itemName = etItemName.text.toString()
                val itemCategory = spinnerItemCategory.selectedItem.toString()
                if (itemName.isNotEmpty() && itemCategory.isNotEmpty()) {
                    if (item == null) {
                        val newItem = ShoppingListItem(
                            name = itemName,
                            category = itemCategory,
                            quantity = itemQuantity,
                            listId = listId
                        )
                        shoppingListViewModel.addShoppingListItem(newItem)
                    } else {
                        item.name = itemName
                        item.category = itemCategory
                        item.quantity = itemQuantity
                        shoppingListViewModel.updateShoppingListItem(item)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteShoppingListItem(item: ShoppingListItem) {
        shoppingListViewModel.deleteShoppingListItem(item)
    }
}