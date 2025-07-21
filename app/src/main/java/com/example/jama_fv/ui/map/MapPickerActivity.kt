package com.example.jama_fv.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.example.jama_fv.R
import com.example.jama_fv.data.remote.FirebaseDataSource
import com.example.jama_fv.data.remote.PlaceRepository
import com.example.jama_fv.data.remote.ReviewRepository
import com.example.jama_fv.data.remote.UserRepository
import com.example.jama_fv.utils.ViewModelFactory

class MapPickerActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var viewModel: MapPickerViewModel
    private var selectedMarker: Marker? = null
    private lateinit var selectedLocationTextView: TextView
    private lateinit var confirmLocationButton: Button

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Launcher para permisos de ubicación
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Permiso de ubicación precisa concedido
                getLastLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Permiso de ubicación aproximada concedido
                getLastLocation()
            }
            else -> {
                Toast.makeText(this, "Permisos de ubicación denegados. No se puede obtener la ubicación actual.", Toast.LENGTH_LONG).show()
                // Centrar el mapa en una ubicación predeterminada (por ejemplo, Huancayo, Perú)
                val huancayo = LatLng(-12.0620, -75.2107)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(huancayo, 15f))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_picker)

        selectedLocationTextView = findViewById(R.id.tv_selected_location)
        confirmLocationButton = findViewById(R.id.btn_confirm_location)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Inicializar ViewModel
        val firebaseDataSource = FirebaseDataSource()
        val userRepository = UserRepository(firebaseDataSource)
        val placeRepository = PlaceRepository(firebaseDataSource)
        val reviewRepository = ReviewRepository(firebaseDataSource)
        val factory = ViewModelFactory(firebaseDataSource, userRepository, placeRepository, reviewRepository)
        viewModel = ViewModelProvider(this, factory)[MapPickerViewModel::class.java]

        // Configurar el mapa
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Observar la ubicación seleccionada del ViewModel
        viewModel.selectedLatLng.observe(this) { latLng ->
            if (latLng != null) {
                selectedLocationTextView.text = "Ubicación seleccionada: Lat: ${"%.4f".format(latLng.latitude)}, Lon: ${"%.4f".format(latLng.longitude)}"
                // Mover el marcador
                selectedMarker?.remove()
                selectedMarker = map.addMarker(MarkerOptions().position(latLng).title("Ubicación Seleccionada"))
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            } else {
                selectedLocationTextView.text = "Ubicación seleccionada: Lat: --, Lon: --"
            }
        }

        confirmLocationButton.setOnClickListener {
            viewModel.selectedLatLng.value?.let { latLng ->
                val resultIntent = Intent().apply {
                    putExtra("latitude", latLng.latitude)
                    putExtra("longitude", latLng.longitude)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } ?: run {
                Toast.makeText(this, "Por favor, selecciona una ubicación en el mapa.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Habilitar controles de zoom
        map.uiSettings.isZoomControlsEnabled = true

        // Habilitar la capa de "Mi Ubicación" si se tienen permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
            getLastLocation()
        } else {
            // Solicitar permisos de ubicación
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        // Listener para clics en el mapa
        map.setOnMapClickListener { latLng ->
            viewModel.updateSelectedLocation(latLng)
        }

        // Recuperar ubicación inicial si se pasó en el Intent
        val initialLat = intent.getDoubleExtra("initial_latitude", 0.0)
        val initialLon = intent.getDoubleExtra("initial_longitude", 0.0)
        if (initialLat != 0.0 || initialLon != 0.0) {
            val initialLatLng = LatLng(initialLat, initialLon)
            viewModel.updateSelectedLocation(initialLatLng)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, 15f))
        }
    }

    @SuppressLint("MissingPermission") // La verificación de permisos se hace con requestPermissionLauncher
    private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val latLng = LatLng(it.latitude, it.longitude)
                        viewModel.updateCurrentDeviceLocation(it)
                        // Si no hay una ubicación inicial pasada, centrar el mapa en la ubicación del dispositivo
                        if (intent.getDoubleExtra("initial_latitude", 0.0) == 0.0 &&
                            intent.getDoubleExtra("initial_longitude", 0.0) == 0.0) {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                            // Opcional: poner un marcador por defecto en la ubicación actual al inicio
                            // viewModel.updateSelectedLocation(latLng)
                        }
                    } ?: run {
                        Toast.makeText(this, "No se pudo obtener la última ubicación. Mueve el mapa manualmente.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al obtener la ubicación: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}