package com.grupo1.casinoludo

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo1.casinoludo.databinding.ActivityResultadoBinding

// Pantalla 3: número ganador, comparación de las 3 apuestas y resumen
class ResultadoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultadoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResultadoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Se reciben los datos de la pantalla anterior
        val tipos = intent.getStringArrayExtra(BaseApuestaActivity.EXTRA_TIPOS) ?: arrayOf()
        val valores = intent.getStringArrayExtra(BaseApuestaActivity.EXTRA_VALORES) ?: arrayOf()
        val numero = intent.getStringExtra(GiroRuletaActivity.EXTRA_NUMERO) ?: ""
        val colorApi = intent.getStringExtra(GiroRuletaActivity.EXTRA_COLOR) ?: ""

        val apuestas = mutableListOf<Apuesta>()
        for (i in tipos.indices) {
            apuestas.add(Apuesta(TipoApuesta.valueOf(tipos[i]), valores[i]))
        }

        val resultado = LogicaRuleta.armarResultado(numero, colorApi)

        mostrarNumeroGanador(resultado)
        mostrarApuestas(apuestas, resultado)

        binding.btnVolverAJugar.setOnClickListener {
            // Vuelve a la carga de apuestas con la lista vacía
            startActivity(Intent(this, BaseApuestaActivity::class.java))
            finish()
        }
        binding.btnSalir.setOnClickListener {
            Toast.makeText(this, R.string.gracias, Toast.LENGTH_LONG).show()
            finishAffinity()
        }
    }

    private fun mostrarNumeroGanador(resultado: ResultadoRuleta) {
        binding.tvNumeroGanador.text = resultado.numero

        // El número se pinta del color que devolvió la API
        val color = when (resultado.color) {
            "Rojo" -> R.color.rojo_ruleta
            "Negro" -> R.color.negro_ruleta
            else -> R.color.verde_cero
        }
        binding.tvNumeroGanador.setTextColor(ContextCompat.getColor(this, color))

        binding.tvColor.text = getString(R.string.detalle_color, resultado.color)
        binding.tvParidad.text = getString(R.string.detalle_paridad, resultado.paridad)
        binding.tvDocena.text = getString(R.string.detalle_docena, resultado.docena)
    }

    private fun mostrarApuestas(apuestas: List<Apuesta>, resultado: ResultadoRuleta) {
        val textosApuesta = listOf(binding.tvApuesta1, binding.tvApuesta2, binding.tvApuesta3)
        val textosEstado = listOf(binding.tvEstado1, binding.tvEstado2, binding.tvEstado3)

        for (i in apuestas.indices) {
            textosApuesta[i].text = "${i + 1}. ${apuestas[i].descripcion()}"
            pintarEstado(textosEstado[i], LogicaRuleta.esAcierto(apuestas[i], resultado))
        }

        val aciertos = LogicaRuleta.contarAciertos(apuestas, resultado)
        binding.tvResumen.text = getString(R.string.resumen_aciertos, aciertos, apuestas.size)

        if (aciertos > 0) {
            binding.tvTituloResultado.text = getString(R.string.titulo_gano)
            binding.imgResultado.setImageResource(R.drawable.img_fichas)
        } else {
            binding.tvTituloResultado.text = getString(R.string.titulo_perdio)
            binding.imgResultado.setImageResource(R.drawable.img_ruleta)
        }
    }

    private fun pintarEstado(texto: TextView, acerto: Boolean) {
        if (acerto) {
            texto.text = getString(R.string.acierto)
            texto.setTextColor(ContextCompat.getColor(this, R.color.acierto))
        } else {
            texto.text = getString(R.string.no_acierto)
            texto.setTextColor(ContextCompat.getColor(this, R.color.no_acierto))
        }
    }
}
