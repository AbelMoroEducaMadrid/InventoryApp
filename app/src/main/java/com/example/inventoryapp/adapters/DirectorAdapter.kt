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

class DirectorAdapter(
    private var directors: List<Director>,
    private val onEdit: (Director) -> Unit,
    private val onDelete: (Director) -> Unit
) : RecyclerView.Adapter<DirectorAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.item_title)
        val detailsTextView: TextView = itemView.findViewById(R.id.item_details)
        val editButton: ImageButton = itemView.findViewById(R.id.btn_edit)
        val deleteButton: ImageButton = itemView.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val director = directors[position]
        holder.nameTextView.text = director.name
        holder.detailsTextView.text = "Nacionalidad: ${director.nationality}\nAño de nacimiento: ${director.birthYear}"

        holder.editButton.setOnClickListener { onEdit(director) }
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

    override fun getItemCount() = directors.size

    fun updateDirectors(newDirectors: List<Director>) {
        directors = newDirectors
        notifyDataSetChanged()
    }
}