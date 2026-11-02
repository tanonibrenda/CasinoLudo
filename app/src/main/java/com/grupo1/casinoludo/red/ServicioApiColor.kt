package com.grupo1.casinoludo.red

import com.google.gson.Gson
import com.grupo1.casinoludo.modelo.ColorRuleta
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.net.Inet4Address
import java.net.InetAddress
import java.util.concurrent.TimeUnit

/**
 * Endpoint de la API de la cátedra:
 * GET https://www.menupro.com.ar/api.php?usuario=usuario1&clave=1234567890&bola=7
 * Al ser suspend, Retrofit hace la llamada fuera del hilo principal (no hay ANR).
 */
interface ServicioApiColor {
    @GET("api.php")
    suspend fun obtenerColor(
        @Query("usuario") usuario: String = ClienteApi.USUARIO,
        @Query("clave") clave: String = ClienteApi.CLAVE,
        @Query("bola") bola: String
    ): Response<RespuestaColor>
}

/** Cliente Retrofit único (Singleton) para toda la app. */
object ClienteApi {

    private const val URL_BASE = "https://www.menupro.com.ar/"
    const val USUARIO = "usuario1"
    const val CLAVE = "1234567890"

    /**
     * Prueba primero las direcciones IPv4 del servidor.
     * El emulador de Android muchas veces no tiene salida por IPv6 y la conexión
     * se quedaba esperando hasta el timeout.
     */
    private val dnsPrimeroIpv4 = object : Dns {
        override fun lookup(hostname: String): List<InetAddress> =
            Dns.SYSTEM.lookup(hostname).sortedBy { if (it is Inet4Address) 0 else 1 }
    }

    val servicio: ServicioApiColor by lazy {
        // Muestra la petición y el JSON en el Logcat (filtrar por "okhttp")
        val logs = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val clienteHttp = OkHttpClient.Builder()
            .addInterceptor(logs)
            .dns(dnsPrimeroIpv4)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ServicioApiColor::class.java)
    }

    /**
     * Pide el color del número a la API y lo devuelve como ColorRuleta.
     * Si el servidor responde con error, lanza una excepción con el mensaje de la API.
     */
    suspend fun consultarColor(numero: String): ColorRuleta {
        val respuesta = servicio.obtenerColor(bola = numero)

        if (!respuesta.isSuccessful) {
            throw IllegalStateException(leerMensajeDeError(respuesta))
        }
        val cuerpo = respuesta.body() ?: throw IllegalStateException("La API respondió vacío")
        if (!cuerpo.error.isNullOrBlank()) {
            throw IllegalStateException(cuerpo.error)
        }
        return convertirColor(cuerpo.color)
    }

    private fun leerMensajeDeError(respuesta: Response<RespuestaColor>): String {
        val textoError = respuesta.errorBody()?.string()
        val mensajeApi = try {
            Gson().fromJson(textoError, RespuestaColor::class.java)?.error
        } catch (_: Exception) {
            null
        }
        return mensajeApi ?: "Error HTTP ${respuesta.code()}"
    }

    /** Convierte el texto de la API ("rojo", "NEGRO", "verde") a nuestro enum. */
    fun convertirColor(texto: String?): ColorRuleta =
        when (texto?.trim()?.lowercase()) {
            "rojo" -> ColorRuleta.ROJO
            "negro" -> ColorRuleta.NEGRO
            "verde" -> ColorRuleta.VERDE
            else -> throw IllegalStateException("La API devolvió un color desconocido: $texto")
        }
}
