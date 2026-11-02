package com.grupo1.casinoludo.modelo

/**
 * Los cuatro tipos de apuesta que admite la mesa.
 * [opciones] son los valores que el usuario puede elegir (vacío para NUMERO,
 * porque el número se escribe en un campo de texto).
 */
enum class TipoApuesta(val etiqueta: String, val opciones: List<String>) {
    NUMERO("Número", emptyList()),
    COLOR("Color", listOf("Rojo", "Negro")),
    PARIDAD("Paridad", listOf("Par", "Impar")),
    DOCENA("Docena", listOf("1D", "2D", "3D"))
}
