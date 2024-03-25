@file:Suppress("DEPRECATION")

package com.example.weatherforecast.location.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.location.viewModel.LocationViewModel
import com.example.weatherforecast.location.viewModel.LocationViewModelFactory
import com.example.weatherforecast.databinding.FragmentCurrentLocationBinding
import com.example.weatherforecast.dp.LocationLocalDataSourceImplementation
import com.example.weatherforecast.model.*
import com.example.weatherforecast.network.LocationRemoteDataSourceImplementation
import com.google.android.gms.location.*
import java.util.*
import kotlin.math.min

class CurrentLocation : Fragment() , OnPlaceClickListener {
    private lateinit var locationViewModelFactory: LocationViewModelFactory
    private lateinit var locationViewModel: LocationViewModel

    private lateinit var hoursOfDayAdapter: HoursOfDayAdapter
    private lateinit var daysOfWeekAdapter: DaysOfWeekAdapter

    private lateinit var hoursLayoutManager: LinearLayoutManager
    private lateinit var daysLayoutManager: LinearLayoutManager

    private lateinit var binding: FragmentCurrentLocationBinding
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val requestLocationCode = 3838
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCurrentLocationBinding.inflate(inflater, container, false)

        hoursLayoutManager = LinearLayoutManager(context)
        hoursLayoutManager.orientation = RecyclerView.HORIZONTAL
        hoursOfDayAdapter = HoursOfDayAdapter(context)
        binding.hoursRecyclerView .apply {
            layoutManager = hoursLayoutManager
            adapter = hoursOfDayAdapter
        }

        daysLayoutManager = LinearLayoutManager(context)
        daysLayoutManager.orientation = RecyclerView.VERTICAL
        daysOfWeekAdapter = DaysOfWeekAdapter(context)
        binding.daysRecyclerView .apply {
            layoutManager = daysLayoutManager
            adapter = daysOfWeekAdapter
        }

        // Fetching API_KEY which we wrapped
        val ai: ApplicationInfo =
            requireContext().packageManager.getApplicationInfo(requireContext().packageName, PackageManager.GET_META_DATA)
/*
        val value = ai.metaData["AIzaSyA2XpEiiz_0D_i0IZoKGoa66FIzTCpfOHU"]
        val apiKey = value.toString()
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyA2XpEiiz_0D_i0IZoKGoa66FIzTCpfOHU")
        }
        val autocompleteSupportFragment1 = childFragmentManager.findFragmentById(R.id.autocomplete_fragment1) as AutocompleteSupportFragment?
        autocompleteSupportFragment1?.setPlaceFields(
            listOf(Place.Field.NAME, Place.Field.LAT_LNG)
        )
        autocompleteSupportFragment1?.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {

                val latlng = place.latLng
                val latitude = latlng?.latitude
                val longitude = latlng?.longitude
                Toast.makeText(context,"latlng is : ${latitude} , ${longitude}", Toast.LENGTH_SHORT).show()
            }

            override fun onError(status: Status) {
                Log.e("TAG", "Error status: ${status.statusCode}, message: ${status.statusMessage}")
                Toast.makeText(requireContext(), "Error: ${status.statusMessage}", Toast.LENGTH_SHORT).show()

                Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
            }
        })
        */
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
/*
        binding.button.setOnClickListener {
            val bundle = Bundle()
            bundle.putDouble("latitude" , latitude)
            bundle.putDouble("longitude" , longitude)
            val navController = findNavController((context as Activity), R.id.fragmentNavHost)
            navController.navigate(R.id.action_currentLocation_to_mapsFragment, bundle)
        }
        binding.button2.setOnClickListener {
            val navController = findNavController((context as Activity), R.id.fragmentNavHost)
            navController.navigate(R.id.action_currentLocation_to_favouritesFragment)
        }
        binding.button3.setOnClickListener {
            val latLng = LatLng(latitude = latitude , longitude = longitude)
            onFavouritePlaceClick(latLng)
        }
        binding.button4.setOnClickListener {
            val navController = findNavController((context as Activity), R.id.fragmentNavHost)
            navController.navigate(R.id.action_currentLocation_to_alertFragment)
        }
        */

    }
    override fun onStart() {
        super.onStart()
        if (arguments != null) {
            latitude = arguments?.getDouble("latitude") ?: 0.0
            longitude = arguments?.getDouble("longitude") ?: 0.0
            locationViewModelFactory = LocationViewModelFactory(
                LocationRepositoryImplementation.getInstance(
                    LocationRemoteDataSourceImplementation(),
                    LocationLocalDataSourceImplementation(requireContext())
                ), latitude, longitude
            )
            locationViewModel = ViewModelProvider(
                this@CurrentLocation,
                locationViewModelFactory
            )[LocationViewModel::class.java]
            locationViewModel.location.observe(this@CurrentLocation) { location ->
                setUpUI(location.list , location.city.name)
            }

        } else {
            if (checkPermissions()) {
                if (isLocationEnabled()) {
                    getFreshLocation()
                } else {
                    enableLocationServices()
                }
            } else {
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    requestLocationCode
                )
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun getFreshLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(0).apply { setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) }.build(),
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    val location = locationResult.lastLocation
                    latitude = location?.latitude ?: 0.0
                    longitude = location?.longitude ?: 0.0
                    locationViewModelFactory = LocationViewModelFactory(
                        LocationRepositoryImplementation.getInstance(
                            LocationRemoteDataSourceImplementation(),
                            LocationLocalDataSourceImplementation(requireContext())
                        ) ,latitude , longitude)
                    locationViewModel = ViewModelProvider(this@CurrentLocation, locationViewModelFactory)[LocationViewModel::class.java]

                    locationViewModel.location.observe(this@CurrentLocation) { location ->
                        setUpUI(location.list , location.city.name)
                    }
                    fusedLocationProviderClient.removeLocationUpdates(this)
                }
            },
            Looper.myLooper()
        )
    }

    private fun enableLocationServices() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestLocationCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getFreshLocation()
            }
        }
    }

    override fun onFavouritePlaceClick(latLng: LatLng) {
        locationViewModel.insertPlace(latLng)
    }

    private fun setUpUI(location: List<WeatherItem> , city: String){
        hoursOfDayAdapter.submitList(location.subList(0 ,7))

        val daysList: MutableList<WeatherItem> = mutableListOf()
        var iterator = 8
        for (i in 0 until 4) {
            println("i: $i, iterator: $iterator")
            daysList.add(location[iterator])
            iterator += 8
        }
        println("${daysList[3].dt_txt}")
        daysOfWeekAdapter.submitList(daysList)

        binding.cityNameTV.text = city.split(" ")[0]
        val currentTime = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = currentTime
        }
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
        var _minute = "00"
        if(minute < 10){
            _minute = "0${minute}"
            binding.dateTimeTV.text = "$day/$month/$year -- $hour:$_minute $amPm"
        }
        else{
            binding.dateTimeTV.text = "$day/$month/$year -- $hour:$minute $amPm"
        }


        binding.descriptionTV.text = location[0].weather[0].description
        binding.tempTV.text = location[0].main.temp.toString()
        binding.windSpeed.text = location[0].wind.speed.toString()
        binding.humidity.text = location[0].main.humidity.toString()
        binding.clouds.text = location[0].clouds.all.toString()
        binding.pressure.text = location[0].main.pressure.toString()
        Glide.with(this)
            .load("https://openweathermap.org/img/w/${location[0].weather[0].icon}.png")
            .into(binding.icon)
    }
}