package com.example.jama_fv.ui.owner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.jama_fv.R

class OwnerMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_main)

        // Configurar el NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_owner) as NavHostFragment
        val navController = navHostFragment.navController

        // Configurar la BottomNavigationView con el NavController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_owner)
        bottomNavigationView.setupWithNavController(navController)

        // Aquí puedes añadir lógica para verificar el usuario actual y su rol
        // Si no hay usuario logueado o el rol no es dueño, redirigir a LoginActivity
    }
}