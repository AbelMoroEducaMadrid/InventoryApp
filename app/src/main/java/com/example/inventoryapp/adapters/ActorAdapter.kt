package com.example.inventoryapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventoryapp.Actor
import com.example.inventoryapp.R

class ActorAdapter(private var actors: List<Actor>) : RecyclerView.Adapter<ActorAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.item_title)
        val detailsTextView: TextView = itemView.findViewById(R.id.item_details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actor = actors[position]
        holder.nameTextView.text = actor.name
        holder.detailsTextView.text = "Nacionalidad: ${actor.nationality}\nAño de nacimiento: ${actor.birthYear}"
    }

    override fun getItemCount() = actors.size

    fun updateActors(newActors: List<Actor>) {
        actors = newActors
        notifyDataSetChanged()
    }
}