package model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class Profile(
    @PrimaryKey val uid: String,
    val fullName: String,
    val dateOfBirth: String
)