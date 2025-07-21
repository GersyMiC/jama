// PlaceRepository.kt
package com.example.jama_fv.data.remote
import com.example.jama_fv.data.model.Negocio
import com.example.jama_fv.data.remote.FirebaseDataSource
import com.example.jama_fv.data.remote.Result

class PlaceRepository(private val firebaseDataSource: FirebaseDataSource) {

    suspend fun addNegocio(negocio: Negocio): Result<Negocio> {
        return firebaseDataSource.addNegocio(negocio)
    }

    suspend fun getNegocios(): Result<List<Negocio>> {
        return firebaseDataSource.getNegocios()
    }

    suspend fun getNegociosByOwner(ownerId: String): Result<List<Negocio>> {
        return firebaseDataSource.getNegociosByOwner(ownerId)
    }

    suspend fun updateNegocio(negocio: Negocio): Result<Negocio> {
        return firebaseDataSource.updateNegocio(negocio)
    }

    suspend fun deleteNegocio(negocioId: String): Result<Unit> {
        return firebaseDataSource.deleteNegocio(negocioId)
    }

    // Otros métodos específicos de lógica de negocio para lugares si los necesitas
    // Por ejemplo, filtrar, ordenar, buscar, etc.
}