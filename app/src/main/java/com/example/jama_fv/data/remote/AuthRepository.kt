// app/src/main/java/com/example/jama_fv/data/remote/AuthRepository.kt
package com.example.jama_fv.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.example.jama_fv.data.model.User
import com.example.jama_fv.data.remote.FirebaseDataSource // Importa FirebaseDataSource si tu constructor la usa

class AuthRepository(private val firebaseDataSource: FirebaseDataSource) { // Mantén esto si AuthRepository depende de FirebaseDataSource
    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser
        return if (firebaseUser != null) {
            User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                name = firebaseUser.displayName ?: "Usuario",
                role = "" // O el rol real si lo cargas de Firestore
            )
        } else {
            null
        }
    }
}