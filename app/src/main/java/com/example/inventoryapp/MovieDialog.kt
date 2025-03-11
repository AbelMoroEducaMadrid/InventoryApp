package com.example.inventoryapp

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

/**
 * Diálogo para añadir o editar una película en la base de datos.
 */
class MovieDialog(
    private val dbHelper: DatabaseHelper, // Helper para acceder a la base de datos
    private val movie: Movie? = null, // Película a editar (null si es nueva)
    private val onMovieAddedOrUpdated: () -> Unit // Función a ejecutar cuando se guarda
) : DialogFragment() {

    /**
     * Crea el diálogo con los campos para la película.
     *
     * @param savedInstanceState Estado guardado de la instancia, si existe.
     * @return El diálogo creado.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Inflamos (creamos) la vista del diálogo usando el layout dialog_movie.xml
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_movie, null)

        // Encontramos los elementos de la vista
        val titleEdit = view.findViewById<EditText>(R.id.edit_title) // Campo para el título
        val yearEdit = view.findViewById<EditText>(R.id.edit_year) // Campo para el año
        val directorSpinner = view.findViewById<Spinner>(R.id.spinner_director) // Spinner para seleccionar director
        val actorsChipGroup = view.findViewById<ChipGroup>(R.id.chip_group_actors) // Grupo de chips para actores

        // Configuramos el spinner con la lista de directores
        val directors = dbHelper.getAllDirectors()
        val directorAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, directors.map { it.name })
        directorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        directorSpinner.adapter = directorAdapter

        // Añadimos chips para los actores disponibles
        val actors = dbHelper.getAllActors()
        actors.forEach { actor ->
            val chip = Chip(context).apply {
                text = actor.name
                isCheckable = true // Permitimos marcar/desmarcar el chip
                tag = actor.id // Guardamos el ID del actor en el tag
                if (movie != null && dbHelper.getActorsForMovie(movie.id).any { it.id == actor.id }) {
                    isChecked = true // Marcamos el chip si el actor ya está en la película
                }
            }
            actorsChipGroup.addView(chip) // Añadimos el chip al grupo
        }

        // Si estamos editando una película, rellenamos los campos con sus datos
        if (movie != null) {
            titleEdit.setText(movie.title)
            yearEdit.setText(movie.year.toString())
            directorSpinner.setSelection(directors.indexOfFirst { it.id == movie.director.id })
        }

        // Creamos el diálogo con un título y botones
        return AlertDialog.Builder(requireContext())
            .setTitle(if (movie == null) "Añadir Película" else "Editar Película") // Título según si es nueva o edición
            .setView(view) // Añadimos la vista con los campos
            .setPositiveButton("Guardar") { _, _ ->
                // Cuando se hace clic en "Guardar", obtenemos los valores de los campos
                val title = titleEdit.text.toString()
                val year = yearEdit.text.toString().toIntOrNull() ?: 0 // Si no es número, usamos 0
                val directorIndex = directorSpinner.selectedItemPosition
                val selectedActors = actorsChipGroup.checkedChipIds.mapNotNull { id ->
                    actorsChipGroup.findViewById<Chip>(id)?.tag as? Int // Obtenemos los IDs de los actores seleccionados
                }

                // Si el título no está vacío y hay un director seleccionado, guardamos o actualizamos
                if (title.isNotEmpty() && directorIndex >= 0) {
                    val directorId = directors[directorIndex].id
                    if (movie == null) {
                        // Si es una película nueva, la añadimos
                        val movieId = dbHelper.addMovie(title, year, directorId)
                        selectedActors.forEach { actorId ->
                            dbHelper.addMovieActor(movieId.toInt(), actorId) // Añadimos relaciones con actores
                        }
                    } else {
                        // Si es una edición, actualizamos
                        dbHelper.updateMovie(movie.id, title, year, directorId)
                        dbHelper.clearMovieActors(movie.id) // Borramos las relaciones anteriores
                        selectedActors.forEach { actorId ->
                            dbHelper.addMovieActor(movie.id, actorId) // Añadimos las nuevas relaciones
                        }
                    }
                    onMovieAddedOrUpdated() // Ejecutamos la función de callback
                }
            }
            .setNegativeButton("Cancelar", null) // Botón para cerrar sin guardar
            .create() // Creamos el diálogo
    }
}