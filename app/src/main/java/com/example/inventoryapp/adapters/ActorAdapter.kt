package com.example.inventoryapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.inventoryapp.Actor
import com.example.inventoryapp.R

/**
 * Adapter para mostrar una lista de actores en un RecyclerView.
 *
 * @param actors Lista de actores a mostrar.
 * @param onEdit Función que se ejecuta cuando se edita un actor.
 * @param onDelete Función que se ejecuta cuando se elimina un actor.
 */
class ActorAdapter(
    private var actors: List<Actor>,
    private val onEdit: (Actor) -> Unit,
    private val onDelete: (Actor) -> Unit
) : RecyclerView.Adapter<ActorAdapter.ViewHolder>() {

    /**
     * ViewHolder para los elementos de la lista de actores.
     *
     * @param itemView Vista que representa un elemento de la lista.
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.item_title) // TextView para el nombre del actor
        val detailsTextView: TextView = itemView.findViewById(R.id.item_details) // TextView para los detalles del actor
        val editButton: ImageButton = itemView.findViewById(R.id.btn_edit) // Botón para editar el actor
        val deleteButton: ImageButton = itemView.findViewById(R.id.btn_delete) // Botón para eliminar el actor
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
     * Vincula los datos del actor con las vistas del ViewHolder.
     *
     * @param holder El ViewHolder que contiene las vistas.
     * @param position La posición del elemento en la lista.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actor = actors[position]
        // Establece el nombre y los detalles del actor en los TextView
        holder.nameTextView.text = actor.name
        holder.detailsTextView.text = "Nacionalidad: ${actor.nationality}\nAño de nacimiento: ${actor.birthYear}"

        // Configura el botón de editar para ejecutar la función onEdit
        holder.editButton.setOnClickListener { onEdit(actor) }

        // Configura el botón de eliminar para mostrar un diálogo de confirmación
        holder.deleteButton.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que quieres eliminar a '${actor.name}'?")
                .setPositiveButton("Sí") { _, _ ->
                    onDelete(actor) // Solo elimina si el usuario confirma
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    /**
     * Obtiene el número de elementos en la lista.
     *
     * @return El número de actores en la lista.
     */
    override fun getItemCount() = actors.size

    /**
     * Actualiza la lista de actores y notifica al adapter que los datos han cambiado.
     *
     * @param newActors Nueva lista de actores.
     */
    fun updateActors(newActors: List<Actor>) {
        actors = newActors
        notifyDataSetChanged()
    }
}