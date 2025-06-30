package com.example.cargarimagen.io

import com.example.cargarimagen.io.response.PredictResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface APIService {
    @Multipart
    @POST("clasificar-planta")
    fun subirImagen(
        @Part imagen: MultipartBody.Part
    ): Call<PredictResponse>

    companion object Factory {
        private const val BASE_URL = "http://148.204.142.3:3003/"
        fun create(): APIService{
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(APIService::class.java)
        }
    }
}