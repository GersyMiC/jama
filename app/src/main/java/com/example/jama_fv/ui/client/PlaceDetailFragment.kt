package com.example.jama_fv.ui.client

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Necesitarás añadir la dependencia de Glide en build.gradle.kts
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.example.jama_fv.R
import com.example.jama_fv.data.model.Negocio
import com.example.jama_fv.data.model.Review
import com.example.jama_fv.data.remote.FirebaseDataSource
import com.example.jama_fv.data.remote.PlaceRepository
import com.example.jama_fv.data.remote.ReviewRepository
import com.example.jama_fv.data.remote.Result
import com.example.jama_fv.data.remote.UserRepository
import com.example.jama_fv.utils.ViewModelFactory
import java.util.Date

class PlaceDetailFragment : Fragment() {

    private lateinit var viewModel: PlaceDetailViewModel
    private lateinit var reviewAdapter: ReviewAdapter

    // Vistas del detalle del negocio
    private lateinit var imageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var typeTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var horarioTextView: TextView
    private lateinit var preciosTextView: TextView
    private lateinit var reviewsRecyclerView: RecyclerView
    private lateinit var reviewCommentEditText: EditText
    private lateinit var reviewRatingBar: RatingBar
    private lateinit var submitReviewButton: Button

    // El negocio que se está mostrando
    private var currentNegocio: Negocio? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Recuperar el objeto Negocio de los argumentos
        arguments?.let {
            currentNegocio = arguments?.getSerializable("negocio") as Negocio?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_negocio_detail, container, false)

        // Inicializar vistas
        imageView = view.findViewById(R.id.iv_detail_image)
        nameTextView = view.findViewById(R.id.tv_detail_name)
        typeTextView = view.findViewById(R.id.tv_detail_type)
        descriptionTextView = view.findViewById(R.id.tv_description)
        horarioTextView = view.findViewById(R.id.tv_detail_horario)
        preciosTextView = view.findViewById(R.id.tv_detail_precios)
        reviewsRecyclerView = view.findViewById(R.id.rv_reviews)
        reviewCommentEditText = view.findViewById(R.id.et_review_comment)
        reviewRatingBar = view.findViewById(R.id.rating_bar_review)
        submitReviewButton = view.findViewById(R.id.btn_submit_review)

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
        viewModel = ViewModelProvider(this, factory)[PlaceDetailViewModel::class.java]

        // Configurar RecyclerView para reseñas
        reviewAdapter = ReviewAdapter(mutableListOf())
        reviewsRecyclerView.layoutManager = LinearLayoutManager(context)
        reviewsRecyclerView.adapter = reviewAdapter

        // Mostrar los detalles del negocio
        currentNegocio?.let { negocio ->
            nameTextView.text = negocio.nombre
            typeTextView.text = "Tipo: ${negocio.tipo}"
            descriptionTextView.text = negocio.descripcion
            horarioTextView.text = "Horario: ${negocio.horario}"
            preciosTextView.text = "Precios: ${negocio.precios}"

            // Cargar imagen con Glide (asegúrate de tener la dependencia)
            if (negocio.imageUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(negocio.imageUrl)
                    .placeholder(R.drawable.ic_jama_logo) // Placeholder si la imagen no carga
                    .error(R.drawable.ic_jama_logo) // Imagen de error
                    .into(imageView)
            } else {
                imageView.setImageResource(R.drawable.ic_jama_logo) // Imagen por defecto
            }

            // Cargar reseñas para este negocio
            negocio.id?.let { businessId ->
                viewModel.loadReviewsForBusiness(businessId)
            } ?: run {
                Toast.makeText(context, "Error: ID de negocio no disponible para cargar reseñas.", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(context, "Error: No se pudo cargar la información del negocio.", Toast.LENGTH_SHORT).show()
            // Considerar cerrar el fragmento o mostrar un mensaje de error
        }

        // Observar las reseñas
        viewModel.reviews.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    // Mostrar indicador de carga de reseñas
                }
                is Result.Success -> {
                    reviewAdapter.updateReviews(result.data)
                }
                is Result.Error -> {
                    Toast.makeText(context, "Error al cargar reseñas: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Observar el estado de envío de la reseña
        viewModel.reviewSubmissionStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    submitReviewButton.isEnabled = false
                }
                is Result.Success -> {
                    Toast.makeText(context, "Reseña enviada con éxito.", Toast.LENGTH_SHORT).show()
                    reviewCommentEditText.text.clear() // Limpiar campo de reseña
                    reviewRatingBar.rating = 0f // Resetear rating
                    submitReviewButton.isEnabled = true
                    // Las reseñas se recargan automáticamente en el ViewModel
                }
                is Result.Error -> {
                    Toast.makeText(context, "Error al enviar reseña: ${result.exception.message}", Toast.LENGTH_LONG).show()
                    submitReviewButton.isEnabled = true
                }
            }
        }

        // Listener para enviar reseña
        submitReviewButton.setOnClickListener {
            val comment = reviewCommentEditText.text.toString().trim()
            val rating = reviewRatingBar.rating.toDouble()
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser == null) {
                Toast.makeText(context, "Debes iniciar sesión para dejar una reseña.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (comment.isEmpty() || rating == 0.0) {
                Toast.makeText(context, "Por favor, escribe un comentario y selecciona una calificación.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            currentNegocio?.id?.let { businessId ->
                // Asumimos que el nombre del usuario se puede obtener del objeto User en Firestore
                // Para simplificar, aquí usamos un placeholder. Idealmente, lo obtendrías del UserRepository.
                val userName = currentUser.displayName ?: "Anónimo" // O consulta el nombre real del usuario desde Firestore

                val review = Review(
                    businessId = businessId,
                    userId = currentUser.uid,
                    userName = userName,
                    rating = rating,
                    comment = comment,
                    createdAt = Timestamp(Date())
                )
                viewModel.submitReview(review)
            } ?: run {
                Toast.makeText(context, "Error: No se puede enviar reseña sin un negocio válido.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}