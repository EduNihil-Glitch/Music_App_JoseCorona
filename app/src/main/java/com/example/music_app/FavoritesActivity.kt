package com.example.music_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.music_app.model.AlbumFavorito
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {

    private lateinit var bottomNavegation : BottomNavigationView

    private lateinit var auth : FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    private lateinit var tvResultado : TextView
    private lateinit var gridFavoritos : GridLayout
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favorites)

        bottomNavegation = findViewById(R.id.bottomNavigation)

        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        tvResultado = findViewById(R.id.tvResultado)
        gridFavoritos = findViewById(R.id.gridFavoritos)
        dbHelper = DatabaseHelper(this)

        menuNavegacion()
        consultarFavoritos()

    }

    //Actualizacion inmediata de la pantalla
    override fun onResume() {
        super.onResume()
        consultarFavoritos()
    }

    //Metodo para usar el menu de navegacion
    private fun menuNavegacion(){

        //Indicar cual es el  item seleccionado
        bottomNavegation.selectedItemId = R.id.nav_favoritos

        //indicar el item al que se ira el usuario
        bottomNavegation.setOnItemSelectedListener { item->

            when(item.itemId){

                R.id.nav_inicio -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_favoritos -> {
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
                Toast.makeText(this@FavoritesActivity, "Error al cerrar sesion", Toast.LENGTH_SHORT).show()
            }

            val intent = Intent(this@FavoritesActivity, MainActivity::class.java)

            //Indicar que se borre toda la info y la aplicacion comience desde 0
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

        }

    }

    //Metodo para mostrar los albumes
    private fun mostrarAlbumesFavoritos(favoritos : List<AlbumFavorito>){

        for(album in favoritos){

            val vistaAlbum = layoutInflater.inflate(
                R.layout.item_album_favorito,
                gridFavoritos,
                false
            )

            val imagen = vistaAlbum.findViewById<ImageView>(R.id.imgFavoritoAlbum)

            Glide.with(this)
                .load(album.imagen)
                .centerCrop()
                .into(imagen)

            vistaAlbum.setOnClickListener {

                val intent = Intent(this, DetailsActivity::class.java)

                intent.putExtra("ID_ALBUM", album.albumId)
                intent.putExtra("NOMBRE_ALBUM", album.nombreAlbum)
                intent.putExtra("ARTISTA_ALBUM", album.artista)
                intent.putExtra("GENERO_ALBUM", album.genero)
                intent.putExtra("CANCIONES_ALBUM", album.canciones)
                intent.putExtra("IMAGEN_ALBUM", album.imagen)

                startActivity(intent)

            }

            gridFavoritos.addView(vistaAlbum)

        }

    }

    //Metodo de consulta de informacion
    private fun consultarFavoritos(){

        val favoritos = dbHelper.obtenerAlbumesFavoritos()

        //Eliminar las vistas repetidas
        gridFavoritos.removeAllViews()

        if(favoritos.isEmpty()){
            tvResultado.text = "No tienes favoritos, agrega uno :´("
            tvResultado.visibility = View.VISIBLE

        } else {
            tvResultado.visibility = View.GONE
            mostrarAlbumesFavoritos(favoritos)
        }

    }
}