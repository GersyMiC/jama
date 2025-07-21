// MyPlacesViewModelFactory.kt
package com.example.jama_fv.ui.owner // O el paquete donde tengas tus ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jama_fv.data.remote.AuthRepository
import com.example.jama_fv.data.remote.FirebaseDataSource // ¡Asegúrate de que esta importación sea correcta!
import com.example.jama_fv.data.remote.PlaceRepository
import com.example.jama_fv.data.remote.ReviewRepository // ¡Asegúrate de que esta importación sea correcta!

class MyPlacesViewModelFactory(
    // Los parámetros de la Factory deben coincidir con los que pasas al ViewModel
    private val firebaseDataSource: FirebaseDataSource,
    private val authRepository: AuthRepository,
    private val placeRepository: PlaceRepository,
    private val reviewRepository: ReviewRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPlacesViewModel::class.java)) {
            // ¡Aquí es donde pasas los parámetros al constructor de MyPlacesViewModel!
            // El orden y los tipos deben coincidir exactamente con el constructor del ViewModel.
            return MyPlacesViewModel(
                firebaseDataSource, // Pasa firebaseDataSource
                authRepository,     // Pasa authRepository
                placeRepository,    // Pasa placeRepository
                reviewRepository    // Pasa reviewRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}