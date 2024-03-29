package com.example.weatherforecast.location.view

import android.app.Activity
import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentMapsBinding
import com.example.weatherforecast.dp.LocationLocalDataSourceImplementation
import com.example.weatherforecast.favouritePlaces.viewModel.FavouritePlacesViewModel
import com.example.weatherforecast.favouritePlaces.viewModel.FavouritePlacesViewModelFactory
import com.example.weatherforecast.model.FavouriteLocation
import com.example.weatherforecast.model.LocationRepositoryImplementation
import com.example.weatherforecast.network.LocationRemoteDataSourceImplementation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@Suppress("DEPRECATION")
class MapsFragment : Fragment() , GoogleMap.OnMapClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: FragmentMapsBinding

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private lateinit var favouritePlacesViewModelFactory: FavouritePlacesViewModelFactory
    private lateinit var favouritePlacesViewModel: FavouritePlacesViewModel

    private lateinit var cityName: String
    private lateinit var address: String

    private val callback = OnMapReadyCallback { googleMap ->
        arguments?.let {
            latitude = it.getDouble("latitude", 0.0)
            longitude = it.getDouble("longitude", 0.0)
        }
        mMap = googleMap
        mMap.setOnMapClickListener(this)
        val location = LatLng(latitude, longitude)
        val markerTitle = arguments?.getString("markerTitle") ?: "Marker Title"
        mMap.addMarker(MarkerOptions().position(location).title(markerTitle))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        val zoomLevel = 10.0f
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
    }

    override fun onMapClick(point: LatLng) {
        Toast.makeText(context, "point is $point", Toast.LENGTH_SHORT).show()
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(point))
        latitude = point.latitude
        longitude = point.longitude
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        favouritePlacesViewModelFactory = FavouritePlacesViewModelFactory(
            LocationRepositoryImplementation.getInstance(
                LocationRemoteDataSourceImplementation(),
                LocationLocalDataSourceImplementation(requireContext())
            )
        )
        favouritePlacesViewModel = ViewModelProvider(
            this,
            favouritePlacesViewModelFactory
        )[FavouritePlacesViewModel::class.java]


        binding.myButton.setOnClickListener {
            val preferences = context?.getSharedPreferences("pref", Context.MODE_PRIVATE)
            val isFavourite = preferences?.getBoolean("isFavourite", false)
            val isAlert = preferences?.getBoolean("isAlert", false)
            if (isFavourite == true) {
                getAddressFromLocation(latitude, longitude)
                val favouriteLocation = FavouriteLocation(latitude, longitude, cityName, address)
                favouritePlacesViewModel.insertPlace(favouriteLocation)
                val editor = preferences.edit()
                editor?.putBoolean("isFavourite", false)
                editor?.apply()

                val navController =
                    Navigation.findNavController((context as Activity), R.id.fragmentNavHost)
                navController.navigate(R.id.action_mapsFragment_to_favouritesFragment)
            }
            else if(isAlert == true){
                val editor = preferences.edit()
                editor?.putBoolean("isAlert", false)
                editor?.apply()

                val bundle = Bundle()
                bundle.putDouble("latitude", latitude)
                bundle.putDouble("longitude", longitude)
                val navController = Navigation.findNavController((context as Activity), R.id.fragmentNavHost)
                navController.navigate(R.id.action_mapsFragment_to_alertFragment , bundle)
            }
            else {
                val bundle = Bundle()
                bundle.putDouble("latitude", latitude)
                bundle.putDouble("longitude", longitude)
                val navController =
                    Navigation.findNavController((context as Activity), R.id.fragmentNavHost)
                navController.navigate(R.id.action_mapsFragment_to_currentLocation, bundle)
            }
        }
    }
    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireContext())
        Log.i("TAG", "getAddressFromLocation: $latitude$longitude")
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        address = addresses?.firstOrNull()?.getAddressLine(0) ?: "Address not found"
        cityName = addresses?.firstOrNull()?.locality ?: "City name not found"
    }
}