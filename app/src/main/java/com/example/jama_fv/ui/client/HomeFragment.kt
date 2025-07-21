package com.example.jama_fv.ui.client
import com.google.firebase.firestore.FirebaseFirestore
import com.example.jama_fv.data.model.Negocio
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.jama_fv.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.jama_fv.ui.client.PlaceDetailFragment
import com.example.jama_fv.ui.client.HomeFragment
import com.google.android.gms.maps.MapView
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class HomeFragment : Fragment(), OnMapReadyCallback { // Implementa OnMapReadyCallback

    private var googleMap: GoogleMap? = null
    private val TAG = "HomeFragment" // Para Logcat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inicializar el SupportMapFragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_container_home_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this) // Llama al callback onMapReady cuando el mapa esté listo

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        Log.d(TAG, "GoogleMap está listo en HomeFragment.")

        // Ejemplo: Mover la cámara a una ubicación predeterminada (por ejemplo, el centro de Huancayo)
        val huancayo = LatLng(-12.0664, -75.2048) // Latitud y longitud de Huancayo, Perú
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(huancayo, 14f))
        googleMap?.addMarker(MarkerOptions().position(huancayo).title("Mi Ubicación por Defecto"))

        // Habilitar controles de zoom (opcional)
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        googleMap?.uiSettings?.isCompassEnabled = true

        val db = FirebaseFirestore.getInstance()

        db.collection("negocios")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val negocio = document.toObject(Negocio::class.java)
                    document.id // Guarda el ID real
                    val lat = negocio.latitud
                    val lng = negocio.longitud
                    val nombre = negocio.nombre

                    if (lat != null && lng != null) {
                        val marker = googleMap?.addMarker(
                            MarkerOptions()
                                .position(LatLng(lat, lng))
                                .title(nombre)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        )
                        marker?.tag = negocio // Guarda el negocio completo en el marcador
                    }
                }
            }
        googleMap?.setOnMarkerClickListener { marker ->
            val negocio = marker.tag as? Negocio
            negocio?.let {
                val bundle = Bundle().apply {
                    putSerializable("negocio", it)
                }
                findNavController().navigate(R.id.action_homeFragment_to_placeDetailFragment, bundle)
            }
            true
        }




    }



    // Opcional: Manejar el ciclo de vida del mapa si fuera más complejo
    override fun onResume() {
        super.onResume()
        // mapFragment?.onResume() // Si usaras MapView en vez de SupportMapFragment
    }

    override fun onPause() {
        super.onPause()
        // mapFragment?.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // mapFragment?.onDestroyView()
        googleMap = null // Liberar la referencia al mapa
    }
}