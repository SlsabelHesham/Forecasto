@file:Suppress("DEPRECATION")
package com.example.weatherforecast

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.weatherforecast.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentNavHost) as NavHostFragment
        navController = navHostFragment.navController

        setupWithNavController(binding.navigationView, navController)
        binding.navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.alertFragment -> navigateToFragment(R.id.alertFragment)
                R.id.favouritesFragment -> navigateToFragment(R.id.favouritesFragment)
                R.id.settingFragment -> navigateToFragment(R.id.settingFragment)
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
    private fun navigateToFragment(fragmentId: Int) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(navController.graph.getStartDestination(), false)
            .setLaunchSingleTop(true)
            .build()
        navController.navigate(fragmentId, null, navOptions)
    }
}
