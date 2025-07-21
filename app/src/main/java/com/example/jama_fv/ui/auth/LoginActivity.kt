package com.example.jama_fv.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.jama_fv.R
import com.example.jama_fv.data.remote.FirebaseDataSource
import com.example.jama_fv.data.remote.PlaceRepository
import com.example.jama_fv.data.remote.ReviewRepository
import com.example.jama_fv.data.remote.UserRepository
import com.example.jama_fv.data.remote.Result
import com.example.jama_fv.ui.client.ClientMainActivity
import com.example.jama_fv.ui.owner.OwnerMainActivity
import com.example.jama_fv.utils.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar si hay un usuario autenticado
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // Aquí puedes obtener el rol desde tu base de datos si es necesario
            // o guardarlo previamente en SharedPreferences al momento del login

            // Suponiendo que ya tienes guardado el rol en preferencias (recomendado):
            val sharedPref = getSharedPreferences("session", MODE_PRIVATE)
            val role = sharedPref.getString("user_role", null)

            when (role) {
                "client" -> startActivity(Intent(this, ClientMainActivity::class.java))
                "owner" -> startActivity(Intent(this, OwnerMainActivity::class.java))
                else -> {
                    // Si no se conoce el rol, cerrar sesión por seguridad
                    FirebaseAuth.getInstance().signOut()
                }
            }
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        // Inicializar vistas
        emailEditText = findViewById(R.id.et_login_email)
        passwordEditText = findViewById(R.id.et_login_password)
        loginButton = findViewById(R.id.btn_login)
        registerLink = findViewById(R.id.tv_register_link)

        // Inicializar ViewModel con su Factory
        val firebaseDataSource = FirebaseDataSource()
        val userRepository = UserRepository(firebaseDataSource)
        val placeRepository = PlaceRepository(firebaseDataSource) // Aunque no se usa directamente aquí, la Factory lo necesita
        val reviewRepository = ReviewRepository(firebaseDataSource) // Aunque no se usa directamente aquí, la Factory lo necesita

        val factory = ViewModelFactory(firebaseDataSource, userRepository, placeRepository, reviewRepository)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        // Observar el estado del inicio de sesión
        viewModel.loginStatus.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    Toast.makeText(this, "Iniciando sesión...", Toast.LENGTH_SHORT).show()
                    loginButton.isEnabled = false // Deshabilitar botón durante la carga
                }
                is Result.Success -> {
                    Toast.makeText(this, "Sesión iniciada correctamente.", Toast.LENGTH_SHORT).show()
                    loginButton.isEnabled = true
                    // Redirigir según el rol del usuario
                    val user = result.data
                    if (user.role == "client") {
                        saveUserRole("client")
                        startActivity(Intent(this, ClientMainActivity::class.java))
                    } else if (user.role == "owner") {
                        saveUserRole("owner")
                        startActivity(Intent(this, OwnerMainActivity::class.java))
                    }

                    finish() // Finalizar LoginActivity para que el usuario no pueda volver atrás
                }
                is Result.Error -> {
                    Toast.makeText(this, "Error al iniciar sesión: ${result.exception.message}", Toast.LENGTH_LONG).show()
                    loginButton.isEnabled = true
                }
            }
        }

        // Listener para el botón de inicio de sesión
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa email y contraseña.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.loginUser(email, password)
        }

        // Listener para el enlace de registro
        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
    private fun saveUserRole(role: String) {
        val sharedPref = getSharedPreferences("session", MODE_PRIVATE)
        sharedPref.edit().putString("user_role", role).apply()
    }

}