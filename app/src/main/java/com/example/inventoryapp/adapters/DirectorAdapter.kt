package com.example.inventoryapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventoryapp.Director
import com.example.inventoryapp.R

class DirectorAdapter(private var directors: List<Director>) : RecyclerView.Adapter<DirectorAdapter.ViewHolder>() {

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
        val director = directors[position]
        holder.nameTextView.text = director.name
        holder.detailsTextView.text = "Nacionalidad: ${director.nationality}\nAño de nacimiento: ${director.birthYear}"
    }

    override fun getItemCount() = directors.size

    fun updateDirectors(newDirectors: List<Director>) {
        directors = newDirectors
        notifyDataSetChanged()
    }
}