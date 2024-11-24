package com.example.midtermapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.midtermapp.R
import com.example.midtermapp.data.ShoppingList

class ShoppingListAdapter(
    private val onItemClick: (ShoppingList) -> Unit,
    private val onDeleteClick: (ShoppingList) -> Unit,
    private val onEditClick: (ShoppingList) -> Unit
) : ListAdapter<ShoppingList, ShoppingListAdapter.ShoppingListViewHolder>(ShoppingListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shopping_list, parent, false)
        return ShoppingListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    inner class ShoppingListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.listName)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
        private val editButton: ImageButton = itemView.findViewById(R.id.edit_button)

        fun bind(shoppingList: ShoppingList) {
            nameTextView.text = shoppingList.name

            itemView.setOnClickListener {
                onItemClick(shoppingList)
            }

            deleteButton.setOnClickListener {
                onDeleteClick(shoppingList)
            }

            editButton.setOnClickListener {
                onEditClick(shoppingList)
            }
        }
    }

    private class ShoppingListDiffCallback : DiffUtil.ItemCallback<ShoppingList>() {
        override fun areItemsTheSame(oldItem: ShoppingList, newItem: ShoppingList): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShoppingList, newItem: ShoppingList): Boolean {
            return oldItem == newItem
        }
    }
}