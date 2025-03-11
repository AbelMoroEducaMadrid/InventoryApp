package com.example.inventoryapp

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.DialogFragment

class DirectorDialog(
    private val dbHelper: DatabaseHelper,
    private val director: Director? = null,
    private val onDirectorAddedOrUpdated: () -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_person, null)

        val nameEdit = view.findViewById<EditText>(R.id.edit_name)
        val nationalityEdit = view.findViewById<EditText>(R.id.edit_nationality)
        val birthYearEdit = view.findViewById<EditText>(R.id.edit_birth_year)

        if (director != null) {
            nameEdit.setText(director.name)
            nationalityEdit.setText(director.nationality)
            birthYearEdit.setText(director.birthYear.toString())
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(if (director == null) "Añadir Director" else "Editar Director")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                val name = nameEdit.text.toString()
                val nationality = nationalityEdit.text.toString()
                val birthYear = birthYearEdit.text.toString().toIntOrNull() ?: 0

                if (name.isNotEmpty()) {
                    if (director == null) {
                        dbHelper.addDirector(name, nationality, birthYear)
                    } else {
                        dbHelper.updateDirector(director.id, name, nationality, birthYear)
                    }
                    onDirectorAddedOrUpdated()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }
}