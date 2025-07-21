// UserRepository.kt
package com.example.jama_fv.data.remote

import com.example.jama_fv.data.model.User

class UserRepository(private val firebaseDataSource: FirebaseDataSource) {

    suspend fun registerUser(email: String, password: String, name: String, role: String): Result<User> {
        return firebaseDataSource.registerUser(email, password, name, role)
    }

    suspend fun loginUser(email: String, password: String): Result<User> {
        return firebaseDataSource.loginUser(email, password)
    }

    suspend fun getCurrentUser(): Result<User> {
        return firebaseDataSource.getCurrentUser()
    }

    fun logout() {
        firebaseDataSource.logout()
    }

    // Otros métodos relacionados con el perfil de usuario, como actualizar información
}