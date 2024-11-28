package com.example.midtermapp.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.midtermapp.R
import com.example.midtermapp.data.ShoppingListItem
import com.example.midtermapp.viewmodel.ShoppingListViewModel
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText

class ShoppingListDetailActivity : AppCompatActivity() {

    private lateinit var shoppingListViewModel: ShoppingListViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ShoppingListItemAdapter
    private lateinit var btnAddItem: Button
    private lateinit var btnBack: ImageButton
    private lateinit var tvListName: TextView
    private lateinit var tvProgress: TextView
    private lateinit var searchView: SearchView
    private lateinit var spinnerFilter: Spinner
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var listId: Int = 0
    private var allItems: List<ShoppingListItem> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list_detail)

        listId = intent.getIntExtra("listId", 0)
        val listName = intent.getStringExtra("listName")

        tvListName = findViewById(R.id.tvListName)
        tvListName.text = listName

        tvProgress = findViewById(R.id.tvProgress)

        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        searchView = findViewById(R.id.searchView)
        spinnerFilter = findViewById(R.id.spinnerFilter)

        recyclerView = findViewById(R.id.rvShoppingListItems)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ShoppingListItemAdapter(
            onItemClick = { _ -> },
            onDeleteClick = { item -> deleteShoppingListItem(item) },
            onEditClick = { item -> showItemDialog(item) },
            onPurchasedChange = { item -> shoppingListViewModel.updateShoppingListItem(item) },
            onProgressUpdate = { updateProgress() }
        )
        recyclerView.adapter = adapter

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            shoppingListViewModel.syncFromFirebase()
            shoppingListViewModel.getItemsForList(listId).observe(this) { items ->
                items?.let {
                    allItems = it
                    filterItems()
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }

        shoppingListViewModel = ViewModelProvider(this).get(ShoppingListViewModel::class.java)

        shoppingListViewModel.getItemsForList(listId).observe(this) { items ->
            items?.let {
                allItems = it
                filterItems()
            }
        }

        btnAddItem = findViewById(R.id.btnAddItem)
        btnAddItem.setOnClickListener {
            showItemDialog(null)
        }

        setupSearchView()
        setupSpinnerFilter()
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterItems()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterItems()
                return true
            }
        })
    }

    private fun setupSpinnerFilter() {
        val categories = arrayOf("All", "Fruits", "Vegetables", "Dairy", "Meat", "Bakery", "Beverages", "Snacks", "Household", "Personal Care", "Other")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = spinnerAdapter

        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                filterItems()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun filterItems() {
        val query = searchView.query.toString().lowercase()
        val selectedCategory = spinnerFilter.selectedItem.toString()

        val filteredItems = allItems.filter { item ->
            val matchesQuery = item.name.lowercase().contains(query)
            val matchesCategory = selectedCategory == "All" || item.category == selectedCategory
            matchesQuery && matchesCategory
        }

        adapter.submitList(filteredItems) {
            updateProgress()
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
            tvItemQuantity.text = getString(R.string.item_quantity, item.quantity)
        }

        btnIncreaseQuantity.setOnClickListener {
            itemQuantity++
            tvItemQuantity.text = getString(R.string.item_quantity, itemQuantity)
        }

        btnDecreaseQuantity.setOnClickListener {
            if (itemQuantity > 1) {
                itemQuantity--
                tvItemQuantity.text = getString(R.string.item_quantity, itemQuantity)
            }
        }

        AlertDialog.Builder(this)
            .setTitle(if (item == null) "Add Item" else "Edit Item")
            .setView(dialogView)
            .setPositiveButton(if (item == null) "Add" else "Save") { _, _ ->
                val itemName = etItemName.text.toString()
                val itemCategory = spinnerItemCategory.selectedItem.toString()
                if (itemName.isNotEmpty()) {
                    val newItem = item?.copy(
                        name = itemName,
                        category = itemCategory,
                        quantity = itemQuantity
                    ) ?: ShoppingListItem(
                        listId = listId,
                        name = itemName,
                        category = itemCategory,
                        quantity = itemQuantity
                    )
                    if (item == null) {
                        shoppingListViewModel.addShoppingListItem(newItem)
                    } else {
                        shoppingListViewModel.updateShoppingListItem(newItem)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteShoppingListItem(item: ShoppingListItem) {
        shoppingListViewModel.deleteShoppingListItem(item)
        updateProgress()
    }

    private fun updateProgress() {
        val items = adapter.currentList
        val purchasedCount = items.count { it.purchased }
        val totalCount = items.size
        tvProgress.text = getString(R.string.items_purchased, purchasedCount, totalCount)
    }
}