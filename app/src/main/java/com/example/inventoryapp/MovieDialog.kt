package com.example.inventoryapp

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class MovieDialog(private val dbHelper: DatabaseHelper, private val onMovieAdded: () -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_movie, null)

        val titleEdit = view.findViewById<EditText>(R.id.edit_title)
        val yearEdit = view.findViewById<EditText>(R.id.edit_year)
        val directorSpinner = view.findViewById<Spinner>(R.id.spinner_director)
        val actorsChipGroup = view.findViewById<ChipGroup>(R.id.chip_group_actors)

        // Llenar spinner de directores
        val directors = dbHelper.getAllDirectors()
        val directorAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, directors.map { it.name })
        directorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        directorSpinner.adapter = directorAdapter

        // Llenar chips de actores
        val actors = dbHelper.getAllActors()
        actors.forEach { actor ->
            val chip = Chip(context).apply {
                text = actor.name
                isCheckable = true
                tag = actor.id
            }
            actorsChipGroup.addView(chip)
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Añadir Película")
            .setView(view)
            .setPositiveButton("Añadir") { _, _ ->
                val title = titleEdit.text.toString()
                val year = yearEdit.text.toString().toIntOrNull() ?: 0
                val directorIndex = directorSpinner.selectedItemPosition
                val selectedActors = actorsChipGroup.checkedChipIds.mapNotNull { id ->
                    actorsChipGroup.findViewById<Chip>(id)?.tag as? Int
                }

                if (title.isNotEmpty() && directorIndex >= 0) {
                    val directorId = directors[directorIndex].id
                    val movieId = dbHelper.addMovie(title, year, directorId)
                    selectedActors.forEach { actorId ->
                        dbHelper.addMovieActor(movieId.toInt(), actorId)
                    }
                    onMovieAdded()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }
}