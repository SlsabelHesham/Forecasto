@file:Suppress("DEPRECATION")
package com.example.weatherforecast

import android.net.ConnectivityManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.weatherforecast.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentNavHost) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        setupWithNavController(binding.navigationView, navController)
        binding.navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            if (item.itemId == R.id.alertFragment) {
                navController.navigate(R.id.alertFragment)
            } else if (item.itemId == R.id.favouritesFragment) {
                navController.navigate(R.id.favouritesFragment)
            } else if (item.itemId == R.id.mapsFragment) {
                navController.navigate(R.id.mapsFragment)
            } else if (item.itemId == R.id.settingFragment) {
                navController.navigate(R.id.mapsFragment)
                val connectivityManager =
                    this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = connectivityManager.activeNetworkInfo
                if (networkInfo != null && networkInfo.isConnected) {
                    Toast.makeText(this, "Internet!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, "Please Connect to the Internet!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}
