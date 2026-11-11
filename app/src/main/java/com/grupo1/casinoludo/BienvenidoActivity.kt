package com.grupo1.casinoludo

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo1.casinoludo.databinding.ActivityBienvenidoBinding

// Muestra las reglas del juego antes de empezar a apostar
class BienvenidoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBienvenidoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBienvenidoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnEntrar.setOnClickListener {
            val intent = Intent(this, BaseApuestaActivity::class.java)
            startActivity(intent)
        }
    }
}
