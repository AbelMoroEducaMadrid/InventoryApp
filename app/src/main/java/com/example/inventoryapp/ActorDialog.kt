package com.example.inventoryapp

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.DialogFragment

class ActorDialog(
    private val dbHelper: DatabaseHelper,
    private val actor: Actor? = null,
    private val onActorAddedOrUpdated: () -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_person, null)

        val nameEdit = view.findViewById<EditText>(R.id.edit_name)
        val nationalityEdit = view.findViewById<EditText>(R.id.edit_nationality)
        val birthYearEdit = view.findViewById<EditText>(R.id.edit_birth_year)

        if (actor != null) {
            nameEdit.setText(actor.name)
            nationalityEdit.setText(actor.nationality)
            birthYearEdit.setText(actor.birthYear.toString())
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(if (actor == null) "Añadir Actor" else "Editar Actor")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                val name = nameEdit.text.toString()
                val nationality = nationalityEdit.text.toString()
                val birthYear = birthYearEdit.text.toString().toIntOrNull() ?: 0

                if (name.isNotEmpty()) {
                    if (actor == null) {
                        dbHelper.addActor(name, nationality, birthYear)
                    } else {
                        dbHelper.updateActor(actor.id, name, nationality, birthYear)
                    }
                    onActorAddedOrUpdated()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }
}