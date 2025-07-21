package com.example.jama_fv.ui.owner

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.example.jama_fv.data.model.Negocio
import com.example.jama_fv.data.remote.AuthRepository
import com.example.jama_fv.data.remote.FirebaseDataSource // Assuming you have this
import com.example.jama_fv.data.remote.PlaceRepository // Assuming you have this
import com.example.jama_fv.data.remote.ReviewRepository // Assuming you have this if your factory needs it

/**
 * ViewModel for MyPlacesFragment.
 * Manages fetching and exposing a list of businesses owned by the current user.
 */
class MyPlacesViewModel(
    // These dependencies are passed by your MyPlacesViewModelFactory
    private val firebaseDataSource: FirebaseDataSource,
    private val authRepository: AuthRepository,
    private val placeRepository: PlaceRepository, // You might not strictly need this here if data is fetched directly
    private val reviewRepository: ReviewRepository // Only if your ViewModel uses it
) : ViewModel() {

    private val _myPlaces = MutableLiveData<List<Negocio>>()
    val myPlaces: LiveData<List<Negocio>> get() = _myPlaces

    private var firestoreListener: ListenerRegistration? = null

    init {
        // Load the places as soon as the ViewModel is created
        loadMyPlaces()
    }

    /**
     * Loads the businesses owned by the current user from Firebase Firestore.
     * Uses a real-time listener to automatically update the list.
     */
    fun loadMyPlaces() {
        val currentUserId = authRepository.getCurrentUser()?.uid // Get the ID of the current authenticated user

        if (currentUserId != null) {
            // Remove any existing listener to prevent duplicate listeners
            firestoreListener?.remove()

            // Set up a real-time listener to the 'negocios' collection
            // Filter documents where 'ownerId' matches the current user's ID
            firestoreListener = FirebaseFirestore.getInstance()
                .collection("negocios") // Make sure this is your actual Firestore collection name for businesses
                .whereEqualTo("userId", currentUserId) // Filter by the owner's ID
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("MyPlacesViewModel", "Error listening for businesses: ${e.message}", e)
                        _myPlaces.value = emptyList() // Set to empty list on error
                        return@addSnapshotListener
                    }

                    val businessesList = mutableListOf<Negocio>()
                    if (snapshot != null) {
                        for (doc in snapshot.documents) {
                            // Convert each Firestore document to a Negocio object
                            // Ensure your Negocio data class has an empty constructor for Firestore deserialization
                            val business = doc.toObject(Negocio::class.java)?.copy(id = doc.id)
                            business?.let { businessesList.add(it) }
                        }
                    }
                    _myPlaces.value = businessesList // Update the LiveData, which will notify the UI
                }
        } else {
            Log.d("MyPlacesViewModel", "User not authenticated. Cannot load businesses.")
            _myPlaces.value = emptyList() // No user, so no businesses to show
        }
    }

    /**
     * Called when the ViewModel is no longer used and will be destroyed.
     * Important to remove the Firestore listener to prevent memory leaks.
     */
    override fun onCleared() {
        super.onCleared()
        firestoreListener?.remove() // Remove the real-time listener
    }
}