package com.grupo1.casinoludo.modelo

import java.io.Serializable

enum class ColorRuleta(val etiqueta: String) {
    ROJO("Rojo"),
    NEGRO("Negro"),
    VERDE("Verde")
}

enum class Paridad(val etiqueta: String) {
    PAR("Par"),
    IMPAR("Impar"),
    SIN_PARIDAD("Sin paridad")   // 0 y 00
}

enum class Docena(val etiqueta: String) {
    PRIMERA("1D"),
    SEGUNDA("2D"),
    TERCERA("3D"),
    SIN_DOCENA("Sin docena")     // 0 y 00
}

/**
 * El número ganador con todas sus características.
 * El color llega de la API; paridad y docena se calculan en LogicaRuleta.
 */
data class ResultadoRuleta(
    val numero: String,
    val color: ColorRuleta,
    val paridad: Paridad,
    val docena: Docena
) : Serializable
