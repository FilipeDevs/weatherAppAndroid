package com.g58093.remise_2.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface AuthApiService {
    @Headers(
        "Content-Type: application/json",
        "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRuc3Jpdm54bGVlcWR0YnloZnR2Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTY5NzE0MDI2MSwiZXhwIjoyMDEyNzE2MjYxfQ.jgJ49-c9Z8iPQnLVTnPlfRZpKwyBKht-OY8wMTceSiM"
    )
    @POST("/auth/v1/token?grant_type=password")
    fun authenticate(@Body authRequest: AuthRequest): Call<Void>
}


object AuthApi {
    private const val BASE_URL = "https://dnsrivnxleeqdtbyhftv.supabase.co"

    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(BASE_URL)
        .build()
}

