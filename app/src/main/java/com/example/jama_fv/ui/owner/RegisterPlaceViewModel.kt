// RegisterPlaceViewModel.kt
package com.example.jama_fv.ui.owner // Asegúrate que el paquete coincida

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.GeoPoint
import com.example.jama_fv.data.model.Negocio
import com.example.jama_fv.data.remote.PlaceRepository
import com.example.jama_fv.data.remote.Result // Importa la clase Result
import kotlinx.coroutines.launch

class RegisterPlaceViewModel(private val placeRepository: PlaceRepository) : ViewModel() {

    // LiveData para observar el estado del registro/actualización de un negocio
    private val _placeOperationStatus = MutableLiveData<Result<Negocio>>()
    val placeOperationStatus: LiveData<Result<Negocio>> = _placeOperationStatus

    // LiveData para la ubicación seleccionada en el mapa
    private val _selectedLocation = MutableLiveData<GeoPoint?>()
    val selectedLocation: LiveData<GeoPoint?> = _selectedLocation

    /**
     * Establece la ubicación seleccionada para el negocio.
     * @param lat La latitud seleccionada.
     * @param lng La longitud seleccionada.
     */
    fun setSelectedLocation(lat: Double, lng: Double) {
        _selectedLocation.value = GeoPoint(lat, lng)
    }

    /**
     * Registra un nuevo negocio en la base de datos.
     * @param negocio El objeto Negocio a registrar.
     */
    fun registerNewPlace(negocio: Negocio) {
        _placeOperationStatus.value = Result.Loading // Indica que la operación está en curso

        viewModelScope.launch {
            val result = placeRepository.addNegocio(negocio)
            _placeOperationStatus.value = result
        }
    }

    /**
     * Actualiza un negocio existente en la base de datos.
     * @param negocio El objeto Negocio a actualizar.
     */
    fun updateExistingPlace(negocio: Negocio) {
        _placeOperationStatus.value = Result.Loading

        viewModelScope.launch {
            val result = placeRepository.updateNegocio(negocio)
            _placeOperationStatus.value = result
        }
    }

    // Puedes añadir funciones para validar campos aquí antes de llamar al repositorio
    fun validatePlaceData(negocio: Negocio): Boolean {
        // Implementa tu lógica de validación aquí
        return negocio.nombre.isNotBlank() && negocio.descripcion != null // Ejemplo básico
    }

    // Funciones para manejar la subida de imágenes si las implementas
    // fun uploadImageForPlace(uri: Uri) { ... }
}