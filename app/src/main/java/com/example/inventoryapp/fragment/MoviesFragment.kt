package com.example.inventoryapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventoryapp.DatabaseHelper
import com.example.inventoryapp.R
import com.example.inventoryapp.adapters.MovieAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.inventoryapp.MovieDialog

class MoviesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val dbHelper = DatabaseHelper(requireContext())
        val movies = dbHelper.getAllMovies()
        adapter = MovieAdapter(movies, dbHelper)
        recyclerView.adapter = adapter

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            MovieDialog(dbHelper) { adapter.updateMovies(dbHelper.getAllMovies()) }
                .show(parentFragmentManager, "MovieDialog")
        }

        return view
    }
}