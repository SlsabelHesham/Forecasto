package com.example.weatherforecast.location.view

import android.app.Activity
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentMapsBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() , GoogleMap.OnMapClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: FragmentMapsBinding

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

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
        Toast.makeText(context , "point is $point" , Toast.LENGTH_SHORT).show()
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
       // return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        binding.myButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putDouble("latitude" , latitude)
            bundle.putDouble("longitude" , longitude)
            val navController = Navigation.findNavController((context as Activity), R.id.fragmentNavHost)
            navController.navigate(R.id.action_mapsFragment_to_currentLocation , bundle)
        }
    }
}