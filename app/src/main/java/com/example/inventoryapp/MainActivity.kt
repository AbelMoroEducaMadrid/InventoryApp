package com.example.inventoryapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.inventoryapp.fragments.ActorsFragment
import com.example.inventoryapp.fragments.DirectorsFragment
import com.example.inventoryapp.fragments.MoviesFragment
import com.google.android.material.navigation.NavigationView

/**
 * Actividad principal de la aplicación con un menú lateral.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout // Layout para el menú lateral

    /**
     * Configura la actividad al iniciarse.
     *
     * @param savedInstanceState Estado guardado de la instancia, si existe.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Establecemos el layout principal

        // Configuramos la barra de herramientas (toolbar)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Encontramos el DrawerLayout y el NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        // Si es la primera vez que se inicia, mostramos el fragmento de películas
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MoviesFragment())
                .commit()
        }

        // Configuramos el listener para los ítems del menú lateral
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_movies -> {
                    // Cuando se selecciona "Películas", mostramos el fragmento de películas
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, MoviesFragment())
                        .commit()
                }
                R.id.nav_directors -> {
                    // Cuando se selecciona "Directores", mostramos el fragmento de directores
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, DirectorsFragment())
                        .commit()
                }
                R.id.nav_actors -> {
                    // Cuando se selecciona "Actores", mostramos el fragmento de actores
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ActorsFragment())
                        .commit()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START) // Cerramos el menú lateral
            true // Indicamos que el evento fue manejado
        }

        // Configuramos el botón de la toolbar para abrir el menú lateral
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    /**
     * Maneja el botón "Atrás" para cerrar el menú si está abierto.
     */
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START) // Cerramos el menú si está abierto
        } else {
            super.onBackPressed() // Si no, dejamos que la actividad maneje el botón normalmente
        }
    }
}