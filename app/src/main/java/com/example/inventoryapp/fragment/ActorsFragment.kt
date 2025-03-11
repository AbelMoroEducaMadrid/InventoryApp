package com.example.inventoryapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventoryapp.DatabaseHelper
import com.example.inventoryapp.R
import com.example.inventoryapp.adapters.ActorAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.inventoryapp.ActorDialog

class ActorsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActorAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val dbHelper = DatabaseHelper(requireContext())
        val actors = dbHelper.getAllActors()
        adapter = ActorAdapter(actors)
        recyclerView.adapter = adapter

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            ActorDialog(dbHelper) { adapter.updateActors(dbHelper.getAllActors()) }
                .show(parentFragmentManager, "ActorDialog")
        }

        return view
    }
}