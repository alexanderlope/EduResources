package com.edu.resources.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import kotlin.math.roundToInt

interface RecursosService {
    @GET("recursos")
    suspend fun getAll(): List<Recurso>

    @POST("recursos")
    suspend fun create(@Body body: RecursoNuevo): Recurso

    @PUT("recursos/{id}")
    suspend fun update(@Path("id") id: String, @Body body: RecursoInput): Recurso

    @PUT("recursos/{id}")
    suspend fun rate(@Path("id") id: String, @Body body: RatingUpdate): Recurso

    @DELETE("recursos/{id}")
    suspend fun delete(@Path("id") id: String): Recurso
}

object Api {
    // URL COMPLETA del recurso en MockAPI, tal como la muestra el sitio (con o sin "/" final).
    private const val BASE_URL = "https://6ae3d032933120ae20f933ac.mockapi.io/recursos/"

    val service: RecursosService by lazy {
        Retrofit.Builder()
            .baseUrl(if (BASE_URL.endsWith("/")) BASE_URL else "$BASE_URL/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecursosService::class.java)
    }

    /** Calcula el nuevo promedio (1 decimal) igual que la versión Expo. */
    fun buildRating(current: List<Int>?, rating: Int): RatingUpdate {
        val ratings = (current ?: emptyList()) + rating
        val promedio = ratings.sum().toDouble() / ratings.size
        return RatingUpdate(ratings, (promedio * 10).roundToInt() / 10.0)
    }
}
