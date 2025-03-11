package com.example.inventoryapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventoryapp.DatabaseHelper
import com.example.inventoryapp.MovieDialog
import com.example.inventoryapp.R
import com.example.inventoryapp.adapters.MovieAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Fragmento para mostrar y gestionar la lista de películas.
 */
class MoviesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView // RecyclerView para mostrar la lista de películas
    private lateinit var adapter: MovieAdapter // Adapter para conectar la lista de películas con el RecyclerView
    private lateinit var dbHelper: DatabaseHelper // Instancia de DatabaseHelper para interactuar con la base de datos

    /**
     * Crea la vista del fragmento donde se mostrará la lista de películas.
     *
     * @param inflater Inflador de layouts para construir la vista.
     * @param container Contenedor donde se colocará la vista.
     * @param savedInstanceState Estado guardado de la instancia, si existe.
     * @return La vista creada con la lista de películas.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos (creamos) la vista usando el layout fragment_list.xml
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        // Inicializamos la base de datos y el RecyclerView
        dbHelper = DatabaseHelper(requireContext()) // Conexión a la base de datos
        recyclerView = view.findViewById(R.id.recycler_view) // Encontramos el RecyclerView en la vista
        recyclerView.layoutManager = LinearLayoutManager(context) // Le decimos que muestre los elementos en una lista vertical

        // Obtenemos todas las películas de la base de datos
        val movies = dbHelper.getAllMovies()
        // Configuramos el adapter con las funciones de editar y eliminar
        adapter = MovieAdapter(movies, dbHelper,
            onEdit = { movie ->
                // Cuando se hace clic en "editar", mostramos un diálogo para modificar la película
                MovieDialog(dbHelper, movie) { adapter.updateMovies(dbHelper.getAllMovies()) }
                    .show(parentFragmentManager, "EditMovieDialog")
            },
            onDelete = { movie ->
                try {
                    // Intentamos eliminar la película de la base de datos
                    if (dbHelper.deleteMovie(movie.id)) {
                        // Si se elimina correctamente, actualizamos la lista y mostramos un mensaje
                        adapter.updateMovies(dbHelper.getAllMovies())
                        Toast.makeText(context, "Película eliminada", Toast.LENGTH_SHORT).show()
                    } else {
                        // Si falla, mostramos un mensaje de error
                        Toast.makeText(context, "Error al eliminar la película", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    // Si hay un error inesperado, mostramos el mensaje del error
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
        recyclerView.adapter = adapter // Conectamos el adapter al RecyclerView

        // Configuramos el botón flotante (FAB) para añadir una nueva película
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            // Cuando se hace clic en el botón, mostramos un diálogo para añadir una película
            MovieDialog(dbHelper) { adapter.updateMovies(dbHelper.getAllMovies()) }
                .show(parentFragmentManager, "AddMovieDialog")
        }

        return view // Devolvemos la vista creada
    }
}