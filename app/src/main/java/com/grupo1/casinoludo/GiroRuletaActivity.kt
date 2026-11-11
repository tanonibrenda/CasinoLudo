package com.grupo1.casinoludo

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.grupo1.casinoludo.databinding.ActivityGiroRuletaBinding
import kotlinx.coroutines.launch
import kotlin.random.Random

// Se gira la ruleta una sola vez para las 3 apuestas y se pide el color a la API
class GiroRuletaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGiroRuletaBinding
    private var tipos = arrayOf<String>()
    private var valores = arrayOf<String>()
    private var numeroGanador = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGiroRuletaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tipos = intent.getStringArrayExtra(BaseApuestaActivity.EXTRA_TIPOS) ?: arrayOf()
        valores = intent.getStringArrayExtra(BaseApuestaActivity.EXTRA_VALORES) ?: arrayOf()

        numeroGanador = LogicaRuleta.girar()

        binding.btnReintentar.setOnClickListener { consultarApi() }
        girarRuleta()
    }

    // Animación de propiedades (ObjectAnimator): la ruleta da varias vueltas
    // y va frenando de a poco. Cuando termina, se consulta el color a la API.
    private fun girarRuleta() {
        binding.tvEstado.text = getString(R.string.girando)
        binding.progreso.visibility = View.GONE

        val vueltas = 360f * 5 + Random.nextInt(0, 360)
        val animacion = ObjectAnimator.ofFloat(binding.imgRuleta, "rotation", 0f, vueltas)
        animacion.duration = 3500
        animacion.interpolator = DecelerateInterpolator()
        animacion.doOnEnd { consultarApi() }
        animacion.start()
    }

    // Igual que en la guía: corrutina con lifecycleScope para no bloquear la pantalla
    private fun consultarApi() {
        binding.tvEstado.text = getString(R.string.consultando_color)
        binding.progreso.visibility = View.VISIBLE
        binding.btnReintentar.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getBolaColor(bola = numeroGanador)
                val color = response.body()?.color
                val errorApi = response.body()?.error

                if (response.isSuccessful && color != null) {
                    irAResultados(color)
                } else if (errorApi != null) {
                    mostrarError(errorApi)
                } else {
                    mostrarError("Error HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                // Sin internet, servidor caído, etc.
                mostrarError("Error de conexión: ${e.localizedMessage}")
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        binding.progreso.visibility = View.GONE
        binding.btnReintentar.visibility = View.VISIBLE
        binding.tvEstado.text = getString(R.string.error_api, mensaje)
    }

    private fun irAResultados(color: String) {
        val intent = Intent(this, ResultadoActivity::class.java)
        intent.putExtra(BaseApuestaActivity.EXTRA_TIPOS, tipos)
        intent.putExtra(BaseApuestaActivity.EXTRA_VALORES, valores)
        intent.putExtra(EXTRA_NUMERO, numeroGanador)
        intent.putExtra(EXTRA_COLOR, color)
        startActivity(intent)
        finish()
    }

    companion object {
        const val EXTRA_NUMERO = "extra_numero"
        const val EXTRA_COLOR = "extra_color"
    }
}
