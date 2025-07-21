// LoginViewModel.kt
package com.example.jama_fv.ui.auth // Asegúrate que el paquete coincida

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jama_fv.data.model.User
import com.example.jama_fv.data.remote.UserRepository
import com.example.jama_fv.data.remote.Result // Importa la clase Result
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    // LiveData para observar el estado del inicio de sesión
    private val _loginStatus = MutableLiveData<Result<User>>()
    val loginStatus: LiveData<Result<User>> = _loginStatus

    /**
     * Inicia sesión de un usuario con email y contraseña.
     * @param email El email del usuario.
     * @param password La contraseña del usuario.
     */
    fun loginUser(email: String, password: String) {
        _loginStatus.value = Result.Loading // Indica que la operación está en curso

        viewModelScope.launch {
            val result = userRepository.loginUser(email, password)
            _loginStatus.value = result // Actualiza el estado con el resultado de la operación
        }
    }
}
