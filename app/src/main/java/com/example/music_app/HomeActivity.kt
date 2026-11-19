package com.example.music_app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.music_app.model.Album
import com.example.music_app.model.ITunesResponse
import com.example.music_app.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var tvResultado : TextView
    private lateinit var contenedorIndie : LinearLayout
    private lateinit var contenedorPop : LinearLayout
    private lateinit var contenedorDaftPunk : LinearLayout
    private lateinit var bottomNavegation : BottomNavigationView

    private lateinit var auth : FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home2)

        tvResultado = findViewById(R.id.tvResultado)
        contenedorIndie = findViewById(R.id.contenedorIndie)
        contenedorPop = findViewById(R.id.contenedorPop)
        contenedorDaftPunk = findViewById(R.id.contenedorDaftPunk)
        bottomNavegation = findViewById(R.id.bottomNavigation)

        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        //METODO PARA OBTENER LA RESPUESTA DEL API
        obtenerAlbum("indie", contenedorIndie)
        obtenerAlbum("kpop", contenedorPop)
        obtenerAlbum("daft punk", contenedorDaftPunk)

        menuNavegacion()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }
    fun obtenerAlbum(genero : String, contenedor: LinearLayout){
        RetrofitClient.api.obtenerAlbumGenero(

            genero = genero,
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

                    mostrarAlbumes(albumes,contenedor)

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
    private fun mostrarAlbumes( albumes : List<Album>, contenedor : LinearLayout ){

        //eliminar los elementos duplicados
        contenedor.removeAllViews()

        //Ciclo que recorre los resultados del album
        for(album in albumes){

            //Crear variable que haga uso de las tarjetas (item_album)
            val vistaAlbum = layoutInflater.inflate(
                R.layout.item_album,
                contenedor,
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

            //Pasar informacion a otra pantalla
            vistaAlbum.setOnClickListener {

                val idAlbum = album.collectionId

                if(idAlbum != null){
                    val intent = Intent(this@HomeActivity, DetailsActivity::class.java)
                    intent.putExtra("ID_ALBUM", idAlbum)
                    intent.putExtra("NOMBRE_ALBUM", album.collectionName)
                    intent.putExtra("ARTISTA_ALBUM", album.artistName)
                    intent.putExtra("GENERO_ALBUM", album.primaryGenreName)
                    intent.putExtra("CANCIONES_ALBUM", album.trackCount)
                    intent.putExtra("IMAGEN_ALBUM", album.obtenerImagenGrande())

                    startActivity(intent)

                }

            }

            contenedor.addView(vistaAlbum)

        }

    }

    //Metodo para usar el menu de navegacion
    private fun menuNavegacion(){

        //Indicar cual es el  item seleccionado
        bottomNavegation.selectedItemId = R.id.nav_inicio

        //indicar el item al que se ira el usuario
        bottomNavegation.setOnItemSelectedListener { item->

            when(item.itemId){

                R.id.nav_inicio -> {
                    true
                }

                R.id.nav_favoritos -> {
                    val intent = Intent(this, FavoritesActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_salir -> {
                    cerrasSesionGoogle()
                    true
                }

                else -> false

            }

        }

    }
    //Metodo para cerrar sesion
    private fun cerrasSesionGoogle(){

        auth.signOut()

        lifecycleScope.launch {

            try {

                credentialManager.clearCredentialState(
                    androidx.credentials.ClearCredentialStateRequest()
                )

            } catch (e : Exception){
                Toast.makeText(this@HomeActivity, "Error al cerrar sesion", Toast.LENGTH_SHORT).show()
            }

            val intent = Intent(this@HomeActivity, MainActivity::class.java)

            //Indicar que se borre toda la info y la aplicacion comience desde 0
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

        }

    }

}