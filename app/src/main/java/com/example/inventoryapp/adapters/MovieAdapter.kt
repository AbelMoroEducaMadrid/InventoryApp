package com.example.inventoryapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.inventoryapp.DatabaseHelper
import com.example.inventoryapp.Movie
import com.example.inventoryapp.R

class MovieAdapter(
    private var movies: List<Movie>,
    private val dbHelper: DatabaseHelper,
    private val onEdit: (Movie) -> Unit,
    private val onDelete: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.item_title)
        val detailsTextView: TextView = itemView.findViewById(R.id.item_details)
        val editButton: ImageButton = itemView.findViewById(R.id.btn_edit)
        val deleteButton: ImageButton = itemView.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        holder.titleTextView.text = movie.title
        val actors = dbHelper.getActorsForMovie(movie.id).joinToString(", ") { it.name }
        holder.detailsTextView.text = "Director: ${movie.director.name}\nActores: $actors\nAño: ${movie.year}"

        holder.editButton.setOnClickListener { onEdit(movie) }
        holder.deleteButton.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que quieres eliminar '${movie.title}'?")
                .setPositiveButton("Sí") { _, _ ->
                    onDelete(movie) // Solo elimina si el usuario confirma
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun getItemCount() = movies.size

    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }
}