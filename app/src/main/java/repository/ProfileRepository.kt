package repository

import android.content.Context
import model.Profile

class ProfileRepository(context: Context) {
    private val profileDao = AppDatabase.getDatabase(context).profileDao()

    suspend fun saveProfile(profile: Profile) {
        profileDao.insert(profile)
    }

    suspend fun getProfile(uid: String): Profile? {
        return profileDao.getProfile(uid)
    }
}