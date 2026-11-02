package com.grupo1.casinoludo.logica

import com.grupo1.casinoludo.modelo.Apuesta
import com.grupo1.casinoludo.modelo.ColorRuleta
import com.grupo1.casinoludo.modelo.Docena
import com.grupo1.casinoludo.modelo.Paridad
import com.grupo1.casinoludo.modelo.ResultadoRuleta
import com.grupo1.casinoludo.modelo.TipoApuesta

/**
 * Reglas del juego. No depende de Android, así que se puede probar con tests comunes.
 */
object LogicaRuleta {

    const val CANTIDAD_APUESTAS = 3

    /** Todos los valores posibles de la ruleta americana: 00, 0, 1 ... 36 */
    val NUMEROS_RULETA: List<String> = listOf("00") + (0..36).map { it.toString() }

    private val FORMATO_NUMERO = Regex("^(00|0|[1-9]|[12][0-9]|3[0-6])$")

    fun esNumeroValido(texto: String): Boolean = FORMATO_NUMERO.matches(texto.trim())

    /** Valida una apuesta antes de agregarla a la lista. */
    fun esApuestaValida(apuesta: Apuesta): Boolean = when (apuesta.tipo) {
        TipoApuesta.NUMERO -> esNumeroValido(apuesta.valor)
        else -> apuesta.valor in apuesta.tipo.opciones
    }

    fun girar(): String = NUMEROS_RULETA.random()

    fun esCeroOdobleCero(numero: String): Boolean = numero == "0" || numero == "00"

    fun calcularParidad(numero: String): Paridad {
        if (esCeroOdobleCero(numero)) return Paridad.SIN_PARIDAD
        return if (numero.toInt() % 2 == 0) Paridad.PAR else Paridad.IMPAR
    }

    fun calcularDocena(numero: String): Docena {
        if (esCeroOdobleCero(numero)) return Docena.SIN_DOCENA
        return when (numero.toInt()) {
            in 1..12 -> Docena.PRIMERA
            in 13..24 -> Docena.SEGUNDA
            else -> Docena.TERCERA
        }
    }

    /** Arma el resultado completo: el color viene de la API, el resto se calcula acá. */
    fun armarResultado(numero: String, colorDeLaApi: ColorRuleta): ResultadoRuleta =
        ResultadoRuleta(
            numero = numero,
            color = colorDeLaApi,
            paridad = calcularParidad(numero),
            docena = calcularDocena(numero)
        )

    /** Compara una apuesta contra el número ganador según su tipo. */
    fun esAcierto(apuesta: Apuesta, resultado: ResultadoRuleta): Boolean = when (apuesta.tipo) {
        TipoApuesta.NUMERO -> apuesta.valor == resultado.numero
        TipoApuesta.COLOR -> apuesta.valor == resultado.color.etiqueta
        TipoApuesta.PARIDAD -> apuesta.valor == resultado.paridad.etiqueta
        TipoApuesta.DOCENA -> apuesta.valor == resultado.docena.etiqueta
    }

    fun contarAciertos(apuestas: List<Apuesta>, resultado: ResultadoRuleta): Int =
        apuestas.count { esAcierto(it, resultado) }
}
