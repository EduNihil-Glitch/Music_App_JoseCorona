package com.example.music_app.network

import com.example.music_app.model.ITunesResponse
import retrofit2.http.GET
import retrofit2.http.Query

//ARCHIVO PARA CONEXIÓN CON INTERNET

interface ITunesApi {

    @GET("search")

    //TODO: Agregar parametros del enalace
    //METODO PARA CREAR EL ENLACE DEL API
    fun obtenerAlbumGenero(

        @Query("term") genero : String,
        @Query("country") pais : String,
        @Query("media") media : String,
        @Query("entity") entidad : String,
        @Query("limit") limite : Int,

    ) : retrofit2.Call<ITunesResponse>

}