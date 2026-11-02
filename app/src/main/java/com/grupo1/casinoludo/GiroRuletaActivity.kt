package com.grupo1.casinoludo

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.grupo1.casinoludo.databinding.ActivityGiroRuletaBinding
import com.grupo1.casinoludo.logica.LogicaRuleta
import com.grupo1.casinoludo.modelo.Apuesta
import com.grupo1.casinoludo.modelo.ResultadoRuleta
import com.grupo1.casinoludo.red.ClienteApi
import com.grupo1.casinoludo.util.Claves
import com.grupo1.casinoludo.util.aplicarMargenesDeSistema
import com.grupo1.casinoludo.util.leerApuestas
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Se gira la ruleta UNA sola vez para las 3 apuestas.
 * Mientras se anima la ruleta, se consulta el color del número a la API
 * en una corrutina, así la interfaz nunca se bloquea.
 */
class GiroRuletaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGiroRuletaBinding

    private lateinit var apuestas: ArrayList<Apuesta>
    private lateinit var numeroGanador: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGiroRuletaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aplicarMargenesDeSistema(binding.main)

        apuestas = intent.leerApuestas()
        // Si se rota la pantalla se conserva el mismo número: no se vuelve a sortear.
        numeroGanador = savedInstanceState?.getString(ESTADO_NUMERO) ?: LogicaRuleta.girar()

        binding.btnReintentar.setOnClickListener { girarYConsultarColor() }
        girarYConsultarColor()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ESTADO_NUMERO, numeroGanador)
    }

    private fun girarYConsultarColor() {
        mostrarCargando()
        animarRuleta()

        lifecycleScope.launch {
            // La animación y la llamada a la API corren al mismo tiempo.
            val esperaAnimacion = async { delay(DURACION_GIRO_MS) }
            try {
                val color = ClienteApi.consultarColor(numeroGanador)
                esperaAnimacion.await()

                val resultado = LogicaRuleta.armarResultado(numeroGanador, color)
                irAResultados(resultado)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                esperaAnimacion.cancel()
                mostrarError(e.localizedMessage ?: e.toString())
            }
        }
    }

    private fun animarRuleta() {
        ObjectAnimator.ofFloat(binding.imgRuleta, View.ROTATION, 0f, 360f * 5).apply {
            duration = DURACION_GIRO_MS
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    private fun mostrarCargando() {
        binding.progreso.visibility = View.VISIBLE
        binding.btnReintentar.visibility = View.GONE
        binding.txtEstado.setText(R.string.consultando_color)
    }

    private fun mostrarError(detalle: String) {
        binding.progreso.visibility = View.GONE
        binding.btnReintentar.visibility = View.VISIBLE
        binding.txtEstado.text = getString(R.string.error_api, detalle)
    }

    private fun irAResultados(resultado: ResultadoRuleta) {
        val intent = Intent(this, ResultadoActivity::class.java)
            .putExtra(Claves.APUESTAS, apuestas)
            .putExtra(Claves.RESULTADO, resultado)
        startActivity(intent)
        finish()
    }

    private companion object {
        const val ESTADO_NUMERO = "numero_ganador"
        const val DURACION_GIRO_MS = 2500L
    }
}
