package com.grupo1.casinoludo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BundleCompat
import androidx.core.widget.doAfterTextChanged
import com.grupo1.casinoludo.databinding.ActivityBaseApuestaBinding
import com.grupo1.casinoludo.logica.LogicaRuleta
import com.grupo1.casinoludo.modelo.Apuesta
import com.grupo1.casinoludo.modelo.TipoApuesta
import com.grupo1.casinoludo.util.Claves
import com.grupo1.casinoludo.util.aplicarMargenesDeSistema

/**
 * Pantalla 2: el jugador carga sus apuestas de a una.
 * El mismo formulario se repite hasta tener 3 apuestas válidas;
 * recién ahí se habilita "Girar la ruleta".
 */
class BaseApuestaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBaseApuestaBinding

    private val apuestas = ArrayList<Apuesta>()
    private var tipoSeleccionado = TipoApuesta.NUMERO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBaseApuestaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aplicarMargenesDeSistema(binding.main)

        restaurarEstado(savedInstanceState)
        configurarSelectorDeTipo()

        binding.inputNumero.doAfterTextChanged { binding.layoutNumero.error = null }
        binding.btnApostar.setOnClickListener { agregarApuesta() }
        binding.btnBorrar.setOnClickListener { borrarApuestas() }
        binding.btnGirar.setOnClickListener { irAGirar() }

        actualizarPantalla()
    }

    // Guarda las apuestas si se gira el celular, para no perder lo cargado.
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(Claves.APUESTAS, apuestas)
        outState.putString(ESTADO_TIPO, tipoSeleccionado.name)
    }

    @Suppress("UNCHECKED_CAST")
    private fun restaurarEstado(estado: Bundle?) {
        if (estado == null) return
        val guardadas = BundleCompat.getSerializable(estado, Claves.APUESTAS, ArrayList::class.java)
        (guardadas as? ArrayList<Apuesta>)?.let { apuestas.addAll(it) }
        estado.getString(ESTADO_TIPO)?.let { tipoSeleccionado = TipoApuesta.valueOf(it) }
    }

    private fun configurarSelectorDeTipo() {
        val botonPorTipo = mapOf(
            TipoApuesta.NUMERO to binding.btnTipoNumero.id,
            TipoApuesta.COLOR to binding.btnTipoColor.id,
            TipoApuesta.PARIDAD to binding.btnTipoParidad.id,
            TipoApuesta.DOCENA to binding.btnTipoDocena.id
        )
        binding.grupoTipo.check(botonPorTipo.getValue(tipoSeleccionado))
        mostrarCampoSegunTipo()

        binding.grupoTipo.addOnButtonCheckedListener { _, idBoton, marcado ->
            if (!marcado) return@addOnButtonCheckedListener
            tipoSeleccionado = botonPorTipo.entries.first { it.value == idBoton }.key
            mostrarCampoSegunTipo()
        }
    }

    /** Número → campo de texto. Color, paridad o docena → opciones para tocar. */
    private fun mostrarCampoSegunTipo() {
        val esNumero = tipoSeleccionado == TipoApuesta.NUMERO
        binding.layoutNumero.visibility = if (esNumero) View.VISIBLE else View.GONE
        binding.grupoOpciones.visibility = if (esNumero) View.GONE else View.VISIBLE
        binding.layoutNumero.error = null

        binding.grupoOpciones.removeAllViews()
        tipoSeleccionado.opciones.forEach { opcion ->
            val radio = RadioButton(this).apply {
                id = View.generateViewId()
                text = opcion
                textSize = 18f
                setPadding(0, 0, 32, 0)
            }
            binding.grupoOpciones.addView(radio)
        }
    }

    private fun agregarApuesta() {
        if (apuestas.size >= LogicaRuleta.CANTIDAD_APUESTAS) {
            avisar(getString(R.string.error_ya_tres))
            return
        }
        val valor = leerValorIngresado() ?: return
        val apuesta = Apuesta(tipoSeleccionado, valor)

        if (!LogicaRuleta.esApuestaValida(apuesta)) {
            avisar(getString(R.string.error_numero_formato))
            return
        }
        apuestas.add(apuesta)
        limpiarFormulario()
        actualizarPantalla()
    }

    /** Devuelve el valor elegido, o null (mostrando el error) si falta o es inválido. */
    private fun leerValorIngresado(): String? {
        if (tipoSeleccionado == TipoApuesta.NUMERO) {
            val texto = binding.inputNumero.text?.toString()?.trim().orEmpty()
            return when {
                texto.isEmpty() -> {
                    binding.layoutNumero.error = getString(R.string.error_numero_vacio); null
                }
                !LogicaRuleta.esNumeroValido(texto) -> {
                    binding.layoutNumero.error = getString(R.string.error_numero_formato); null
                }
                else -> texto
            }
        }
        val idMarcado = binding.grupoOpciones.checkedRadioButtonId
        if (idMarcado == View.NO_ID) {
            avisar(getString(R.string.error_opcion))
            return null
        }
        return findViewById<RadioButton>(idMarcado).text.toString()
    }

    private fun limpiarFormulario() {
        binding.inputNumero.text?.clear()
        binding.grupoOpciones.clearCheck()
    }

    private fun borrarApuestas() {
        apuestas.clear()
        limpiarFormulario()
        actualizarPantalla()
    }

    /** Refresca el título "Apuesta X de 3", la lista y qué botones están habilitados. */
    private fun actualizarPantalla() {
        val total = LogicaRuleta.CANTIDAD_APUESTAS
        val completas = apuestas.size >= total

        binding.txtTituloApuesta.text = if (completas) {
            getString(R.string.titulo_apuestas_completas)
        } else {
            getString(R.string.titulo_apuesta, apuestas.size + 1, total)
        }

        binding.txtListaApuestas.text = if (apuestas.isEmpty()) {
            getString(R.string.sin_apuestas)
        } else {
            apuestas.mapIndexed { i, a -> "${i + 1}. ${a.descripcion()}" }.joinToString("\n")
        }

        binding.cardNuevaApuesta.visibility = if (completas) View.GONE else View.VISIBLE
        binding.btnBorrar.isEnabled = apuestas.isNotEmpty()
        binding.btnGirar.isEnabled = completas
    }

    private fun irAGirar() {
        val faltan = LogicaRuleta.CANTIDAD_APUESTAS - apuestas.size
        if (faltan > 0) {
            avisar(getString(R.string.error_faltan_apuestas, faltan))
            return
        }
        val intent = Intent(this, GiroRuletaActivity::class.java)
            .putExtra(Claves.APUESTAS, ArrayList(apuestas))
        startActivity(intent)
        finish()
    }

    private fun avisar(mensaje: String) =
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()

    private companion object {
        const val ESTADO_TIPO = "tipo_seleccionado"
    }
}
