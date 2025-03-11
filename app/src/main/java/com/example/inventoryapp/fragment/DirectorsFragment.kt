package com.example.inventoryapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventoryapp.DatabaseHelper
import com.example.inventoryapp.R
import com.example.inventoryapp.adapters.DirectorAdapter
import com.example.inventoryapp.DirectorDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DirectorsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DirectorAdapter
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        dbHelper = DatabaseHelper(requireContext())
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val directors = dbHelper.getAllDirectors()
        adapter = DirectorAdapter(directors,
            onEdit = { director ->
                DirectorDialog(dbHelper, director) { adapter.updateDirectors(dbHelper.getAllDirectors()) }
                    .show(parentFragmentManager, "EditDirectorDialog")
            },
            onDelete = { director ->
                try {
                    if (dbHelper.deleteDirector(director.id)) {
                        adapter.updateDirectors(dbHelper.getAllDirectors())
                        Toast.makeText(context, "Director eliminado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error al eliminar el director", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        )
        recyclerView.adapter = adapter

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            DirectorDialog(dbHelper) { adapter.updateDirectors(dbHelper.getAllDirectors()) }
                .show(parentFragmentManager, "AddDirectorDialog")
        }

        return view
    }
}