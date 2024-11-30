package com.example.androidmidterm

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import viewmodel.ProfileViewModel
import com.example.androidmidterm.ProfileAdapter

class ProfileActivity : AppCompatActivity() {
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val saveButton: Button = findViewById(R.id.saveButton)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ProfileAdapter()
        recyclerView.adapter = adapter

        // Load saved profile data
        profileViewModel.loadProfile { profile ->
            profile?.let { profileData ->
                adapter.updateProfile(profileData.fullName, profileData.dateOfBirth)
            }
        }

        saveButton.setOnClickListener {
            val fullName = adapter.getFullName()
            val dateOfBirth = adapter.getDateOfBirth()
            profileViewModel.saveProfile(fullName, dateOfBirth) { success ->
                if (success) {
                    Toast.makeText(this, "Save successful", Toast.LENGTH_SHORT).show()
                    adapter.updateProfile(fullName, dateOfBirth)
                } else {
                    Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}