package com.example.jama_fv.ui.client

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jama_fv.data.model.Negocio
import com.example.jama_fv.data.model.Review
import com.example.jama_fv.data.remote.PlaceRepository
import com.example.jama_fv.data.remote.ReviewRepository
import com.example.jama_fv.data.remote.Result
import kotlinx.coroutines.launch

class PlaceDetailViewModel(
    private val placeRepository: PlaceRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _place = MutableLiveData<Result<Negocio>>()
    val place: LiveData<Result<Negocio>> = _place

    private val _reviews = MutableLiveData<Result<List<Review>>>()
    val reviews: LiveData<Result<List<Review>>> = _reviews

    private val _reviewSubmissionStatus = MutableLiveData<Result<Review>>()
    val reviewSubmissionStatus: LiveData<Result<Review>> = _reviewSubmissionStatus

    // Puedes añadir una función para cargar un negocio específico si es necesario
    // Por ahora, asumimos que el negocio se pasa al fragmento

    /**
     * Carga las reseñas para un negocio específico.
     * @param businessId El ID del negocio cuyas reseñas se quieren cargar.
     */
    fun loadReviewsForBusiness(businessId: String) {
        _reviews.value = Result.Loading
        viewModelScope.launch {
            val result = reviewRepository.getReviewsForBusiness(businessId)
            _reviews.value = result
        }
    }

    /**
     * Envía una nueva reseña para un negocio.
     * @param review La reseña a enviar.
     */
    fun submitReview(review: Review) {
        _reviewSubmissionStatus.value = Result.Loading
        viewModelScope.launch {
            val result = reviewRepository.addReview(review)
            _reviewSubmissionStatus.value = result
            // Si la reseña se envía con éxito, recargar las reseñas para actualizar la lista
            if (result is Result.Success) {
                loadReviewsForBusiness(review.businessId)
            }
        }
    }
}