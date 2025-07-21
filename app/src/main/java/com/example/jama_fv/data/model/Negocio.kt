// app/src/main/java/com/example/jama_fv/data/model/Negocio.kt

package com.example.jama_fv.data.model


import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import java.io.Serializable // <-- ¡Importa esto!


data class Negocio(
    val id: String = "",
    val userId: String = "",
    val nombre: String = "",
    val tipo: String = "",
    val descripcion: String = "",
    val horario: String = "",
    val precios: String = "",
    val rating: Double = 0.0,
    val imageUrl: String? = null,
    val ownerUid: String = "",
    val latitud: Double? = null, // Alternativa para GeoPoint
    val longitud: Double? = null, // Alternativa para GeoPoint
    val createdAtMillis: Long = System.currentTimeMillis() //
): Serializable

