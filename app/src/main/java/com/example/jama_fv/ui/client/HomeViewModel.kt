// HomeViewModel.kt
package com.example.jama_fv.ui.client // Asegúrate que el paquete coincida

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jama_fv.data.model.Negocio
import com.example.jama_fv.data.remote.PlaceRepository
import com.example.jama_fv.data.remote.Result // Importa la clase Result
import kotlinx.coroutines.launch

class HomeViewModel(private val placeRepository: PlaceRepository) : ViewModel() {

    // LiveData para la lista de negocios a mostrar en el mapa/lista
    private val _negocios = MutableLiveData<Result<List<Negocio>>>()
    val negocios: LiveData<Result<List<Negocio>>> = _negocios

    // LiveData para la ubicación actual del usuario (si se implementa la lógica de ubicación aquí)
    private val _currentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location> = _currentLocation

    // LiveData para el estado de carga del mapa o la UI
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        // Al inicializar el ViewModel, intenta cargar los negocios
        loadNegocios()
    }

    /**
     * Carga todos los negocios disponibles desde el repositorio.
     */
    fun loadNegocios() {
        _negocios.value = Result.Loading // Indica que la carga de negocios está en curso
        _isLoading.value = true

        viewModelScope.launch {
            val result = placeRepository.getNegocios()
            _negocios.value = result
            _isLoading.value = false
        }
    }

    /**
     * Actualiza la ubicación actual del usuario.
     * Esta función sería llamada desde la Activity/Fragment cuando se obtiene una nueva ubicación.
     * @param location La ubicación actual del dispositivo.
     */
    fun updateCurrentLocation(location: Location) {
        _currentLocation.value = location
    }

    // Puedes añadir más funciones aquí para filtrar negocios, buscar, etc.
}