// PresetAdapter.kt
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

class PresetAdapter(
    private val onItemClick: (ShoppingList) -> Unit,
    private val onDeleteClick: (ShoppingList) -> Unit,
    private val onEditClick: (ShoppingList) -> Unit,
    private val onImportClick: (ShoppingList) -> Unit
) : ListAdapter<ShoppingList, PresetAdapter.PresetViewHolder>(PresetDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_preset, parent, false)
        return PresetViewHolder(view)
    }

    override fun onBindViewHolder(holder: PresetViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    inner class PresetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.presetName)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
        private val editButton: ImageButton = itemView.findViewById(R.id.edit_button)
        private val importButton: ImageButton = itemView.findViewById(R.id.import_button)

        fun bind(preset: ShoppingList) {
            nameTextView.text = preset.name

            itemView.setOnClickListener {
                onItemClick(preset)
            }

            deleteButton.setOnClickListener {
                onDeleteClick(preset)
            }

            editButton.setOnClickListener {
                onEditClick(preset)
            }

            importButton.setOnClickListener {
                onImportClick(preset)
            }
        }
    }

    private class PresetDiffCallback : DiffUtil.ItemCallback<ShoppingList>() {
        override fun areItemsTheSame(oldItem: ShoppingList, newItem: ShoppingList): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShoppingList, newItem: ShoppingList): Boolean {
            return oldItem == newItem
        }
    }
}