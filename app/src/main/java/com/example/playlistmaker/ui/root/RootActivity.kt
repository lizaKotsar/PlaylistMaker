package com.example.playlistmaker.ui.root

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class RootActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController

        val bnv = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bnv.setupWithNavController(navController)

        val showBottomBarOn = setOf(
            R.id.mediaFragment,
            R.id.searchFragment,
            R.id.settingsFragment
        )


        navController.addOnDestinationChangedListener { _, dest, _ ->
            bnv.isVisible = dest.id in showBottomBarOn
        }


        bnv.setOnItemReselectedListener { /* no-op */ }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHost.navController.navigateUp() || super.onSupportNavigateUp()
    }
}
