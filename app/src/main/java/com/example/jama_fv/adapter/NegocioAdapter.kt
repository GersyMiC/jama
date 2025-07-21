// NegocioAdapter.kt (crea un nuevo archivo en el paquete adapter o similar)
package com.example.jama_fv.adapter // O la ruta de tu paquete de adaptadores

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jama_fv.data.model.Negocio // Tu clase Negocio
import com.example.jama_fv.databinding.ItemNegocioBinding // Asegúrate de que esta ruta sea correcta
// Usaremos un layout llamado item_negocio.xml para cada elemento de la lista

class NegocioAdapter(private val onItemClick: (Negocio) -> Unit) :
    ListAdapter<Negocio, NegocioAdapter.NegocioViewHolder>(NegocioDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NegocioViewHolder {
        val binding = ItemNegocioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NegocioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NegocioViewHolder, position: Int) {
        val negocio = getItem(position)
        holder.bind(negocio)
        holder.itemView.setOnClickListener { onItemClick(negocio) }
    }

    inner class NegocioViewHolder(private val binding: ItemNegocioBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(negocio: Negocio) {
            // Aquí asignas los datos del objeto Negocio a las vistas de tu item_negocio.xml
            binding.textViewNombreNegocio.text = negocio.nombre

            // binding.imageViewNegocio.load(negocio.imageUrl) // Si usas Coil/Glide para imágenes
            // Asegúrate de que los IDs de tus TextView/ImageView en item_negocio.xml coincidan
        }
    }

    class NegocioDiffCallback : DiffUtil.ItemCallback<Negocio>() {
        override fun areItemsTheSame(oldItem: Negocio, newItem: Negocio): Boolean {
            return oldItem.id == newItem.id // Compara por el ID único
        }

        override fun areContentsTheSame(oldItem: Negocio, newItem: Negocio): Boolean {
            return oldItem == newItem // Compara si el contenido es el mismo
        }
    }
}