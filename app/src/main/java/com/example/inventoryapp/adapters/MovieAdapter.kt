package com.example.inventoryapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventoryapp.DatabaseHelper
import com.example.inventoryapp.Movie
import com.example.inventoryapp.R

class MovieAdapter(private var movies: List<Movie>, private val dbHelper: DatabaseHelper) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.item_title)
        val detailsTextView: TextView = itemView.findViewById(R.id.item_details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        holder.titleTextView.text = movie.title
        val actors = dbHelper.getActorsForMovie(movie.id).joinToString(", ") { it.name }
        holder.detailsTextView.text = "Director: ${movie.director.name}\nActores: $actors\nAño: ${movie.year}"
    }

    override fun getItemCount() = movies.size

    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }
}