package com.example.jama_fv.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jama_fv.data.remote.FirebaseDataSource
import com.example.jama_fv.data.remote.PlaceRepository
import com.example.jama_fv.data.remote.ReviewRepository
import com.example.jama_fv.data.remote.UserRepository
import com.example.jama_fv.ui.auth.LoginViewModel
import com.example.jama_fv.ui.auth.RegisterViewModel
import com.example.jama_fv.ui.client.HomeViewModel
import com.example.jama_fv.ui.owner.RegisterPlaceViewModel
import com.example.jama_fv.ui.map.MapPickerViewModel
import com.example.jama_fv.ui.client.PlaceDetailViewModel

/**
 * Factoría personalizada para crear instancias de ViewModel con dependencias.
 * Esto es necesario porque los ViewModels tienen parámetros en su constructor (repositorios).
 */
class ViewModelFactory(
    private val firebaseDataSource: FirebaseDataSource,
    private val userRepository: UserRepository,
    private val placeRepository: PlaceRepository,
    private val reviewRepository: ReviewRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // Si el ViewModel es LoginViewModel, lo creamos con UserRepository
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(userRepository) as T
            // Si el ViewModel es RegisterViewModel, lo creamos con UserRepository
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel(userRepository) as T
            // Si el ViewModel es HomeViewModel, lo creamos con PlaceRepository
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(placeRepository) as T
            // Si el ViewModel es RegisterPlaceViewModel, lo creamos con PlaceRepository
            modelClass.isAssignableFrom(RegisterPlaceViewModel::class.java) -> RegisterPlaceViewModel(placeRepository) as T
            // Si el ViewModel es MapPickerViewModel, no tiene dependencias de repositorio
            modelClass.isAssignableFrom(MapPickerViewModel::class.java) -> MapPickerViewModel() as T
            // Si el ViewModel es PlaceDetailViewModel, lo creamos con PlaceRepository y ReviewRepository
            modelClass.isAssignableFrom(PlaceDetailViewModel::class.java) -> PlaceDetailViewModel(placeRepository, reviewRepository) as T
            // Si la clase del ViewModel no es reconocida, lanzamos una excepción
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}