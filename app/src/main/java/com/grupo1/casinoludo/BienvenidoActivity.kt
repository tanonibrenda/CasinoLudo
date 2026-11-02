package com.grupo1.casinoludo

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.grupo1.casinoludo.databinding.ActivityBienvenidoBinding
import com.grupo1.casinoludo.util.aplicarMargenesDeSistema

/** Explica las reglas antes de empezar a apostar. */
class BienvenidoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBienvenidoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBienvenidoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aplicarMargenesDeSistema(binding.main)

        binding.btnEntrar.setOnClickListener {
            startActivity(Intent(this, BaseApuestaActivity::class.java))
        }
    }
}
