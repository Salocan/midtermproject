package viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import model.Profile
import repository.ProfileRepository

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val profileRepository = ProfileRepository(application)

    fun saveProfile(fullName: String, dateOfBirth: String, onResult: (Boolean) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val profile = Profile(uid, fullName, dateOfBirth)
        viewModelScope.launch {
            try {
                profileRepository.saveProfile(profile)
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun loadProfile(onResult: (Profile?) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            val profile = profileRepository.getProfile(uid)
            onResult(profile)
        }
    }
}