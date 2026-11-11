package com.grupo1.casinoludo

// Reglas del juego (no usa nada de Android, por eso se puede probar con tests)
object LogicaRuleta {

    const val CANTIDAD_APUESTAS = 3

    // La ruleta americana tiene 38 casilleros: 00, 0 y del 1 al 36
    val numeros = listOf("00") + (0..36).map { it.toString() }

    fun girar(): String {
        return numeros.random()
    }

    // Acepta solo 00, 0 o del 1 al 36 (no acepta "05", "37", letras, etc.)
    fun esNumeroValido(texto: String): Boolean {
        if (texto == "00") return true
        if (texto.length > 1 && texto.startsWith("0")) return false
        val numero = texto.toIntOrNull() ?: return false
        return numero in 0..36
    }

    fun esCero(numero: String): Boolean {
        return numero == "0" || numero == "00"
    }

    // El 0 y el 00 no son ni par ni impar
    fun calcularParidad(numero: String): String {
        if (esCero(numero)) return "Sin paridad"
        return if (numero.toInt() % 2 == 0) "Par" else "Impar"
    }

    // El 0 y el 00 no pertenecen a ninguna docena
    fun calcularDocena(numero: String): String {
        if (esCero(numero)) return "Sin docena"
        return when (numero.toInt()) {
            in 1..12 -> "1D"
            in 13..24 -> "2D"
            else -> "3D"
        }
    }

    // Arma el resultado con el color de la API y la paridad y docena calculadas acá
    fun armarResultado(numero: String, colorApi: String): ResultadoRuleta {
        val color = colorApi.lowercase().replaceFirstChar { it.uppercase() }  // "rojo" -> "Rojo"
        return ResultadoRuleta(numero, color, calcularParidad(numero), calcularDocena(numero))
    }

    // Compara una apuesta con el número ganador según el tipo de apuesta
    fun esAcierto(apuesta: Apuesta, resultado: ResultadoRuleta): Boolean {
        return when (apuesta.tipo) {
            TipoApuesta.NUMERO -> apuesta.valor == resultado.numero
            TipoApuesta.COLOR -> apuesta.valor == resultado.color
            TipoApuesta.PARIDAD -> apuesta.valor == resultado.paridad
            TipoApuesta.DOCENA -> apuesta.valor == resultado.docena
        }
    }

    fun contarAciertos(apuestas: List<Apuesta>, resultado: ResultadoRuleta): Int {
        var aciertos = 0
        for (apuesta in apuestas) {
            if (esAcierto(apuesta, resultado)) aciertos++
        }
        return aciertos
    }
}
