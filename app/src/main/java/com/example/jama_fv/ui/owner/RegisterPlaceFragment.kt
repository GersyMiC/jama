package com.example.jama_fv.ui.owner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide // Necesitarás añadir la dependencia de Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import com.example.jama_fv.R
import com.example.jama_fv.data.model.Negocio
import com.example.jama_fv.data.remote.FirebaseDataSource
import com.example.jama_fv.data.remote.PlaceRepository
import com.example.jama_fv.data.remote.ReviewRepository
import com.example.jama_fv.data.remote.Result
import com.example.jama_fv.data.remote.UserRepository
import com.example.jama_fv.ui.map.MapPickerActivity
import com.example.jama_fv.utils.ViewModelFactory
import java.util.Date

class RegisterPlaceFragment : Fragment() {

    private lateinit var viewModel: RegisterPlaceViewModel

    // Vistas del layout
    private lateinit var placeImageView: ImageView
    private lateinit var selectImageButton: Button
    private lateinit var typeSpinner: Spinner
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var hourStartEditText: EditText
    private lateinit var hourEndEditText: EditText
    private lateinit var priceMinEditText: EditText
    private lateinit var priceMaxEditText: EditText
    private lateinit var locationTextView: TextView
    private lateinit var selectLocationButton: Button
    private lateinit var saveBusinessButton: Button

    // Para la imagen (simplificado, necesitarías Firebase Storage para subirla)
    private var selectedImageUri: String = "" // Simula la URL de la imagen

    // Para la ubicación
    private var selectedGeoPoint: GeoPoint? = null

    // Activity Result Launcher para MapPickerActivity
    private val mapPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val latitude = result.data?.getDoubleExtra("latitude", 0.0)
            val longitude = result.data?.getDoubleExtra("longitude", 0.0)
            if (latitude != null && longitude != null) {
                selectedGeoPoint = GeoPoint(latitude, longitude)
                locationTextView.text = "Ubicación: Lat ${"%.4f".format(latitude)}, Lon ${"%.4f".format(longitude)}"
                viewModel.setSelectedLocation(latitude, longitude) // Actualizar ViewModel
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register_place, container, false)

        // Inicializar vistas
        placeImageView = view.findViewById(R.id.iv_place_image)
        selectImageButton = view.findViewById(R.id.btn_select_image)
        typeSpinner = view.findViewById(R.id.spinner_tipo)
        nameEditText = view.findViewById(R.id.et_nombre)
        descriptionEditText = view.findViewById(R.id.et_descripcion)
        hourStartEditText = view.findViewById(R.id.editTextHoraInicio)
        hourEndEditText = view.findViewById(R.id.editTextHoraFin)
        priceMinEditText = view.findViewById(R.id.et_precio_min)
        priceMaxEditText = view.findViewById(R.id.et_precio_max)
        locationTextView = view.findViewById(R.id.tv_ubicacion_seleccionada)
        selectLocationButton = view.findViewById(R.id.btn_seleccionar_ubicacion)
        saveBusinessButton = view.findViewById(R.id.btn_guardar_negocio)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar ViewModel
        val firebaseDataSource = FirebaseDataSource()
        val userRepository = UserRepository(firebaseDataSource)
        val placeRepository = PlaceRepository(firebaseDataSource)
        val reviewRepository = ReviewRepository(firebaseDataSource)
        val factory = ViewModelFactory(firebaseDataSource, userRepository, placeRepository, reviewRepository)
        viewModel = ViewModelProvider(this, factory)[RegisterPlaceViewModel::class.java]


        // Configurar Spinner de tipos de negocio
        val businessTypes = arrayOf("Café", "Restaurante", "Puesto al paso", "Bar", "Otros")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, businessTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = adapter

        // Observar la ubicación seleccionada del ViewModel
        viewModel.selectedLocation.observe(viewLifecycleOwner) { geoPoint ->
            selectedGeoPoint = geoPoint
            if (geoPoint != null) {
                locationTextView.text = "Ubicación: Lat ${"%.4f".format(geoPoint.latitude)}, Lon ${"%.4f".format(geoPoint.longitude)}"
            } else {
                locationTextView.text = "Ubicación: No seleccionada"
            }
        }

