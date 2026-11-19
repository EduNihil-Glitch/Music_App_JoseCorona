package com.example.music_app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context : Context) : SQLiteOpenHelper(

    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION

) {

    companion object{
        private const val DATABASE_NAME = "musica.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_USUARIOS = "usuarios"
        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_CORREO = "correo"
        private const val COL_PASSWORD = "password"
        private const val TABLE_FAVORITOS = "favoritos"
        private const val COL_ID_FAVORITO = "id"
        private const val COL_ID_ALBUM = "album_id"
        private const val COL_NOMBRE_ALBUM = "nombre_album"
        private const val COL_ARTISTA = "artista"
        private const val COL_GENERO = "genero"
        private const val COL_CANCIONES = "canciones"
        private const val COL_IMAGEN = "imagen"
        private const val CREATE_TABLE_FAVORITOS = """
            CREATE TABLE $TABLE_FAVORITOS (
            $COL_ID_FAVORITO INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_ID_ALBUM INTEGER UNIQUE NOT NULL,
            $COL_NOMBRE_ALBUM TEXT NOT NULL,
            $COL_ARTISTA TEXT,
            $COL_GENERO TEXT,
            $COL_CANCIONES INTEGER,
            $COL_IMAGEN TEXT
            )
        """

        private const val  CREATE_TABLE_USUARIOS = """
            CREATE TABLE $TABLE_USUARIOS(
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_CORREO TEXT NOT NULL UNIQUE,
                $COL_PASSWORD TEXT NOT NULL
            )
        """

    }

    //METODO PARA CREAR LA BASE DE DATOS Y SUS TABLAS
    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL(CREATE_TABLE_USUARIOS)
        db?.execSQL(CREATE_TABLE_FAVORITOS)

    }

    //METODO PARA ACTUALIZAR LA BASE DE DATOS
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //Validar la version actual de la BD
        if(oldVersion < 2){
            db?.execSQL(CREATE_TABLE_FAVORITOS)
        }
    }

    //metodo para registrar un usuario
    fun registrarUsuario(nombre : String, correo : String, password : String) : Boolean{
        //CREAR VARIABLE CON PERMISOS DE ESCRIBIR EN LA DB
        val db = writableDatabase

        //CREAR VARIABLE PARA GUARDAR LOS DATOS
        val datos = ContentValues().apply{
            put(COL_NOMBRE, nombre)
            put(COL_CORREO, correo)
            put(COL_PASSWORD, password)
        }

        val resultado = db.insert(TABLE_USUARIOS, null, datos)

        return resultado != -1L


    }

    //METODO PARA LA CONSULTA DE USUARIOS
    fun iniciarSesion(correo: String, password: String) : Boolean{
        val db = readableDatabase

        val query_sql = """
            SELECT * FROM $TABLE_USUARIOS
            WHERE $COL_CORREO = ? AND $COL_PASSWORD = ?
            """.trimIndent()

        val cursor = db.rawQuery(
            query_sql, arrayOf(correo, password)
        )

        //Contar el numero de resultados
        val usuarioEncontrado = cursor.count > 0

        cursor.close()
        db.close()

        return usuarioEncontrado

    }

    //METODO PARA INSERTAR UN REGISTRO DESDE EL API
    fun guardarAlbumFavorito(idAlbum : Long, nombreAlbum : String, artista : String, genero : String, canciones : Int, imagen : String) : Boolean{

        val db = writableDatabase

        val datos = ContentValues().apply {

            put(COL_ID_ALBUM, idAlbum)
            put(COL_NOMBRE_ALBUM, nombreAlbum)
            put(COL_ARTISTA, artista)
            put(COL_GENERO, genero)
            put(COL_CANCIONES, canciones)
            put(COL_IMAGEN, imagen)

        }

        val resultado = db.insertWithOnConflict(
            TABLE_FAVORITOS,
            null,
            datos,
            SQLiteDatabase.CONFLICT_IGNORE
        )

        return resultado != -1L

    }

    //METODO PARA ELIMINAR UN REGISTRO
    fun eliminarAlbumFavorito(idAlbum : Long) : Boolean {

        val db = writableDatabase
        val resultado = db.delete(
            TABLE_FAVORITOS,
            "$COL_ID_ALBUM = ?",
            arrayOf(idAlbum.toString())
        )

        return resultado > 0

    }

    //METODO PARA CONSULTAR LA INFORMACION DEL API
    fun existeAlbumFavorito(idAlbum : Long) : Boolean {

        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COL_ID_FAVORITO FROM $TABLE_FAVORITOS WHERE $COL_ID_ALBUM = ?",
            arrayOf(idAlbum.toString())
        )

        val existe = cursor.moveToFirst()

        cursor.close()

        return existe 

    }

}