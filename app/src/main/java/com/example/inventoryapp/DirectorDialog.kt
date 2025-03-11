package com.example.inventoryapp

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment

/**
 * Diálogo para añadir o editar un director en la base de datos.
 */
class DirectorDialog(
    private val dbHelper: DatabaseHelper, // Helper para acceder a la base de datos
    private val director: Director? = null, // Director a editar (null si es nuevo)
    private val onDirectorAddedOrUpdated: () -> Unit // Función a ejecutar cuando se guarda
) : DialogFragment() {

    /**
     * Crea el diálogo con los campos para el director.
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

        // Si estamos editando un director, rellenamos los campos con sus datos
        if (director != null) {
            nameEdit.setText(director.name)
            nationalityEdit.setText(director.nationality)
            birthYearEdit.setText(director.birthYear.toString())
        }

        // Creamos el diálogo con un título y botones
        return AlertDialog.Builder(requireContext())
            .setTitle(if (director == null) "Añadir Director" else "Editar Director") // Título según si es nuevo o edición
            .setView(view) // Añadimos la vista con los campos
            .setPositiveButton("Guardar") { _, _ ->
                // Cuando se hace clic en "Guardar", obtenemos los valores de los campos
                val name = nameEdit.text.toString()
                val nationality = nationalityEdit.text.toString()
                val birthYear = birthYearEdit.text.toString().toIntOrNull() ?: 0 // Si no es número, usamos 0

                // Si el nombre no está vacío, guardamos o actualizamos el director
                if (name.isNotEmpty()) {
                    if (director == null) {
                        // Si es un director nuevo, lo añadimos
                        dbHelper.addDirector(name, nationality, birthYear)
                    } else {
                        // Si es una edición, lo actualizamos
                        dbHelper.updateDirector(director.id, name, nationality, birthYear)
                    }
                    onDirectorAddedOrUpdated() // Ejecutamos la función de callback
                }
            }
            .setNegativeButton("Cancelar", null) // Botón para cerrar sin guardar
            .create() // Creamos el diálogo
    }
}