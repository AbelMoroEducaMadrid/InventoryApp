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
import com.example.inventoryapp.R
import com.example.inventoryapp.adapters.MovieAdapter
import com.example.inventoryapp.MovieDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MoviesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MovieAdapter
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        dbHelper = DatabaseHelper(requireContext())
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val movies = dbHelper.getAllMovies()
        adapter = MovieAdapter(movies, dbHelper,
            onEdit = { movie ->
                MovieDialog(dbHelper, movie) { adapter.updateMovies(dbHelper.getAllMovies()) }
                    .show(parentFragmentManager, "EditMovieDialog")
            },
            onDelete = { movie ->
                try {
                    if (dbHelper.deleteMovie(movie.id)) {
                        adapter.updateMovies(dbHelper.getAllMovies())
                        Toast.makeText(context, "Película eliminada", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error al eliminar la película", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
        recyclerView.adapter = adapter

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            MovieDialog(dbHelper) { adapter.updateMovies(dbHelper.getAllMovies()) }
                .show(parentFragmentManager, "AddMovieDialog")
        }

        return view
    }
}