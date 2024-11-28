// PresetsActivity.kt
package com.example.midtermapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.midtermapp.R
import com.example.midtermapp.data.ShoppingList
import com.example.midtermapp.viewmodel.ShoppingListViewModel
import android.widget.Button
import android.widget.EditText
import android.app.AlertDialog
import android.view.LayoutInflater

class PresetsActivity : AppCompatActivity() {

    private lateinit var shoppingListViewModel: ShoppingListViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PresetAdapter
    private lateinit var btnAddPreset: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presets)

        shoppingListViewModel = ViewModelProvider(this).get(ShoppingListViewModel::class.java)

        recyclerView = findViewById(R.id.rvPresets)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = PresetAdapter(
            onItemClick = { preset -> openPresetDetail(preset) },
            onDeleteClick = { preset -> deletePreset(preset) },
            onEditClick = { preset -> showRenameDialog(preset) },
            onImportClick = { preset -> importPreset(preset) }
        )
        recyclerView.adapter = adapter

        shoppingListViewModel.allPresets.observe(this) { presets ->
            presets?.let {
                adapter.submitList(it)
            }
        }

        btnAddPreset = findViewById(R.id.btnAddPreset)
        btnAddPreset.setOnClickListener {
            showAddPresetDialog()
        }
    }

    private fun showAddPresetDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_preset, null)
        val etPresetName = dialogView.findViewById<EditText>(R.id.etPresetName)

        AlertDialog.Builder(this)
            .setTitle("New Preset")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val presetName = etPresetName.text.toString()
                if (presetName.isNotEmpty()) {
                    val preset = ShoppingList(name = presetName, isPreset = true)
                    shoppingListViewModel.addPreset(preset)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deletePreset(preset: ShoppingList) {
        shoppingListViewModel.deletePreset(preset)
    }

    private fun showRenameDialog(preset: ShoppingList) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.rename, null)
        val etNewName = dialogView.findViewById<EditText>(R.id.etNewName)
        etNewName.setText(preset.name)

        AlertDialog.Builder(this)
            .setTitle("Rename Preset")
            .setView(dialogView)
            .setPositiveButton("Rename") { _, _ ->
                val newName = etNewName.text.toString()
                if (newName.isNotEmpty()) {
                    preset.name = newName
                    shoppingListViewModel.updatePreset(preset)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun importPreset(preset: ShoppingList) {
        shoppingListViewModel.importPreset(preset)
    }

    private fun openPresetDetail(preset: ShoppingList) {
        val intent = Intent(this, ShoppingListDetailActivity::class.java)
        intent.putExtra("listId", preset.id)
        intent.putExtra("listName", preset.name)
        startActivity(intent)
    }
}