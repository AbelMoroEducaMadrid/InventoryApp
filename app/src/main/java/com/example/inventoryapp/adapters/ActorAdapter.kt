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

class ActorAdapter(
    private var actors: List<Actor>,
    private val onEdit: (Actor) -> Unit,
    private val onDelete: (Actor) -> Unit
) : RecyclerView.Adapter<ActorAdapter.ViewHolder>() {

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
        val actor = actors[position]
        holder.nameTextView.text = actor.name
        holder.detailsTextView.text = "Nacionalidad: ${actor.nationality}\nAño de nacimiento: ${actor.birthYear}"

        holder.editButton.setOnClickListener { onEdit(actor) }
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

    override fun getItemCount() = actors.size

    fun updateActors(newActors: List<Actor>) {
        actors = newActors
        notifyDataSetChanged()
    }
}