package com.grupo1.casinoludo

import com.grupo1.casinoludo.logica.LogicaRuleta
import com.grupo1.casinoludo.modelo.Apuesta
import com.grupo1.casinoludo.modelo.ColorRuleta
import com.grupo1.casinoludo.modelo.Docena
import com.grupo1.casinoludo.modelo.Paridad
import com.grupo1.casinoludo.modelo.TipoApuesta
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LogicaRuletaTest {

    @Test
    fun laRuletaTiene38Numeros() {
        assertEquals(38, LogicaRuleta.NUMEROS_RULETA.size)
        repeat(200) { assertTrue(LogicaRuleta.girar() in LogicaRuleta.NUMEROS_RULETA) }
    }

    @Test
    fun validaFormatoDelNumero() {
        listOf("00", "0", "7", "19", "36").forEach { assertTrue(it, LogicaRuleta.esNumeroValido(it)) }
        listOf("", "37", "-1", "000", "05", "abc", "1.5").forEach { assertFalse(it, LogicaRuleta.esNumeroValido(it)) }
    }

    @Test
    fun ceroYDobleCeroNoTienenParidadNiDocena() {
        for (n in listOf("0", "00")) {
            assertEquals(Paridad.SIN_PARIDAD, LogicaRuleta.calcularParidad(n))
            assertEquals(Docena.SIN_DOCENA, LogicaRuleta.calcularDocena(n))
        }
    }

    @Test
    fun calculaParidadYDocena() {
        assertEquals(Paridad.PAR, LogicaRuleta.calcularParidad("12"))
        assertEquals(Paridad.IMPAR, LogicaRuleta.calcularParidad("13"))
        assertEquals(Docena.PRIMERA, LogicaRuleta.calcularDocena("12"))
        assertEquals(Docena.SEGUNDA, LogicaRuleta.calcularDocena("13"))
        assertEquals(Docena.SEGUNDA, LogicaRuleta.calcularDocena("24"))
        assertEquals(Docena.TERCERA, LogicaRuleta.calcularDocena("25"))
    }

    @Test
    fun comparaLasTresApuestasContraElGanador() {
        val ganador = LogicaRuleta.armarResultado("14", ColorRuleta.ROJO)
        val apuestas = listOf(
            Apuesta(TipoApuesta.COLOR, "Rojo"),     // acierta
            Apuesta(TipoApuesta.PARIDAD, "Impar"),  // no acierta (14 es par)
            Apuesta(TipoApuesta.DOCENA, "2D")       // acierta
        )
        assertEquals(2, LogicaRuleta.contarAciertos(apuestas, ganador))
    }

    @Test
    fun conCeroSoloAciertaElPlenoAlCero() {
        val ganador = LogicaRuleta.armarResultado("0", ColorRuleta.VERDE)
        assertTrue(LogicaRuleta.esAcierto(Apuesta(TipoApuesta.NUMERO, "0"), ganador))
        assertFalse(LogicaRuleta.esAcierto(Apuesta(TipoApuesta.NUMERO, "00"), ganador))
        assertFalse(LogicaRuleta.esAcierto(Apuesta(TipoApuesta.PARIDAD, "Par"), ganador))
        assertFalse(LogicaRuleta.esAcierto(Apuesta(TipoApuesta.DOCENA, "1D"), ganador))
        assertFalse(LogicaRuleta.esAcierto(Apuesta(TipoApuesta.COLOR, "Negro"), ganador))
    }
}
