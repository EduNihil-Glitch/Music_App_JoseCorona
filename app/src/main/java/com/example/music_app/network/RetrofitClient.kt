package com.example.music_app.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//ARCHIVO QUE CONSTRUYE EL URL Y CONECTA LA API

object RetrofitClient {

    private const val BASE_URL = "https://itunes.apple.com/"

    //VARIABLE QUE CONSTRUYE LA PETICION
    val api : ITunesApi by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)

    }
}