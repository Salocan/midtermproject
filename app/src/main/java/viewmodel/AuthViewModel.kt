package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    fun register(email: String, password: String, onResult: (FirebaseUser?) -> Unit) {
        viewModelScope.launch {
            val user = authRepository.register(email, password)
            onResult(user)
        }
    }

    fun login(email: String, password: String, onResult: (FirebaseUser?) -> Unit) {
        viewModelScope.launch {
            val user = authRepository.login(email, password)
            onResult(user)
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return authRepository.getCurrentUser()
    }

    fun logout(onResult: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onResult()
        }
    }
}