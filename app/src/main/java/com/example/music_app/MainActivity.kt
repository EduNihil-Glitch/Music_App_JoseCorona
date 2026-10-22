package com.example.music_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var etCorreo : EditText
    private lateinit var etPassword : EditText
    private lateinit var btnIniciar : Button

    private lateinit var dbHelper: DatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val tvCrearCuenta = findViewById<TextView>(R.id.tvCrearCuenta)

        etCorreo = findViewById(R.id.etCorreo)
        etPassword = findViewById(R.id.etPassword)
        btnIniciar = findViewById(R.id.btnIniciar)

        dbHelper = DatabaseHelper(this)

        //CREAR EVENTO PARA INICIAR SESION
        btnIniciar.setOnClickListener {
            //Todo : agregar el metodo de iniciar secion
            iniciarSesion()
        }

        //Crear evento para ir a otra pantalla
        tvCrearCuenta.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    //Metodo para iniciar secion
    private fun iniciarSesion(){

        val correo = etCorreo.text.toString().trim()
        val pass = etPassword.text.toString().trim()

        if(correo.isEmpty() || pass.isEmpty()){
            Toast.makeText(this,"Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val resultado = dbHelper.iniciarSesion(correo,pass)

        if(resultado ){
            Toast.makeText(this,"Bienvenido", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)

            finish()

        } else {
            Toast.makeText(this,"Error al iniciar sesion", Toast.LENGTH_SHORT).show()
        }

    }

}