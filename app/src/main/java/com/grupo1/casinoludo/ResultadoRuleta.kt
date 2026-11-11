package com.grupo1.casinoludo

// El número ganador con sus características.
// El color viene de la API; la paridad y la docena las calculamos nosotros.
data class ResultadoRuleta(
    val numero: String,
    val color: String,
    val paridad: String,
    val docena: String
)
