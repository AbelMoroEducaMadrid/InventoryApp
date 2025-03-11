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
import com.example.inventoryapp.DirectorDialog
import com.example.inventoryapp.R
import com.example.inventoryapp.adapters.DirectorAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Fragmento para mostrar y gestionar la lista de directores.
 */
class DirectorsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView // RecyclerView para mostrar la lista de directores
    private lateinit var adapter: DirectorAdapter // Adapter para conectar la lista de directores con el RecyclerView
    private lateinit var dbHelper: DatabaseHelper // Instancia de DatabaseHelper para interactuar con la base de datos

    /**
     * Crea la vista del fragmento donde se mostrará la lista de directores.
     *
     * @param inflater Inflador de layouts para construir la vista.
     * @param container Contenedor donde se colocará la vista.
     * @param savedInstanceState Estado guardado de la instancia, si existe.
     * @return La vista creada con la lista de directores.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos (creamos) la vista usando el layout fragment_list.xml
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        // Inicializamos la base de datos y el RecyclerView
        dbHelper = DatabaseHelper(requireContext()) // Conexión a la base de datos
        recyclerView = view.findViewById(R.id.recycler_view) // Encontramos el RecyclerView en la vista
        recyclerView.layoutManager = LinearLayoutManager(context) // Le decimos que muestre los elementos en una lista vertical

        // Obtenemos todos los directores de la base de datos
        val directors = dbHelper.getAllDirectors()
        // Configuramos el adapter con las funciones de editar y eliminar
        adapter = DirectorAdapter(directors,
            onEdit = { director ->
                // Cuando se hace clic en "editar", mostramos un diálogo para modificar el director
                DirectorDialog(dbHelper, director) { adapter.updateDirectors(dbHelper.getAllDirectors()) }
                    .show(parentFragmentManager, "EditDirectorDialog")
            },
            onDelete = { director ->
                try {
                    // Intentamos eliminar el director de la base de datos
                    if (dbHelper.deleteDirector(director.id)) {
                        // Si se elimina correctamente, actualizamos la lista y mostramos un mensaje
                        adapter.updateDirectors(dbHelper.getAllDirectors())
                        Toast.makeText(context, "Director eliminado", Toast.LENGTH_SHORT).show()
                    } else {
                        // Si falla, mostramos un mensaje de error
                        Toast.makeText(context, "Error al eliminar el director", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    // Si hay un error inesperado, mostramos el mensaje del error
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        )
        recyclerView.adapter = adapter // Conectamos el adapter al RecyclerView

        // Configuramos el botón flotante (FAB) para añadir un nuevo director
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            // Cuando se hace clic en el botón, mostramos un diálogo para añadir un director
            DirectorDialog(dbHelper) { adapter.updateDirectors(dbHelper.getAllDirectors()) }
                .show(parentFragmentManager, "AddDirectorDialog")
        }

        return view // Devolvemos la vista creada
    }
}