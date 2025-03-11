package com.example.inventoryapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.inventoryapp.DatabaseHelper
import com.example.inventoryapp.R

class AddItemFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_item, container, false)

        val nameEditText = view.findViewById<EditText>(R.id.edit_name)
        val quantityEditText = view.findViewById<EditText>(R.id.edit_quantity)
        val addButton = view.findViewById<Button>(R.id.btn_add)

        addButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val quantity = quantityEditText.text.toString().toIntOrNull() ?: 0

            if (name.isNotEmpty()) {
                val dbHelper = DatabaseHelper(requireContext())
                dbHelper.addItem(name, quantity)

                nameEditText.text.clear()
                quantityEditText.text.clear()
            }
        }

        return view
    }
}