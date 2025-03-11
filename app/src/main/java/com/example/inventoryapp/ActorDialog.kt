package com.example.inventoryapp

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment

/**
 * Diálogo para añadir o editar un actor en la base de datos.
 */
class ActorDialog(
    private val dbHelper: DatabaseHelper, // Helper para acceder a la base de datos
    private val actor: Actor? = null, // Actor a editar (null si es nuevo)
    private val onActorAddedOrUpdated: () -> Unit // Función a ejecutar cuando se guarda
) : DialogFragment() {

    /**
     * Crea el diálogo con los campos para el actor.
     *
     * @param savedInstanceState Estado guardado de la instancia, si existe.
     * @return El diálogo creado.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Inflamos (creamos) la vista del diálogo usando el layout dialog_person.xml
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_person, null)

        // Encontramos los campos de texto en la vista
        val nameEdit = view.findViewById<EditText>(R.id.edit_name) // Campo para el nombre
        val nationalityEdit = view.findViewById<EditText>(R.id.edit_nationality) // Campo para la nacionalidad
        val birthYearEdit = view.findViewById<EditText>(R.id.edit_birth_year) // Campo para el año de nacimiento

        // Si estamos editando un actor, rellenamos los campos con sus datos
        if (actor != null) {
            nameEdit.setText(actor.name)
            nationalityEdit.setText(actor.nationality)
            birthYearEdit.setText(actor.birthYear.toString())
        }

        // Creamos el diálogo con un título y botones
        return AlertDialog.Builder(requireContext())
            .setTitle(if (actor == null) "Añadir Actor" else "Editar Actor") // Título según si es nuevo o edición
            .setView(view) // Añadimos la vista con los campos
            .setPositiveButton("Guardar") { _, _ ->
                // Cuando se hace clic en "Guardar", obtenemos los valores de los campos
                val name = nameEdit.text.toString()
                val nationality = nationalityEdit.text.toString()
                val birthYear = birthYearEdit.text.toString().toIntOrNull() ?: 0 // Si no es número, usamos 0

                // Si el nombre no está vacío, guardamos o actualizamos el actor
                if (name.isNotEmpty()) {
                    if (actor == null) {
                        // Si es un actor nuevo, lo añadimos
                        dbHelper.addActor(name, nationality, birthYear)
                    } else {
                        // Si es una edición, lo actualizamos
                        dbHelper.updateActor(actor.id, name, nationality, birthYear)
                    }
                    onActorAddedOrUpdated() // Ejecutamos la función de callback
                }
            }
            .setNegativeButton("Cancelar", null) // Botón para cerrar sin guardar
            .create() // Creamos el diálogo
    }
}