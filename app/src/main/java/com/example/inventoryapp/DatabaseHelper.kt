package com.example.inventoryapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MoviesDB"
        private const val DATABASE_VERSION = 1

        // Tabla Movies
        private const val TABLE_MOVIES = "movies"
        private const val MOVIE_ID = "id"
        private const val MOVIE_TITLE = "title"
        private const val MOVIE_YEAR = "year"
        private const val MOVIE_DIRECTOR_ID = "director_id"

        // Tabla Directors
        private const val TABLE_DIRECTORS = "directors"
        private const val DIRECTOR_ID = "id"
        private const val DIRECTOR_NAME = "name"
        private const val DIRECTOR_NATIONALITY = "nationality"
        private const val DIRECTOR_BIRTH_YEAR = "birth_year"

        // Tabla Actors
        private const val TABLE_ACTORS = "actors"
        private const val ACTOR_ID = "id"
        private const val ACTOR_NAME = "name"
        private const val ACTOR_NATIONALITY = "nationality"
        private const val ACTOR_BIRTH_YEAR = "birth_year"

        // Tabla Movies_Actors (relación M-N)
        private const val TABLE_MOVIES_ACTORS = "movies_actors"
        private const val MA_MOVIE_ID = "movie_id"
        private const val MA_ACTOR_ID = "actor_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $TABLE_MOVIES (" +
                "$MOVIE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$MOVIE_TITLE TEXT," +
                "$MOVIE_YEAR INTEGER," +
                "$MOVIE_DIRECTOR_ID INTEGER," +
                "FOREIGN KEY($MOVIE_DIRECTOR_ID) REFERENCES $TABLE_DIRECTORS($DIRECTOR_ID))")

        db.execSQL("CREATE TABLE $TABLE_DIRECTORS (" +
                "$DIRECTOR_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$DIRECTOR_NAME TEXT," +
                "$DIRECTOR_NATIONALITY TEXT," +
                "$DIRECTOR_BIRTH_YEAR INTEGER)")

        db.execSQL("CREATE TABLE $TABLE_ACTORS (" +
                "$ACTOR_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$ACTOR_NAME TEXT," +
                "$ACTOR_NATIONALITY TEXT," +
                "$ACTOR_BIRTH_YEAR INTEGER)")

        db.execSQL("CREATE TABLE $TABLE_MOVIES_ACTORS (" +
                "$MA_MOVIE_ID INTEGER," +
                "$MA_ACTOR_ID INTEGER," +
                "FOREIGN KEY($MA_MOVIE_ID) REFERENCES $TABLE_MOVIES($MOVIE_ID)," +
                "FOREIGN KEY($MA_ACTOR_ID) REFERENCES $TABLE_ACTORS($ACTOR_ID)," +
                "PRIMARY KEY($MA_MOVIE_ID, $MA_ACTOR_ID))")

        initializeData(db)
    }

    private fun initializeData(db: SQLiteDatabase) {
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
            directorIds.add(db.insert(TABLE_DIRECTORS, null, values))
        }

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
            actorIds.add(db.insert(TABLE_ACTORS, null, values))
        }

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
            movieIds.add(db.insert(TABLE_MOVIES, null, values))
        }

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
                db.insert(TABLE_MOVIES_ACTORS, null, values)
            }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOVIES_ACTORS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOVIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DIRECTORS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ACTORS")
        onCreate(db)
    }

    // Métodos para Movies
    fun addMovie(title: String, year: Int, directorId: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(MOVIE_TITLE, title)
            put(MOVIE_YEAR, year)
            put(MOVIE_DIRECTOR_ID, directorId)
        }
        val id = db.insert(TABLE_MOVIES, null, values)
        db.close()
        return id
    }

    fun updateMovie(id: Int, title: String, year: Int, directorId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(MOVIE_TITLE, title)
            put(MOVIE_YEAR, year)
            put(MOVIE_DIRECTOR_ID, directorId)
        }
        val rowsAffected = db.update(TABLE_MOVIES, values, "$MOVIE_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsAffected > 0
    }

    fun deleteMovie(id: Int): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_MOVIES_ACTORS, "$MA_MOVIE_ID = ?", arrayOf(id.toString()))
            val rowsAffected = db.delete(TABLE_MOVIES, "$MOVIE_ID = ?", arrayOf(id.toString()))
            db.setTransactionSuccessful()
            return rowsAffected > 0
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun getAllMovies(): List<Movie> {
        val movies = mutableListOf<Movie>()
        val db = readableDatabase
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
                        "", 0
                    )
                )
                movies.add(movie)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return movies
    }

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

    fun addMovieActor(movieId: Int, actorId: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(MA_MOVIE_ID, movieId)
            put(MA_ACTOR_ID, actorId)
        }
        db.insert(TABLE_MOVIES_ACTORS, null, values)
        db.close()
    }

    fun clearMovieActors(movieId: Int) {
        val db = writableDatabase
        db.delete(TABLE_MOVIES_ACTORS, "$MA_MOVIE_ID = ?", arrayOf(movieId.toString()))
        db.close()
    }

    // Métodos para Directors
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

    // Métodos para Actors
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

    fun deleteActor(id: Int): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_MOVIES_ACTORS, "$MA_ACTOR_ID = ?", arrayOf(id.toString()))
            val rowsAffected = db.delete(TABLE_ACTORS, "$ACTOR_ID = ?", arrayOf(id.toString()))
            db.setTransactionSuccessful()
            return rowsAffected > 0
        } finally {
            db.endTransaction()
            db.close()
        }
    }

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