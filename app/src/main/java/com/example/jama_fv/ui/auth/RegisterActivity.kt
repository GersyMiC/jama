package com.example.jama_fv.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
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

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var roleRadioGroup: RadioGroup
    private lateinit var clientRadioButton: RadioButton
    private lateinit var ownerRadioButton: RadioButton
    private lateinit var registerButton: Button
    private lateinit var loginLink: TextView

    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar vistas
        nameEditText = findViewById(R.id.et_register_name)
        emailEditText = findViewById(R.id.et_register_email)
        passwordEditText = findViewById(R.id.et_register_password)
        roleRadioGroup = findViewById(R.id.rg_role_selection)
        clientRadioButton = findViewById(R.id.rb_client_role)
        ownerRadioButton = findViewById(R.id.rb_owner_role)
        registerButton = findViewById(R.id.btn_register)
        loginLink = findViewById(R.id.tv_login_link)

        // Inicializar ViewModel con su Factory
        val firebaseDataSource = FirebaseDataSource()
        val userRepository = UserRepository(firebaseDataSource)
        val placeRepository = PlaceRepository(firebaseDataSource) // Aunque no se usa directamente aquí, la Factory lo necesita
        val reviewRepository = ReviewRepository(firebaseDataSource) // Aunque no se usa directamente aquí, la Factory lo necesita

        val factory = ViewModelFactory(firebaseDataSource, userRepository, placeRepository, reviewRepository)
        viewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]

        // Observar el estado del registro
        viewModel.registerStatus.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    Toast.makeText(this, "Registrando...", Toast.LENGTH_SHORT).show()
                    registerButton.isEnabled = false
                }
                is Result.Success -> {
                    Toast.makeText(this, "Registro exitoso. ¡Bienvenido!", Toast.LENGTH_SHORT).show()
                    registerButton.isEnabled = true
                    // Redirigir según el rol seleccionado
                    val user = result.data
                    if (user.role == "client") {
                        startActivity(Intent(this, ClientMainActivity::class.java))
                    } else if (user.role == "owner") {
                        startActivity(Intent(this, OwnerMainActivity::class.java))
                    }
                    finish()
                }
                is Result.Error -> {
                    Toast.makeText(this, "Error al registrar: ${result.exception.message}", Toast.LENGTH_LONG).show()
                    registerButton.isEnabled = true
                }
            }
        }

        // Listener para el botón de registro
        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val role = if (clientRadioButton.isChecked) "client" else "owner"

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.registerUser(email, password, name, role)
        }

        // Listener para el enlace de inicio de sesión
        loginLink.setOnClickListener {
            finish() // Vuelve a LoginActivity
        }
    }
}