package com.grupo1.casinoludo.util

import android.content.Intent
import android.view.View
import androidx.core.content.IntentCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupo1.casinoludo.modelo.Apuesta
import com.grupo1.casinoludo.modelo.ResultadoRuleta

/** Claves para pasar datos entre pantallas con Intent. */
object Claves {
    const val APUESTAS = "apuestas"
    const val RESULTADO = "resultado"
}

/** Deja espacio para la barra de estado y la de navegación (la app usa edge-to-edge). */
fun aplicarMargenesDeSistema(raiz: View) {
    ViewCompat.setOnApplyWindowInsetsListener(raiz) { vista, insets ->
        val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        vista.setPadding(barras.left, barras.top, barras.right, barras.bottom)
        insets
    }
}

@Suppress("UNCHECKED_CAST")
fun Intent.leerApuestas(): ArrayList<Apuesta> =
    (IntentCompat.getSerializableExtra(this, Claves.APUESTAS, ArrayList::class.java)
        as? ArrayList<Apuesta>) ?: arrayListOf()

fun Intent.leerResultado(): ResultadoRuleta? =
    IntentCompat.getSerializableExtra(this, Claves.RESULTADO, ResultadoRuleta::class.java)
