package com.example.inventoryapp

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.DialogFragment

class DirectorDialog(private val dbHelper: DatabaseHelper, private val onDirectorAdded: () -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_person, null)

        val nameEdit = view.findViewById<EditText>(R.id.edit_name)
        val nationalityEdit = view.findViewById<EditText>(R.id.edit_nationality)
        val birthYearEdit = view.findViewById<EditText>(R.id.edit_birth_year)

        return AlertDialog.Builder(requireContext())
            .setTitle("Añadir Director")
            .setView(view)
            .setPositiveButton("Añadir") { _, _ ->
                val name = nameEdit.text.toString()
                val nationality = nationalityEdit.text.toString()
                val birthYear = birthYearEdit.text.toString().toIntOrNull() ?: 0

                if (name.isNotEmpty()) {
                    dbHelper.addDirector(name, nationality, birthYear)
                    onDirectorAdded()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }
}