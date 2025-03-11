package com.example.inventoryapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventoryapp.ActorDialog
import com.example.inventoryapp.DatabaseHelper
import com.example.inventoryapp.R
import com.example.inventoryapp.adapters.ActorAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Fragmento para mostrar y gestionar la lista de actores.
 */
class ActorsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView // RecyclerView para mostrar la lista de actores
    private lateinit var adapter: ActorAdapter // Adapter para la lista de actores
    private lateinit var dbHelper: DatabaseHelper // Instancia de DatabaseHelper para acceder a la base de datos

    /**
     * Crea la vista del fragmento.
     *
     * @param inflater Inflador de layouts.
     * @param container Contenedor de la vista.
     * @param savedInstanceState Estado guardado de la instancia.
     * @return La vista creada.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        // Inicializa la base de datos y el RecyclerView
        dbHelper = DatabaseHelper(requireContext())
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Obtiene todos los actores de la base de datos
        val actors = dbHelper.getAllActors()
        // Configura el adapter con las funciones de editar y eliminar
        adapter = ActorAdapter(actors,
            onEdit = { actor ->
                // Muestra un diálogo para editar el actor
                ActorDialog(dbHelper, actor) { adapter.updateActors(dbHelper.getAllActors()) }
                    .show(parentFragmentManager, "EditActorDialog")
            },
            onDelete = { actor ->
                try {
                    // Intenta eliminar el actor de la base de datos
                    if (dbHelper.deleteActor(actor.id)) {
                        // Actualiza la lista de actores si la eliminación fue exitosa
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

        // Configura el botón flotante para añadir un nuevo actor
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            // Muestra un diálogo para añadir un nuevo actor
            ActorDialog(dbHelper) { adapter.updateActors(dbHelper.getAllActors()) }
                .show(parentFragmentManager, "AddActorDialog")
        }

        return view
    }
}