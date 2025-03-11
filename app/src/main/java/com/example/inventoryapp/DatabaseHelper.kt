package com.example.inventoryapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Clase para gestionar la base de datos SQLite de la aplicación.
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MoviesDB" // Nombre de la base de datos
        private const val DATABASE_VERSION = 1 // Versión de la base de datos

        // Constantes para la tabla de películas
        private const val TABLE_MOVIES = "movies"
        private const val MOVIE_ID = "id"
        private const val MOVIE_TITLE = "title"
        private const val MOVIE_YEAR = "year"
        private const val MOVIE_DIRECTOR_ID = "director_id"

        // Constantes para la tabla de directores
        private const val TABLE_DIRECTORS = "directors"
        private const val DIRECTOR_ID = "id"
        private const val DIRECTOR_NAME = "name"
        private const val DIRECTOR_NATIONALITY = "nationality"
        private const val DIRECTOR_BIRTH_YEAR = "birth_year"

        // Constantes para la tabla de actores
        private const val TABLE_ACTORS = "actors"
        private const val ACTOR_ID = "id"
        private const val ACTOR_NAME = "name"
        private const val ACTOR_NATIONALITY = "nationality"
        private const val ACTOR_BIRTH_YEAR = "birth_year"

        // Constantes para la tabla de relación películas-actores
        private const val TABLE_MOVIES_ACTORS = "movies_actors"
        private const val MA_MOVIE_ID = "movie_id"
        private const val MA_ACTOR_ID = "actor_id"
    }

    /**
     * Crea las tablas de la base de datos cuando se inicializa por primera vez.
     *
     * @param db La base de datos SQLite.
     */
    override fun onCreate(db: SQLiteDatabase) {
        // Creamos la tabla de películas
        db.execSQL("CREATE TABLE $TABLE_MOVIES (" +
                "$MOVIE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$MOVIE_TITLE TEXT," +
                "$MOVIE_YEAR INTEGER," +
                "$MOVIE_DIRECTOR_ID INTEGER," +
                "FOREIGN KEY($MOVIE_DIRECTOR_ID) REFERENCES $TABLE_DIRECTORS($DIRECTOR_ID))")

        // Creamos la tabla de directores
        db.execSQL("CREATE TABLE $TABLE_DIRECTORS (" +
                "$DIRECTOR_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$DIRECTOR_NAME TEXT," +
                "$DIRECTOR_NATIONALITY TEXT," +
                "$DIRECTOR_BIRTH_YEAR INTEGER)")

        // Creamos la tabla de actores
        db.execSQL("CREATE TABLE $TABLE_ACTORS (" +
                "$ACTOR_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$ACTOR_NAME TEXT," +
                "$ACTOR_NATIONALITY TEXT," +
                "$ACTOR_BIRTH_YEAR INTEGER)")

        // Creamos la tabla de relación entre películas y actores
        db.execSQL("CREATE TABLE $TABLE_MOVIES_ACTORS (" +
                "$MA_MOVIE_ID INTEGER," +
                "$MA_ACTOR_ID INTEGER," +
                "FOREIGN KEY($MA_MOVIE_ID) REFERENCES $TABLE_MOVIES($MOVIE_ID)," +
                "FOREIGN KEY($MA_ACTOR_ID) REFERENCES $TABLE_ACTORS($ACTOR_ID)," +
                "PRIMARY KEY($MA_MOVIE_ID, $MA_ACTOR_ID))")

        // Llenamos la base de datos con datos iniciales
        initializeData(db)
    }

    /**
     * Inicializa la base de datos con datos de ejemplo.
     *
     * @param db La base de datos SQLite.
     */
    private fun initializeData(db: SQLiteDatabase) {
        // Lista de directores iniciales
        val directors = listOf(
            Triple("Jonathan Demme", "EE.UU.", 1944),
            Triple("Martin Campbell", "Nueva Zelanda", 1943),
            Triple("Quentin Tarantino", "EE.UU.", 1963)
        )
        val directorIds = mutableListOf<Long>()
        directors.forEach { (name, nationality, birthYear) ->
            val values = ContentValues().apply {
                put(DIRECTOR_NAME, name)
                put(DIRECTOR_NATIONALITY, nationality)
                put(DIRECTOR_BIRTH_YEAR, birthYear)
            }
            directorIds.add(db.insert(TABLE_DIRECTORS, null, values)) // Añadimos y guardamos el ID
        }

        // Lista de actores iniciales
        val actors = listOf(
            Triple("Jodie Foster", "EE.UU.", 1962),
            Triple("Anthony Hopkins", "Reino Unido", 1937),
            Triple("Antonio Banderas", "España", 1960),
            Triple("Catherine Zeta-Jones", "Reino Unido", 1969),
            Triple("Jamie Foxx", "EE.UU.", 1967),
            Triple("Leonardo DiCaprio", "EE.UU.", 1974)
        )
        val actorIds = mutableListOf<Long>()
        actors.forEach { (name, nationality, birthYear) ->
            val values = ContentValues().apply {
                put(ACTOR_NAME, name)
                put(ACTOR_NATIONALITY, nationality)
                put(ACTOR_BIRTH_YEAR, birthYear)
            }
            actorIds.add(db.insert(TABLE_ACTORS, null, values)) // Añadimos y guardamos el ID
        }

        // Lista de películas iniciales
        val movies = listOf(
            Triple("El silencio de los corderos", 1991, directorIds[0]),
            Triple("La leyenda del Zorro", 2005, directorIds[1]),
            Triple("Django", 2012, directorIds[2])
        )
        val movieIds = mutableListOf<Long>()
        movies.forEach { (title, year, directorId) ->
            val values = ContentValues().apply {
                put(MOVIE_TITLE, title)
                put(MOVIE_YEAR, year)
                put(MOVIE_DIRECTOR_ID, directorId)
            }
            movieIds.add(db.insert(TABLE_MOVIES, null, values)) // Añadimos y guardamos el ID
        }

        // Relaciones entre películas y actores
        val movieActors = listOf(
            Pair(movieIds[0], listOf(actorIds[0], actorIds[1])),
            Pair(movieIds[1], listOf(actorIds[2], actorIds[3])),
            Pair(movieIds[2], listOf(actorIds[4], actorIds[5]))
        )
        movieActors.forEach { (movieId, actors) ->
            actors.forEach { actorId ->
                val values = ContentValues().apply {
                    put(MA_MOVIE_ID, movieId)
                    put(MA_ACTOR_ID, actorId)
                }
                db.insert(TABLE_MOVIES_ACTORS, null, values) // Añadimos la relación
            }
        }
    }

    /**
     * Actualiza la base de datos cuando cambia la versión.
     *
     * @param db La base de datos SQLite.
     * @param oldVersion Versión anterior.
     * @param newVersion Nueva versión.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Borramos todas las tablas y las recreamos
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOVIES_ACTORS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOVIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DIRECTORS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ACTORS")
        onCreate(db) // Volvemos a crear todo
    }

    /**
     * Añade una nueva película a la base de datos.
     *
     * @param title Título de la película.
     * @param year Año de la película.
     * @param directorId ID del director.
     * @return ID de la película añadida.
     */
    fun addMovie(title: String, year: Int, directorId: Int): Long {
        val db = writableDatabase // Abrimos la base de datos en modo escritura
        val values = ContentValues().apply {
            put(MOVIE_TITLE, title)
            put(MOVIE_YEAR, year)
            put(MOVIE_DIRECTOR_ID, directorId)
        }
        val id = db.insert(TABLE_MOVIES, null, values) // Insertamos la película
        db.close() // Cerramos la base de datos
        return id // Devolvemos el ID generado
    }

    /**
     * Actualiza una película existente.
     *
     * @param id ID de la película.
     * @param title Nuevo título.
     * @param year Nuevo año.
     * @param directorId Nuevo ID del director.
     * @return True si se actualizó correctamente.
     */
    fun updateMovie(id: Int, title: String, year: Int, directorId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(MOVIE_TITLE, title)
            put(MOVIE_YEAR, year)
            put(MOVIE_DIRECTOR_ID, directorId)
        }
        val rowsAffected = db.update(TABLE_MOVIES, values, "$MOVIE_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsAffected > 0 // True si se actualizó al menos una fila
    }

    /**
     * Elimina una película de la base de datos.
     *
     * @param id ID de la película a eliminar.
     * @return True si se eliminó correctamente.
     */
    fun deleteMovie(id: Int): Boolean {
        val db = writableDatabase
        db.beginTransaction() // Empezamos una transacción para asegurar consistencia
        try {
            db.delete(TABLE_MOVIES_ACTORS, "$MA_MOVIE_ID = ?", arrayOf(id.toString())) // Borramos relaciones
            val rowsAffected = db.delete(TABLE_MOVIES, "$MOVIE_ID = ?", arrayOf(id.toString())) // Borramos la película
            db.setTransactionSuccessful() // Marcamos la transacción como exitosa
            return rowsAffected > 0
        } finally {
            db.endTransaction() // Finalizamos la transacción
            db.close()
        }
    }

    /**
     * Obtiene todas las películas de la base de datos.
     *
     * @return Lista de películas.
     */
    fun getAllMovies(): List<Movie> {
        val movies = mutableListOf<Movie>()
        val db = readableDatabase // Abrimos en modo lectura
        val cursor = db.rawQuery("SELECT m.$MOVIE_ID, m.$MOVIE_TITLE, m.$MOVIE_YEAR, " +
                "d.$DIRECTOR_ID, d.$DIRECTOR_NAME " +
                "FROM $TABLE_MOVIES m " +
                "JOIN $TABLE_DIRECTORS d ON m.$MOVIE_DIRECTOR_ID = d.$DIRECTOR_ID", null)

        if (cursor.moveToFirst()) {
            do {
                val movie = Movie(
                    cursor.getInt(cursor.getColumnIndexOrThrow(MOVIE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MOVIE_TITLE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(MOVIE_YEAR)),
                    Director(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DIRECTOR_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DIRECTOR_NAME)),
                        "", 0 // Nacionalidad y año no se necesitan aquí
                    )
                )
                movies.add(movie)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return movies
    }

    /**
     * Obtiene los actores de una película específica.
     *
     * @param movieId ID de la película.
     * @return Lista de actores.
     */
    fun getActorsForMovie(movieId: Int): List<Actor> {
        val actors = mutableListOf<Actor>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT a.* FROM $TABLE_ACTORS a " +
                "JOIN $TABLE_MOVIES_ACTORS ma ON a.$ACTOR_ID = ma.$MA_ACTOR_ID " +
                "WHERE ma.$MA_MOVIE_ID = ?", arrayOf(movieId.toString()))

        if (cursor.moveToFirst()) {
            do {
                actors.add(
                    Actor(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ACTOR_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ACTOR_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ACTOR_NATIONALITY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(ACTOR_BIRTH_YEAR))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return actors
    }

    /**
     * Añade una relación entre una película y un actor.
     *
     * @param movieId ID de la película.
     * @param actorId ID del actor.
     */
    fun addMovieActor(movieId: Int, actorId: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(MA_MOVIE_ID, movieId)
            put(MA_ACTOR_ID, actorId)
        }
        db.insert(TABLE_MOVIES_ACTORS, null, values)
        db.close()
    }

    /**
     * Elimina todas las relaciones de actores para una película.
     *
     * @param movieId ID de la película.
     */
    fun clearMovieActors(movieId: Int) {
        val db = writableDatabase
        db.delete(TABLE_MOVIES_ACTORS, "$MA_MOVIE_ID = ?", arrayOf(movieId.toString()))
        db.close()
    }

    /**
     * Añade un nuevo director a la base de datos.
     *
     * @param name Nombre del director.
     * @param nationality Nacionalidad del director.
     * @param birthYear Año de nacimiento del director.
     * @return ID del director añadido.
     */
    fun addDirector(name: String, nationality: String, birthYear: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DIRECTOR_NAME, name)
            put(DIRECTOR_NATIONALITY, nationality)
            put(DIRECTOR_BIRTH_YEAR, birthYear)
        }
        val id = db.insert(TABLE_DIRECTORS, null, values)
        db.close()
        return id
    }

    /**
     * Actualiza un director existente.
     *
     * @param id ID del director.
     * @param name Nuevo nombre.
     * @param nationality Nueva nacionalidad.
     * @param birthYear Nuevo año de nacimiento.
     * @return True si se actualizó correctamente.
     */
    fun updateDirector(id: Int, name: String, nationality: String, birthYear: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DIRECTOR_NAME, name)
            put(DIRECTOR_NATIONALITY, nationality)
            put(DIRECTOR_BIRTH_YEAR, birthYear)
        }
        val rowsAffected = db.update(TABLE_DIRECTORS, values, "$DIRECTOR_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsAffected > 0
    }

    /**
     * Elimina un director de la base de datos.
     *
     * @param id ID del director a eliminar.
     * @return True si se eliminó correctamente.
     */
    fun deleteDirector(id: Int): Boolean {
        val db = writableDatabase
        try {
            val rowsAffected = db.delete(TABLE_DIRECTORS, "$DIRECTOR_ID = ?", arrayOf(id.toString()))
            if (rowsAffected == 0) return false
            return true
        } catch (e: SQLiteConstraintException) {
            throw SQLiteConstraintException("El director tiene películas asociadas")
        } finally {
            db.close()
        }
    }

    /**
     * Obtiene todos los directores de la base de datos.
     *
     * @return Lista de directores.
     */
    fun getAllDirectors(): List<Director> {
        val directors = mutableListOf<Director>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_DIRECTORS", null)

        if (cursor.moveToFirst()) {
            do {
                directors.add(
                    Director(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DIRECTOR_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DIRECTOR_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DIRECTOR_NATIONALITY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DIRECTOR_BIRTH_YEAR))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return directors
    }

    /**
     * Añade un nuevo actor a la base de datos.
     *
     * @param name Nombre del actor.
     * @param nationality Nacionalidad del actor.
     * @param birthYear Año de nacimiento del actor.
     * @return ID del actor añadido.
     */
    fun addActor(name: String, nationality: String, birthYear: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(ACTOR_NAME, name)
            put(ACTOR_NATIONALITY, nationality)
            put(ACTOR_BIRTH_YEAR, birthYear)
        }
        val id = db.insert(TABLE_ACTORS, null, values)
        db.close()
        return id
    }

    /**
     * Actualiza un actor existente.
     *
     * @param id ID del actor.
     * @param name Nuevo nombre.
     * @param nationality Nueva nacionalidad.
     * @param birthYear Nuevo año de nacimiento.
     * @return True si se actualizó correctamente.
     */
    fun updateActor(id: Int, name: String, nationality: String, birthYear: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(ACTOR_NAME, name)
            put(ACTOR_NATIONALITY, nationality)
            put(ACTOR_BIRTH_YEAR, birthYear)
        }
        val rowsAffected = db.update(TABLE_ACTORS, values, "$ACTOR_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsAffected > 0
    }

    /**
     * Elimina un actor de la base de datos.
     *
     * @param id ID del actor a eliminar.
     * @return True si se eliminó correctamente.
     */
    fun deleteActor(id: Int): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_MOVIES_ACTORS, "$MA_ACTOR_ID = ?", arrayOf(id.toString())) // Borramos relaciones
            val rowsAffected = db.delete(TABLE_ACTORS, "$ACTOR_ID = ?", arrayOf(id.toString())) // Borramos el actor
            db.setTransactionSuccessful()
            return rowsAffected > 0
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    /**
     * Obtiene todos los actores de la base de datos.
     *
     * @return Lista de actores.
     */
    fun getAllActors(): List<Actor> {
        val actors = mutableListOf<Actor>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ACTORS", null)

        if (cursor.moveToFirst()) {
            do {
                actors.add(
                    Actor(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ACTOR_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ACTOR_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ACTOR_NATIONALITY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(ACTOR_BIRTH_YEAR))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return actors
    }
}

data class Movie(val id: Int, val title: String, val year: Int, val director: Director)
data class Director(val id: Int, val name: String, val nationality: String, val birthYear: Int)
data class Actor(val id: Int, val name: String, val nationality: String, val birthYear: Int)