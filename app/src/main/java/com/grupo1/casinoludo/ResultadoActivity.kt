package com.grupo1.casinoludo

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.grupo1.casinoludo.databinding.ActivityResultadoBinding
import com.grupo1.casinoludo.databinding.ItemApuestaResultadoBinding
import com.grupo1.casinoludo.logica.LogicaRuleta
import com.grupo1.casinoludo.modelo.Apuesta
import com.grupo1.casinoludo.modelo.ColorRuleta
import com.grupo1.casinoludo.modelo.ResultadoRuleta
import com.grupo1.casinoludo.util.aplicarMargenesDeSistema
import com.grupo1.casinoludo.util.leerApuestas
import com.grupo1.casinoludo.util.leerResultado

/**
 * Pantalla 3: muestra el número ganador con su color, paridad y docena,
 * compara cada una de las 3 apuestas (Acierto / No acierto) y el resumen final.
 */
class ResultadoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultadoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResultadoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aplicarMargenesDeSistema(binding.main)

        val apuestas = intent.leerApuestas()
        val resultado = intent.leerResultado()
        if (resultado == null) {
            volverAJugar()
            return
        }

        mostrarNumeroGanador(resultado)
        mostrarComparacion(apuestas, resultado)

        binding.btnVolverAJugar.setOnClickListener { volverAJugar() }
        binding.btnSalir.setOnClickListener { salir() }
    }

    private fun mostrarNumeroGanador(resultado: ResultadoRuleta) {
        binding.txtNumeroGanador.text = resultado.numero
        val colorFondo = when (resultado.color) {
            ColorRuleta.ROJO -> R.color.rojo_ruleta
            ColorRuleta.NEGRO -> R.color.negro_ruleta
            ColorRuleta.VERDE -> R.color.verde_cero
        }
        (binding.txtNumeroGanador.background.mutate() as GradientDrawable)
            .setColor(ContextCompat.getColor(this, colorFondo))

        binding.txtColor.text = getString(R.string.detalle_color, resultado.color.etiqueta)
        binding.txtParidad.text = getString(R.string.detalle_paridad, resultado.paridad.etiqueta)
        binding.txtDocena.text = getString(R.string.detalle_docena, resultado.docena.etiqueta)
    }

    private fun mostrarComparacion(apuestas: List<Apuesta>, resultado: ResultadoRuleta) {
        apuestas.forEachIndexed { i, apuesta ->
            val fila = ItemApuestaResultadoBinding.inflate(layoutInflater, binding.contenedorApuestas, false)
            val acerto = LogicaRuleta.esAcierto(apuesta, resultado)

            fila.txtDescripcion.text = "${i + 1}. ${apuesta.descripcion()}"
            fila.txtEstadoApuesta.text = if (acerto) {
                "✔ " + getString(R.string.acierto)
            } else {
                "✘ " + getString(R.string.no_acierto)
            }
            fila.txtEstadoApuesta.setTextColor(
                ContextCompat.getColor(this, if (acerto) R.color.acierto else R.color.no_acierto)
            )
            binding.contenedorApuestas.addView(fila.root)
        }

        val aciertos = LogicaRuleta.contarAciertos(apuestas, resultado)
        binding.txtResumen.text = getString(R.string.resumen_aciertos, aciertos, apuestas.size)

        when {
            aciertos == apuestas.size -> {
                binding.txtTituloResultado.setText(R.string.titulo_gano_todo)
                binding.imgResultado.setImageResource(R.drawable.img_ficha)
            }
            aciertos > 0 -> {
                binding.txtTituloResultado.setText(R.string.titulo_gano)
                binding.imgResultado.setImageResource(R.drawable.img_ficha)
            }
            else -> {
                binding.txtTituloResultado.text =
                    getString(R.string.titulo_perdio) + "\n" + getString(R.string.pero_no_te_rindas)
                binding.imgResultado.setImageResource(R.drawable.img_dado)
            }
        }
    }

    /** Empieza de nuevo con una lista de apuestas vacía. */
    private fun volverAJugar() {
        startActivity(Intent(this, BaseApuestaActivity::class.java))
        finish()
    }

    private fun salir() {
        Toast.makeText(this, R.string.gracias, Toast.LENGTH_LONG).show()
        finishAffinity()
    }
}
