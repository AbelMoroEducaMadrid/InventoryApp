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
import com.example.inventoryapp.adapters.ActorAdapter
import com.example.inventoryapp.ActorDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ActorsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActorAdapter
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

        val actors = dbHelper.getAllActors()
        adapter = ActorAdapter(actors,
            onEdit = { actor ->
                ActorDialog(dbHelper, actor) { adapter.updateActors(dbHelper.getAllActors()) }
                    .show(parentFragmentManager, "EditActorDialog")
            },
            onDelete = { actor ->
                try {
                    if (dbHelper.deleteActor(actor.id)) {
                        adapter.updateActors(dbHelper.getAllActors())
                        Toast.makeText(context, "Actor eliminado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error al eliminar el actor", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
        recyclerView.adapter = adapter

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            ActorDialog(dbHelper) { adapter.updateActors(dbHelper.getAllActors()) }
                .show(parentFragmentManager, "AddActorDialog")
        }

        return view
    }
}