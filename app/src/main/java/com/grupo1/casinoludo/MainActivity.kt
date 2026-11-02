package com.grupo1.casinoludo

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.grupo1.casinoludo.databinding.ActivityMainBinding
import com.grupo1.casinoludo.util.aplicarMargenesDeSistema

/** Pantalla 1: Bienvenida con imagen, título y botón JUGAR. */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aplicarMargenesDeSistema(binding.main)

        binding.btnJugar.setOnClickListener {
            startActivity(Intent(this, BienvenidoActivity::class.java))
        }
    }
}
