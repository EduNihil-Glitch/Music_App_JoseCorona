package com.example.music_app

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

class DetailsActivity : AppCompatActivity() {
    private lateinit var imgPortadaDetalle : ImageView
    private lateinit var tvNombreAlbumDetalle : TextView
    private lateinit var tvArtistaAlbumDetalle : TextView
    private lateinit var tvAlbumDetalle : TextView
    private lateinit var btnFavorito : ImageButton

    private lateinit var dbHelper: DatabaseHelper
    private var albumFavorito = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_details)

        imgPortadaDetalle = findViewById(R.id.imgPortadaDetalle)
        tvNombreAlbumDetalle = findViewById(R.id.tvNombreAlbumDetalle)
        tvArtistaAlbumDetalle = findViewById(R.id.tvArtistaAlbumDetalle)
        tvAlbumDetalle = findViewById(R.id.tvAlbumDetalle)
        btnFavorito = findViewById(R.id.btnFavorito)

        dbHelper = DatabaseHelper(this)

        mostrarInformacionAlbum()

    }

    //METODO PARA OBTENER LA INFORMACIÓN QUE VIENE DEL API
    private fun mostrarInformacionAlbum(){
        val idAlbum = intent.getLongExtra("ID_ALBUM",1)
        val nombreAlbum = intent.getStringExtra("NOMBRE_ALBUM")?:"Album desconocido"
        val artistaAlbum = intent.getStringExtra("ARTISTA_ALBUM")?:"Artista desconocido"
        val generoAlbum = intent.getStringExtra("GENERO_ALBUM")?:"Sin genero"
        val cancionesAlbum = intent.getIntExtra("CANCIONES_ALBUM",1)
        val imagenAlbum = intent.getStringExtra("IMAGEN_ALBUM")?:""

        tvNombreAlbumDetalle.text = nombreAlbum
        tvArtistaAlbumDetalle.text = artistaAlbum
        tvAlbumDetalle.text = generoAlbum + " - " + "$cancionesAlbum canciones"

        Glide.with(this)
            .load(imagenAlbum)
            .centerCrop()
            .into(imgPortadaDetalle)

        //Verificar que el album se encuentre como favorito
        if (idAlbum != -1L){
            dbHelper.existeAlbumFavorito(idAlbum)
        }

        albumFavorito = dbHelper.existeAlbumFavorito(idAlbum)
        actualizarIcono()

        btnFavorito.setOnClickListener {
            //albumFavorito = !albumFavorito

            if(albumFavorito){
                val eliminado = dbHelper.eliminarAlbumFavorito(idAlbum)
                if (eliminado){
                    albumFavorito = !albumFavorito
                    Toast.makeText(this, "Album $nombreAlbum eliminado de favoritos", Toast.LENGTH_SHORT).show()
                }


            } else {
                val guardado = dbHelper.guardarAlbumFavorito(
                    idAlbum = idAlbum,
                    nombreAlbum = nombreAlbum,
                    artista = artistaAlbum,
                    genero = generoAlbum,
                    canciones = cancionesAlbum,
                    imagen = imagenAlbum
                )
                if(guardado){
                    albumFavorito = !albumFavorito
                    Toast.makeText(this, "Album $nombreAlbum agregado a favoritos", Toast.LENGTH_SHORT).show()
                }

            }
            actualizarIcono()
        }


    }
    private fun actualizarIcono(){
        if(albumFavorito){
            btnFavorito.setImageResource(R.drawable.favorito)
        }else {
            btnFavorito.setImageResource(R.drawable.no_favorito)
        }
    }

}