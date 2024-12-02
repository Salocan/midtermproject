package com.example.androidmidterm


import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidmidterm.databinding.ItemProfileBinding

class ProfileAdapter : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {
    private var fullName: String = ""
    private var dateOfBirth: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val binding = ItemProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProfileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = 1

    fun getFullName(): String = fullName

    fun getDateOfBirth(): String = dateOfBirth


    fun updateProfile(fullName: String, dateOfBirth: String) {
        this.fullName = fullName
        this.dateOfBirth = dateOfBirth
        notifyDataSetChanged()
    }

    inner class ProfileViewHolder(private val binding: ItemProfileBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.fullNameEditText.setText(fullName)
            binding.dateOfBirthEditText.setText(dateOfBirth)

            binding.fullNameEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    fullName = s.toString()
                }
                override fun afterTextChanged(s: Editable?) {}
            })
            binding.dateOfBirthEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    dateOfBirth = s.toString()
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }
}