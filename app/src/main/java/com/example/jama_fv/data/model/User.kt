// app/src/main/java/com.example/jama_fv/data/model/User.kt
package com.example.jama_fv.data.model
import com.google.firebase.Timestamp
import java.io.Serializable



data class User(
    val uid: String = "", // <-- Debe estar aquí
    val email: String? = null, // <-- Debe estar aquí
    val name: String? = null, // <-- Debe estar aquí
    val role: String = "", // <-- Debe estar aquí
    val createdAt: Timestamp = Timestamp.now()
): Serializable  {
    // Constructor sin argumentos para Firebase (Firestore necesita esto para deserializar)
    constructor() : this("", "", "", "")
}