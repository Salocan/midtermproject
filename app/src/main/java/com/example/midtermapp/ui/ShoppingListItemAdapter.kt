package com.example.midtermapp.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.midtermapp.R
import com.example.midtermapp.data.ShoppingListItem

class ShoppingListItemAdapter(
    private val onItemClick: (ShoppingListItem) -> Unit,
    private val onDeleteClick: (ShoppingListItem) -> Unit,
    private val onEditClick: (ShoppingListItem) -> Unit,
    private val onPurchasedChange: (ShoppingListItem) -> Unit,
    private val onProgressUpdate: () -> Unit
) : ListAdapter<ShoppingListItem, ShoppingListItemAdapter.ShoppingListItemViewHolder>(ShoppingListItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shopping_list_item, parent, false)
        return ShoppingListItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShoppingListItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    inner class ShoppingListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.itemName)
        private val categoryTextView: TextView = itemView.findViewById(R.id.itemCategory)
        private val quantityTextView: TextView = itemView.findViewById(R.id.itemQuantity)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
        private val editButton: ImageButton = itemView.findViewById(R.id.edit_button)
        private val purchasedCheckBox: CheckBox = itemView.findViewById(R.id.purchasedCheckBox)

        fun bind(item: ShoppingListItem) {
            nameTextView.text = item.name
            categoryTextView.text = item.category
            quantityTextView.text = "x${item.quantity}"

            purchasedCheckBox.setOnCheckedChangeListener(null)
            purchasedCheckBox.isChecked = item.purchased
            purchasedCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (item.purchased != isChecked) {
                    item.purchased = isChecked
                    onPurchasedChange(item)
                    onProgressUpdate()
                }
            }

            if (item.purchased) {
                nameTextView.paintFlags = nameTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                nameTextView.paintFlags = nameTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            itemView.setOnClickListener {
                onItemClick(item)
            }

            deleteButton.setOnClickListener {
                onDeleteClick(item)
            }

            editButton.setOnClickListener {
                onEditClick(item)
            }
        }
    }

    private class ShoppingListItemDiffCallback : DiffUtil.ItemCallback<ShoppingListItem>() {
        override fun areItemsTheSame(oldItem: ShoppingListItem, newItem: ShoppingListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShoppingListItem, newItem: ShoppingListItem): Boolean {
            return oldItem == newItem
        }
    }
}