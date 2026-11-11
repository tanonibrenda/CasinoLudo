package com.grupo1.casinoludo

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 * Capa de red, armada igual que en la guía de la cátedra (RuletaApi.kt).
 * Ejemplo de respuesta: {"bola": 7, "color": "rojo", "error": null}
 * La bola se maneja como texto para poder mandar también el "00".
 */
data class RuletaResponse(
    @SerializedName("bola") val bola: String? = null,
    @SerializedName("color") val color: String? = null,
    @SerializedName("error") val error: String? = null
)

interface RuletaApiService {
    // GET https://www.menupro.com.ar/api.php?usuario=usuario1&clave=1234567890&bola=7
    @GET("api.php")
    suspend fun getBolaColor(
        @Query("usuario") usuario: String = "usuario1",
        @Query("clave") clave: String = "1234567890",
        @Query("bola") bola: String
    ): Response<RuletaResponse>
}

object RetrofitClient {
    private const val BASE_URL = "https://www.menupro.com.ar/"

    val apiService: RuletaApiService by lazy {
        // Muestra la petición y el JSON en el Logcat (filtrar por "okhttp")
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RuletaApiService::class.java)
    }
}
