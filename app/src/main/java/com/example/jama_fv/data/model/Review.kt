package com.example.jama_fv.data.model

import com.google.firebase.Timestamp

data class Review(
    var businessId: String = "", // ID del negocio al que pertenece la reseña
    var userId: String = "",     // UID del usuario que hizo la reseña
    var userName: String = "",
    var rating: Double = 0.0,
    var comment: String = "",
    var createdAt: Timestamp? = null
) {
    constructor() : this("", "", "", 0.0, "", null)
}