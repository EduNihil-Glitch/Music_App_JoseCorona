package com.example.music_app.model

//ARCHIVO PARA DETECTAR LAS RESPUESTAS DEL API

data class ITunesResponse(

    val resultCount : Int,
    val results : List<Album>

)
