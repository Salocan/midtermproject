package repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import model.Profile

@Dao
interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: Profile)

    @Query("SELECT * FROM profile WHERE uid = :uid")
    suspend fun getProfile(uid: String): Profile?
}