package com.grupo1.casinoludo

// Una apuesta del jugador: el tipo y lo que eligió ("17", "Rojo", "Par", "2D")
data class Apuesta(
    val tipo: TipoApuesta,
    val valor: String
) {
    // Texto para mostrar en pantalla, por ejemplo "Color: Rojo"
    fun descripcion(): String {
        val nombreTipo = when (tipo) {
            TipoApuesta.NUMERO -> "Número"
            TipoApuesta.COLOR -> "Color"
            TipoApuesta.PARIDAD -> "Paridad"
            TipoApuesta.DOCENA -> "Docena"
        }
        return "$nombreTipo: $valor"
    }
}
