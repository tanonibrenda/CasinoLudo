package com.grupo1.casinoludo.modelo

import java.io.Serializable

/**
 * Una apuesta del jugador.
 * [valor] guarda lo elegido tal cual se muestra: "17", "00", "Rojo", "Par", "2D".
 * Es Serializable para poder pasarla entre pantallas con un Intent.
 */
data class Apuesta(
    val tipo: TipoApuesta,
    val valor: String
) : Serializable {

    fun descripcion(): String = "${tipo.etiqueta}: $valor"
}
