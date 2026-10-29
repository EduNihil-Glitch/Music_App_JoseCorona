package com.example.music_app

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.music_app.model.Album
import com.example.music_app.model.ITunesResponse
import com.example.music_app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var tvResultado : TextView
    private lateinit var contenedorIndie : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home2)

        tvResultado = findViewById(R.id.tvResultado)
        contenedorIndie = findViewById(R.id.contenedorIndie)


        obtenerAlbum()

        //METODO PARA OBTENER LA RESPUESTA DEL API



    }
    fun obtenerAlbum(){
        RetrofitClient.api.obtenerAlbumGenero(

            genero = "edm",
            pais = "MX",
            media = "music",
            entidad = "album",
            limite = 15

        ).enqueue(object : Callback<ITunesResponse>{

            //METODO EN CASO DE OBTENER UNA RESPUESTA EXITOSA
            override fun onResponse(call: Call<ITunesResponse?>, response: Response<ITunesResponse?>) {

                //Variable que obtenga toda la respuesta del API
                val albumes = response.body()?.results ?: emptyList()

                if(albumes.isNotEmpty()){

                    mostrarAlbumes(albumes)

                } else {
                    Toast.makeText(this@HomeActivity, "No hay resultados", Toast.LENGTH_SHORT).show()
                }

            }

            //METODO EN CASO DE OBTENER UNA RESPUESTA FALLIDA
            override fun onFailure(call: Call<ITunesResponse?>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Error en el API", Toast.LENGTH_SHORT).show()
            }

        })
    }

    //Metodo para mostrar una lista de album
    private fun mostrarAlbumes( albumes : List<Album> ){

        //eliminar los elementos duplicados
        contenedorIndie.removeAllViews()

        //Ciclo que recorre los resultados del album
        for(album in albumes){

            //Crear variable que haga uso de las tarjetas (item_album)
            val vistaAlbum = layoutInflater.inflate(
                R.layout.item_album,
                contenedorIndie,
                false
            )


            //Obtener la informacion del API
            val imgPortada = vistaAlbum.findViewById<ImageView>(R.id.imgPortadaAlbum)
            val tvNombreAlbum = vistaAlbum.findViewById<TextView>(R.id.tvNombreAlbum)
            val tvNombreArtista = vistaAlbum.findViewById<TextView>(R.id.tvNombreArtista)

            tvNombreAlbum.text = album.collectionName ?: "Album sin nombre"
            tvNombreArtista.text = album.artistName ?: "Artista desconocido"
            Glide.with(this)
                .load(album.obtenerImagenGrande())
                .into(imgPortada)

            contenedorIndie.addView(vistaAlbum)

        }

    }
}