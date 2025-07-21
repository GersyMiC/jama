// RegisterViewModel.kt
package com.example.jama_fv.ui.auth // Asegúrate que el paquete coincida

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jama_fv.data.model.User
import com.example.jama_fv.data.remote.UserRepository
import com.example.jama_fv.data.remote.Result // Importa la clase Result
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    // LiveData para observar el estado del registro
    private val _registerStatus = MutableLiveData<Result<User>>()
    val registerStatus: LiveData<Result<User>> = _registerStatus

    /**
     * Registra un nuevo usuario con email, contraseña, nombre y rol.
     * @param email El email del nuevo usuario.
     * @param password La contraseña del nuevo usuario.
     * @param name El nombre del nuevo usuario.
     * @param role El rol del usuario ("client" o "owner").
     */
    fun registerUser(email: String, password: String, name: String, role: String) {
        _registerStatus.value = Result.Loading // Indica que la operación está en curso

        viewModelScope.launch {
            val result = userRepository.registerUser(email, password, name, role)
            _registerStatus.value = result // Actualiza el estado con el resultado
        }
    }
}