        // Observar el estado de la operación de guardado/actualización del negocio
        viewModel.placeOperationStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    Toast.makeText(context, "Guardando negocio...", Toast.LENGTH_SHORT).show()
                    saveBusinessButton.isEnabled = false
                }
                is Result.Success -> {
                    Toast.makeText(context, "Negocio guardado exitosamente.", Toast.LENGTH_SHORT).show()
                    saveBusinessButton.isEnabled = true
                    // Opcional: Limpiar campos o navegar a otra pantalla
                    clearFields()
                }
                is Result.Error -> {
                    Toast.makeText(context, "Error al guardar negocio: ${result.exception.message}", Toast.LENGTH_LONG).show()
                    saveBusinessButton.isEnabled = true
                }
            }
        }

        // Listeners
        selectImageButton.setOnClickListener {
            // Aquí iría la lógica para seleccionar una imagen de la galería/cámara
            // Para simplificar, solo actualizamos la URL de una imagen de ejemplo
            selectedImageUri = "https://example.com/your_image.jpg" // Cambia por una URL real o sube a Firebase Storage
            Glide.with(this).load(R.drawable.ic_jama_logo).into(placeImageView) // Carga una imagen de ejemplo
            Toast.makeText(context, "Funcionalidad de selección de imagen pendiente. Usando placeholder.", Toast.LENGTH_SHORT).show()
        }

        selectLocationButton.setOnClickListener {
            val intent = Intent(context, MapPickerActivity::class.java)
            // Puedes pasar la ubicación actual si ya la tienes para centrar el mapa
            selectedGeoPoint?.let {
                intent.putExtra("initial_latitude", it.latitude)
                intent.putExtra("initial_longitude", it.longitude)
            }
            mapPickerLauncher.launch(intent)
        }

        saveBusinessButton.setOnClickListener {
            saveBusiness()
        }
    }

    private fun saveBusiness() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(context, "Debes iniciar sesión como dueño para registrar un negocio.", Toast.LENGTH_SHORT).show()
            return
        }

        val name = nameEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()
        val type = typeSpinner.selectedItem.toString()
        val horario = "${hourStartEditText.text.toString()} - ${hourEndEditText.text.toString()}"
        val precios = "S/${priceMinEditText.text.toString()} - S/${priceMaxEditText.text.toString()}"

        // Validaciones básicas
        if (name.isEmpty() || description.isEmpty() ||
            hourStartEditText.text.isEmpty() || hourEndEditText.text.isEmpty() ||
            priceMinEditText.text.isEmpty() || priceMaxEditText.text.isEmpty()) {
            Toast.makeText(context, "Por favor, completa todos los campos de texto.", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedGeoPoint == null) {
            Toast.makeText(context, "Por favor, selecciona una ubicación en el mapa.", Toast.LENGTH_SHORT).show()
            return
        }

        val negocio = Negocio(
            id = "", // Firebase generará el ID
            userId = currentUser.uid,
            nombre = name,
            tipo = type,
            descripcion = description,
            horario = horario,
            precios = precios,
            latitud = selectedGeoPoint?.latitude, // <-- Pasa la latitud
            longitud = selectedGeoPoint?.longitude,
            imageUrl = "", // O la URL de Firebase Storage
            createdAtMillis = System.currentTimeMillis()
        )

        viewModel.registerNewPlace(negocio)
    }

    private fun clearFields() {
        nameEditText.text.clear()
        descriptionEditText.text.clear()
        hourStartEditText.text.clear()
        hourEndEditText.text.clear()
        priceMinEditText.text.clear()
        priceMaxEditText.text.clear()
        locationTextView.text = "Ubicación: No seleccionada"
        selectedGeoPoint = null
        selectedImageUri = ""
        placeImageView.setImageResource(R.drawable.ic_jama_logo) // Volver a la imagen por defecto
        typeSpinner.setSelection(0)
    }
}