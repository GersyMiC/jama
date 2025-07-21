// ReviewRepository.kt
package com.example.jama_fv.data.remote

import com.example.jama_fv.data.model.Review

class ReviewRepository(private val firebaseDataSource: FirebaseDataSource) {

    suspend fun addReview(review: Review): Result<Review> {
        return firebaseDataSource.addReview(review)
    }

    suspend fun getReviewsForBusiness(businessId: String): Result<List<Review>> {
        return firebaseDataSource.getReviewsForBusiness(businessId)
    }

    // Puedes añadir métodos para actualizar o eliminar reseñas si es necesario
}