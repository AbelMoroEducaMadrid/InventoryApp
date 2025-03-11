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

/**
 * Adapter para mostrar una lista de películas en un RecyclerView.
 *
 * @param movies Lista de películas a mostrar.
 * @param dbHelper Instancia de DatabaseHelper para acceder a la base de datos.
 * @param onEdit Función que se ejecuta cuando se edita una película.
 * @param onDelete Función que se ejecuta cuando se elimina una película.
 */
class MovieAdapter(
    private var movies: List<Movie>,
    private val dbHelper: DatabaseHelper,
    private val onEdit: (Movie) -> Unit,
    private val onDelete: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    /**
     * ViewHolder para los elementos de la lista de películas.
     *
     * @param itemView Vista que representa un elemento de la lista.
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.item_title) // TextView para el título de la película
        val detailsTextView: TextView = itemView.findViewById(R.id.item_details) // TextView para los detalles de la película
        val editButton: ImageButton = itemView.findViewById(R.id.btn_edit) // Botón para editar la película
        val deleteButton: ImageButton = itemView.findViewById(R.id.btn_delete) // Botón para eliminar la película
    }

    /**
     * Crea un nuevo ViewHolder para los elementos de la lista.
     *
     * @param parent El ViewGroup en el que se añadirá la nueva vista.
     * @param viewType El tipo de vista.
     * @return Un nuevo ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Infla la vista del elemento de la lista
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return ViewHolder(view)
    }

    /**
     * Vincula los datos de la película con las vistas del ViewHolder.
     *
     * @param holder El ViewHolder que contiene las vistas.
     * @param position La posición del elemento en la lista.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        // Establece el título y los detalles de la película en los TextView
        holder.titleTextView.text = movie.title
        val actors = dbHelper.getActorsForMovie(movie.id).joinToString(", ") { it.name }
        holder.detailsTextView.text = "Director: ${movie.director.name}\nActores: $actors\nAño: ${movie.year}"

        // Configura el botón de editar para ejecutar la función onEdit
        holder.editButton.setOnClickListener { onEdit(movie) }

        // Configura el botón de eliminar para mostrar un diálogo de confirmación
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

    /**
     * Obtiene el número de elementos en la lista.
     *
     * @return El número de películas en la lista.
     */
    override fun getItemCount() = movies.size

    /**
     * Actualiza la lista de películas y notifica al adapter que los datos han cambiado.
     *
     * @param newMovies Nueva lista de películas.
     */
    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }
}