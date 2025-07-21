// MapPickerViewModel.kt
package com.example.jama_fv.ui.map // Asegúrate que el paquete coincida

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint

class MapPickerViewModel : ViewModel() {

    // LiveData para la ubicación que el usuario ha seleccionado en el mapa
    private val _selectedLatLng = MutableLiveData<LatLng?>()
    val selectedLatLng: LiveData<LatLng?> = _selectedLatLng

    // LiveData para la ubicación actual del dispositivo (si se usa para centrar el mapa inicialmente)
    private val _currentDeviceLocation = MutableLiveData<Location?>()
    val currentDeviceLocation: LiveData<Location?> = _currentDeviceLocation

    /**
     * Actualiza la ubicación seleccionada por el usuario en el mapa.
     * @param latLng Las coordenadas LatLng seleccionadas.
     */
    fun updateSelectedLocation(latLng: LatLng) {
        _selectedLatLng.value = latLng
    }

    /**
     * Actualiza la ubicación actual del dispositivo.
     * @param location La ubicación actual del dispositivo.
     */
    fun updateCurrentDeviceLocation(location: Location) {
        _currentDeviceLocation.value = location
    }

    /**
     * Devuelve la ubicación seleccionada como un GeoPoint.
     * @return GeoPoint con la latitud y longitud seleccionadas, o null si no hay ninguna.
     */
    fun getSelectedGeoPoint(): GeoPoint? {
        return selectedLatLng.value?.let { GeoPoint(it.latitude, it.longitude) }
    }
}