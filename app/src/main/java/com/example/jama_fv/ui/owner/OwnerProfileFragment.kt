package com.example.jama_fv.ui.owner

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.jama_fv.R
import com.example.jama_fv.data.model.User
import com.example.jama_fv.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvRole: TextView
    private lateinit var btnEditProfile: Button
    private lateinit var btnLogout: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_owner_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvName = view.findViewById(R.id.tv_name)
        tvEmail = view.findViewById(R.id.tv_email)
        tvRole = view.findViewById(R.id.tv_role)
        btnEditProfile = view.findViewById(R.id.btn_edit_profile)
        btnLogout = view.findViewById(R.id.btn_logout)

        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject(User::class.java)
                        tvName.text = user?.name ?: "Nombre no disponible"
                        tvEmail.text = user?.email ?: "Email no disponible"
                        tvRole.text = user?.role ?: "Rol no disponible"
                    } else {
                        Toast.makeText(context, "No se encontró el usuario", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "No has iniciado sesión", Toast.LENGTH_SHORT).show()
        }

        // Puedes configurar aquí los botones si necesitas
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()

            // Limpiar preferencias (si guardaste el rol u otros datos de sesión)
            val sharedPref = requireActivity().getSharedPreferences("session", AppCompatActivity.MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            // Mostrar mensaje
            Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()

            // Redirigir al LoginActivity y limpiar la pila de actividades
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        btnEditProfile.setOnClickListener {
            Toast.makeText(context, "Función de edición aún no implementada", Toast.LENGTH_SHORT).show()
        }
    }
}
