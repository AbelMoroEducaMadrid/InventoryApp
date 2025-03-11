package com.example.inventoryapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import com.example.inventoryapp.fragments.MoviesFragment
import com.example.inventoryapp.fragments.DirectorsFragment
import com.example.inventoryapp.fragments.ActorsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MoviesFragment())
                .commit()
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_movies -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, MoviesFragment())
                        .commit()
                }
                R.id.nav_directors -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, DirectorsFragment())
                        .commit()
                }
                R.id.nav_actors -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ActorsFragment())
                        .commit()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}