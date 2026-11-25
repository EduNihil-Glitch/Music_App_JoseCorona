package com.example.music_app.model

data class AlbumFavorito(

    val albumId : Long,
    val nombreAlbum : String,
    val artista : String,
    val genero : String,
    val canciones : Int,
    val imagen : String

)
