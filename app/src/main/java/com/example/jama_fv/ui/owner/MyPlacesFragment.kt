// MyPlacesFragment.kt
package com.example.jama_fv.ui.owner

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jama_fv.adapter.NegocioAdapter
import com.example.jama_fv.databinding.FragmentMyPlacesBinding
import com.example.jama_fv.data.remote.AuthRepository
import com.example.jama_fv.data.remote.FirebaseDataSource // <-- Necesitas importar FirebaseDataSource
import com.example.jama_fv.data.remote.PlaceRepository      // <-- Necesitas importar PlaceRepository
import com.example.jama_fv.data.remote.ReviewRepository     // <-- Necesitas importar ReviewRepository

class MyPlacesFragment : Fragment() {

    private var _binding: FragmentMyPlacesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MyPlacesViewModel
    private lateinit var negocioAdapter: NegocioAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPlacesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inicializar el ViewModel
        // Instancia las dependencias que tu ViewModel y Factory necesitan
        val firebaseDataSource = FirebaseDataSource() // Instancia de FirebaseDataSource
        val authRepository = AuthRepository(firebaseDataSource) // AuthRepository necesita FirebaseDataSource
        val placeRepository = PlaceRepository(firebaseDataSource) // PlaceRepository necesita FirebaseDataSource
        val reviewRepository = ReviewRepository(firebaseDataSource) // Asumo que ReviewRepository también necesita FirebaseDataSource

        // Ahora, crea la Factory pasando TODAS las dependencias que su constructor espera
        val viewModelFactory = MyPlacesViewModelFactory(
            firebaseDataSource, // Pasa firebaseDataSource
            authRepository,     // Pasa authRepository
            placeRepository,    // Pasa placeRepository
            reviewRepository    // Pasa reviewRepository
        )

        // Inicializa el ViewModel usando la Factory
        viewModel = ViewModelProvider(this, viewModelFactory)[MyPlacesViewModel::class.java]

        // 2. Configurar el RecyclerView
        setupRecyclerView()

        // 3. Observar los datos del ViewModel
        viewModel.myPlaces.observe(viewLifecycleOwner) { negocios ->
            Log.d("MyPlacesFragment", "Negocios observados: ${negocios.size}")
            negocioAdapter.submitList(negocios)

            binding.progressBarMyPlaces.visibility = View.GONE
            binding.textNoPlacesYet.visibility = if (negocios.isEmpty()) View.VISIBLE else View.GONE
        }

        // Mostrar el ProgressBar mientras se cargan los datos inicialmente
        binding.progressBarMyPlaces.visibility = View.VISIBLE
        binding.textNoPlacesYet.visibility = View.GONE
    }

    private fun setupRecyclerView() {
        negocioAdapter = NegocioAdapter { negocio ->
            Log.d("MyPlacesFragment", "Clic en negocio: ${negocio.nombre}")
        }

        binding.recyclerViewMyPlaces.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = negocioAdapter
            setHasFixedSize(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}