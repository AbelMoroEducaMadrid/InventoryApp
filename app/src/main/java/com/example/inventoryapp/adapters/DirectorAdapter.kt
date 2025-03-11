package com.example.inventoryapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.inventoryapp.Director
import com.example.inventoryapp.R

/**
 * Adapter para mostrar una lista de directores en un RecyclerView.
 *
 * @param directors Lista de directores a mostrar.
 * @param onEdit Función que se ejecuta cuando se edita un director.
 * @param onDelete Función que se ejecuta cuando se elimina un director.
 */
class DirectorAdapter(
    private var directors: List<Director>,
    private val onEdit: (Director) -> Unit,
    private val onDelete: (Director) -> Unit
) : RecyclerView.Adapter<DirectorAdapter.ViewHolder>() {

    /**
     * ViewHolder para los elementos de la lista de directores.
     *
     * @param itemView Vista que representa un elemento de la lista.
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.item_title) // TextView para el nombre del director
        val detailsTextView: TextView = itemView.findViewById(R.id.item_details) // TextView para los detalles del director
        val editButton: ImageButton = itemView.findViewById(R.id.btn_edit) // Botón para editar el director
        val deleteButton: ImageButton = itemView.findViewById(R.id.btn_delete) // Botón para eliminar el director
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
     * Vincula los datos del director con las vistas del ViewHolder.
     *
     * @param holder El ViewHolder que contiene las vistas.
     * @param position La posición del elemento en la lista.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val director = directors[position]
        // Establece el nombre y los detalles del director en los TextView
        holder.nameTextView.text = director.name
        holder.detailsTextView.text = "Nacionalidad: ${director.nationality}\nAño de nacimiento: ${director.birthYear}"

        // Configura el botón de editar para ejecutar la función onEdit
        holder.editButton.setOnClickListener { onEdit(director) }

        // Configura el botón de eliminar para mostrar un diálogo de confirmación
        holder.deleteButton.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que quieres eliminar a '${director.name}'?")
                .setPositiveButton("Sí") { _, _ ->
                    onDelete(director) // Solo elimina si el usuario confirma
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    /**
     * Obtiene el número de elementos en la lista.
     *
     * @return El número de directores en la lista.
     */
    override fun getItemCount() = directors.size

    /**
     * Actualiza la lista de directores y notifica al adapter que los datos han cambiado.
     *
     * @param newDirectors Nueva lista de directores.
     */
    fun updateDirectors(newDirectors: List<Director>) {
        directors = newDirectors
        notifyDataSetChanged()
    }
}