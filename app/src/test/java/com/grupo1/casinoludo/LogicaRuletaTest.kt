package com.grupo1.casinoludo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LogicaRuletaTest {

    @Test
    fun laRuletaTiene38Numeros() {
        assertEquals(38, LogicaRuleta.numeros.size)
        for (i in 1..200) {
            assertTrue(LogicaRuleta.girar() in LogicaRuleta.numeros)
        }
    }

    @Test
    fun validaElNumeroIngresado() {
        for (valido in listOf("00", "0", "7", "19", "36")) {
            assertTrue(valido, LogicaRuleta.esNumeroValido(valido))
        }
        for (invalido in listOf("", "37", "-1", "000", "05", "abc", "1.5")) {
            assertFalse(invalido, LogicaRuleta.esNumeroValido(invalido))
        }
    }

    @Test
    fun ceroYDobleCeroNoTienenParidadNiDocena() {
        for (n in listOf("0", "00")) {
            assertEquals("Sin paridad", LogicaRuleta.calcularParidad(n))
            assertEquals("Sin docena", LogicaRuleta.calcularDocena(n))
        }
    }

    @Test
    fun calculaParidadYDocena() {
        assertEquals("Par", LogicaRuleta.calcularParidad("12"))
        assertEquals("Impar", LogicaRuleta.calcularParidad("13"))
        assertEquals("1D", LogicaRuleta.calcularDocena("12"))
        assertEquals("2D", LogicaRuleta.calcularDocena("13"))
        assertEquals("2D", LogicaRuleta.calcularDocena("24"))
        assertEquals("3D", LogicaRuleta.calcularDocena("25"))
    }

    @Test
    fun comparaLasTresApuestasContraElGanador() {
        val ganador = LogicaRuleta.armarResultado("14", "rojo")
        val apuestas = listOf(
            Apuesta(TipoApuesta.COLOR, "Rojo"),     // acierta
            Apuesta(TipoApuesta.PARIDAD, "Impar"),  // no acierta, 14 es par
            Apuesta(TipoApuesta.DOCENA, "2D")       // acierta
        )
        assertEquals(2, LogicaRuleta.contarAciertos(apuestas, ganador))
    }

    @Test
    fun conCeroSoloGanaQuienApostoAlCero() {
        val ganador = LogicaRuleta.armarResultado("0", "verde")
        assertTrue(LogicaRuleta.esAcierto(Apuesta(TipoApuesta.NUMERO, "0"), ganador))
        assertFalse(LogicaRuleta.esAcierto(Apuesta(TipoApuesta.NUMERO, "00"), ganador))
        assertFalse(LogicaRuleta.esAcierto(Apuesta(TipoApuesta.PARIDAD, "Par"), ganador))
        assertFalse(LogicaRuleta.esAcierto(Apuesta(TipoApuesta.DOCENA, "1D"), ganador))
        assertFalse(LogicaRuleta.esAcierto(Apuesta(TipoApuesta.COLOR, "Negro"), ganador))
    }
}
