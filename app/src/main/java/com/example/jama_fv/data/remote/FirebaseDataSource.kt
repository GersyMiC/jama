// FirebaseDataSource.kt
package com.example.jama_fv.data.remote
import com.example.jama_fv.data.remote.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.example.jama_fv.data.model.Negocio
import com.example.jama_fv.data.model.User
import com.example.jama_fv.data.model.Review
import kotlinx.coroutines.tasks.await

import java.util.Date

// Usaremos un Sealed Class para manejar el estado de las operaciones (Éxito, Error, Cargando)
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

class FirebaseDataSource(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {

    // --- Métodos de AUTENTICACIÓN (para UserRepository) ---

    // Registro de usuario con email y contraseña
    suspend fun registerUser(email: String, password: String, name: String, role: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                val user = User(
                    uid  = firebaseUser.uid,
                    name = name,
                    email = email,
                    role = role,
                    createdAt =  Timestamp.now()
                )
                firestore.collection("users").document(firebaseUser.uid).set(user).await()
                Result.Success(user)
            } else {
                Result.Error(Exception("No se pudo crear el usuario en Authentication."))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Inicio de sesión de usuario con email y contraseña
    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                val userDocument = firestore.collection("users").document(firebaseUser.uid).get().await()
                val user = userDocument.toObject(User::class.java)
                if (user != null) {
                    Result.Success(user)
                } else {
                    Result.Error(Exception("No se encontró el perfil de usuario en Firestore."))
                }
            } else {
                Result.Error(Exception("Fallo al iniciar sesión en Authentication."))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Obtener datos del usuario actual
    suspend fun getCurrentUser(): Result<User> {
        return try {
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                val userDocument = firestore.collection("users").document(firebaseUser.uid).get().await()
                val user = userDocument.toObject(User::class.java)
                if (user != null) {
                    Result.Success(user)
                } else {
                    Result.Error(Exception("No se encontró el perfil de usuario en Firestore."))
                }
            } else {
                Result.Error(Exception("Usuario no autenticado."))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Cerrar sesión
    fun logout() {
        auth.signOut()
    }

    // --- Métodos de NEGOCIOS (para PlaceRepository) ---

    // Añadir un nuevo negocio a Firestore
    suspend fun addNegocio(negocio: Negocio): Result<Negocio> {
        return try {
            val docRef = firestore.collection("negocios").add(negocio).await()
            val newNegocio = negocio.copy(id = docRef.id) // Asignar el ID generado por Firestore
            docRef.set(newNegocio).await() // Actualizar el documento con el ID
            Result.Success(newNegocio)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Obtener todos los negocios (para clientes)
    suspend fun getNegocios(): Result<List<Negocio>> {
        return try {
            val result = firestore.collection("negocios").get().await()
            val negocios = result.documents.mapNotNull { it.toObject(Negocio::class.java)}
            Result.Success(negocios)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Obtener negocios por ID de dueño (para dueños)
    suspend fun getNegociosByOwner(ownerId: String): Result<List<Negocio>> {
        return try {
            val result = firestore.collection("negocios").whereEqualTo("userId", ownerId).get().await()
            val negocios = result.documents.mapNotNull { it.toObject(Negocio::class.java)}
            Result.Success(negocios)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Actualizar un negocio existente
    suspend fun updateNegocio(negocio: Negocio): Result<Negocio> {
        return try {
            negocio.id?.let {
                firestore.collection("negocios").document(it).set(negocio).await()
                Result.Success(negocio)
            } ?: Result.Error(IllegalArgumentException("ID de negocio no puede ser nulo para actualizar."))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Eliminar un negocio
    suspend fun deleteNegocio(negocioId: String): Result<Unit> {
        return try {
            firestore.collection("negocios").document(negocioId).delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // --- Métodos de RESEÑAS (para ReviewRepository) ---

    // Añadir una reseña
    suspend fun addReview(review: Review): Result<Review> {
        return try {
            val docRef = firestore.collection("reviews").add(review).await()
            Result.Success(review) // Firestore no devuelve el ID de la reseña en el objeto, pero se guarda
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Obtener reseñas para un negocio específico
    suspend fun getReviewsForBusiness(businessId: String): Result<List<Review>> {
        return try {
            val result = firestore.collection("reviews").whereEqualTo("businessId", businessId).get().await()
            val reviews = result.documents.mapNotNull { it.toObject(Review::class.java) }
            Result.Success(reviews)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // --- Métodos de Storage (para subir imágenes, opcional) ---
    // (Ejemplo, necesitarías InputStream o Uri para la imagen)
    /*
    suspend fun uploadImage(imageUri: Uri, path: String): Result<String> {
        return try {
            val ref = storage.reference.child(path)
            val uploadTask = ref.putFile(imageUri).await()
            val imageUrl = ref.downloadUrl.await().toString()
            Result.Success(imageUrl)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    */
}