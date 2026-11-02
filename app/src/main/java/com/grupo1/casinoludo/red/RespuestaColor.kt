package com.grupo1.casinoludo.red

import com.google.gson.annotations.SerializedName

/**
 * Respuesta JSON de la API de la cátedra.
 * Ejemplo: {"bola": 7, "color": "rojo", "error": null}
 * La bola se lee como texto para poder representar también el "00".
 */
data class RespuestaColor(
    @SerializedName("bola") val bola: String? = null,
    @SerializedName("color") val color: String? = null,
    @SerializedName("error") val error: String? = null
)
