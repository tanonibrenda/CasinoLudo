package com.grupo1.casinoludo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo1.casinoludo.databinding.ActivityBaseApuestaBinding

// Pantalla 2: el jugador carga sus 3 apuestas, una por una.
// Recién con las 3 cargadas se habilita el botón para girar la ruleta.
class BaseApuestaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBaseApuestaBinding
    private val apuestas = mutableListOf<Apuesta>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBaseApuestaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Según el tipo elegido se muestra el campo del número o las opciones
        binding.rgTipo.setOnCheckedChangeListener { _, idElegido ->
            mostrarOpciones(idElegido)
        }
        mostrarOpciones(binding.rgTipo.checkedRadioButtonId)

        binding.btnApostar.setOnClickListener { agregarApuesta() }
        binding.btnBorrar.setOnClickListener { borrarApuestas() }
        binding.btnGirar.setOnClickListener { irAGirar() }

        actualizarPantalla()
    }

    private fun mostrarOpciones(idElegido: Int) {
        binding.tilNumero.visibility = if (idElegido == R.id.rbNumero) View.VISIBLE else View.GONE
        binding.rgColor.visibility = if (idElegido == R.id.rbColor) View.VISIBLE else View.GONE
        binding.rgParidad.visibility = if (idElegido == R.id.rbParidad) View.VISIBLE else View.GONE
        binding.rgDocena.visibility = if (idElegido == R.id.rbDocena) View.VISIBLE else View.GONE
        binding.tilNumero.error = null
    }

    private fun agregarApuesta() {
        if (apuestas.size >= LogicaRuleta.CANTIDAD_APUESTAS) {
            Toast.makeText(this, R.string.error_ya_tres, Toast.LENGTH_SHORT).show()
            return
        }

        val apuesta = when (binding.rgTipo.checkedRadioButtonId) {
            R.id.rbNumero -> leerNumero()
            R.id.rbColor -> leerOpcion(binding.rgColor, TipoApuesta.COLOR)
            R.id.rbParidad -> leerOpcion(binding.rgParidad, TipoApuesta.PARIDAD)
            R.id.rbDocena -> leerOpcion(binding.rgDocena, TipoApuesta.DOCENA)
            else -> null
        }

        // Si la apuesta no es válida, no se agrega y se vuelve a pedir
        if (apuesta == null) return

        apuestas.add(apuesta)
        limpiarFormulario()
        actualizarPantalla()
    }

    private fun leerNumero(): Apuesta? {
        val texto = binding.etNumero.text.toString().trim()
        if (texto.isEmpty()) {
            binding.tilNumero.error = getString(R.string.error_numero_vacio)
            return null
        }
        if (!LogicaRuleta.esNumeroValido(texto)) {
            binding.tilNumero.error = getString(R.string.error_numero_formato)
            return null
        }
        binding.tilNumero.error = null
        return Apuesta(TipoApuesta.NUMERO, texto)
    }

    private fun leerOpcion(grupo: RadioGroup, tipo: TipoApuesta): Apuesta? {
        val idMarcado = grupo.checkedRadioButtonId
        if (idMarcado == -1) {
            Toast.makeText(this, R.string.error_opcion, Toast.LENGTH_SHORT).show()
            return null
        }
        val opcion = grupo.findViewById<RadioButton>(idMarcado)
        return Apuesta(tipo, opcion.text.toString())
    }

    private fun limpiarFormulario() {
        binding.etNumero.text?.clear()
        binding.rgColor.clearCheck()
        binding.rgParidad.clearCheck()
        binding.rgDocena.clearCheck()
    }

    private fun borrarApuestas() {
        apuestas.clear()
        limpiarFormulario()
        actualizarPantalla()
    }

    // Actualiza el título "Apuesta X de 3", la lista y los botones
    private fun actualizarPantalla() {
        val total = LogicaRuleta.CANTIDAD_APUESTAS
        val completas = apuestas.size == total

        if (completas) {
            binding.tvTituloApuesta.text = getString(R.string.titulo_apuestas_completas)
        } else {
            binding.tvTituloApuesta.text = getString(R.string.titulo_apuesta, apuestas.size + 1, total)
        }

        if (apuestas.isEmpty()) {
            binding.tvListaApuestas.text = getString(R.string.sin_apuestas)
        } else {
            var lista = ""
            for (i in apuestas.indices) {
                lista += "${i + 1}. ${apuestas[i].descripcion()}\n"
            }
            binding.tvListaApuestas.text = lista.trim()
        }

        binding.cardNuevaApuesta.visibility = if (completas) View.GONE else View.VISIBLE
        binding.btnBorrar.isEnabled = apuestas.isNotEmpty()
        binding.btnGirar.isEnabled = completas
    }

    private fun irAGirar() {
        if (apuestas.size < LogicaRuleta.CANTIDAD_APUESTAS) {
            val faltan = LogicaRuleta.CANTIDAD_APUESTAS - apuestas.size
            Toast.makeText(this, getString(R.string.error_faltan_apuestas, faltan), Toast.LENGTH_SHORT).show()
            return
        }

        // Las apuestas viajan a la otra pantalla como dos arreglos de texto
        val tipos = Array(apuestas.size) { apuestas[it].tipo.name }
        val valores = Array(apuestas.size) { apuestas[it].valor }

        val intent = Intent(this, GiroRuletaActivity::class.java)
        intent.putExtra(EXTRA_TIPOS, tipos)
        intent.putExtra(EXTRA_VALORES, valores)
        startActivity(intent)
        finish()
    }

    companion object {
        const val EXTRA_TIPOS = "extra_tipos"
        const val EXTRA_VALORES = "extra_valores"
    }
}
