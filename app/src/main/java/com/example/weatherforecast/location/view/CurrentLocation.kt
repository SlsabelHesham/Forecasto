@file:Suppress("DEPRECATION")
package com.example.weatherforecast.location.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.LAYOUT_DIRECTION_LTR
import android.view.View.LAYOUT_DIRECTION_RTL
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.utils.Constants.LANG_ARABIC
import com.example.weatherforecast.utils.Constants.LANG_ENGLISH
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentCurrentLocationBinding
import com.example.weatherforecast.dp.LocationLocalDataSourceImplementation
import com.example.weatherforecast.location.viewModel.LocationViewModel
import com.example.weatherforecast.location.viewModel.LocationViewModelFactory
import com.example.weatherforecast.model.*
import com.example.weatherforecast.network.LocationRemoteDataSourceImplementation
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*


class CurrentLocation : Fragment(){
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

        if(getFromSP().second == "ar"){
            updateAppLanguage(LANG_ARABIC, LAYOUT_DIRECTION_RTL)
        }else{
            updateAppLanguage(LANG_ENGLISH, LAYOUT_DIRECTION_LTR)
        }

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
        binding.mapLocation.setOnClickListener{
            val navController = Navigation.findNavController(context as Activity , R.id.fragmentNavHost)
            navController.navigate(R.id.action_currentLocation_to_mapsFragment)
        }
        binding.menu.setOnClickListener {
            val navController = Navigation.findNavController(context as Activity, R.id.fragmentNavHost)
            navController.  navigate(R.id.action_currentLocation_to_settingFragment)
        }

        if (arguments != null) {
            latitude = arguments?.getDouble("latitude") ?: 0.0
            longitude = arguments?.getDouble("longitude") ?: 0.0

            val pair = getFromSP()
            locationViewModelFactory = LocationViewModelFactory(
                LocationRepositoryImplementation.getInstance(
                    LocationRemoteDataSourceImplementation(),
                    LocationLocalDataSourceImplementation(requireContext())
                ) ,latitude , longitude, pair.first, pair.second)
            locationViewModel = ViewModelProvider(this@CurrentLocation, locationViewModelFactory)[LocationViewModel::class.java]

            Log.i("TAGss", "onLocationResult: $latitude $longitude")
            Log.i("TAGss", "onLocationResult: ${pair.first} ${pair.second}")

            lifecycleScope.launch {
                locationViewModel.location.collectLatest {result ->
                    when(result){
                        is ApiState.Loading -> {
                            //binding.progressBar.visibility = View.VISIBLE
                        }
                        is ApiState.Success -> {
                            //binding.progressBar.visibility = View.GONE
                            setUpUI(result.data?.list ?: listOf(), result.data?.city?.name ?: "")
                        }
                        else -> {
                            Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
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
        return binding.root
    }
    private fun getFromSP() : Pair<String, String>{
    val preferences = context?.getSharedPreferences("pref", Context.MODE_PRIVATE)
    val unitSP = preferences?.getString("temperature", "")
    val langSP = preferences?.getString("language", "")
    return Pair(unitSP.toString(), langSP.toString())
    }
    private fun updateAppLanguage(language: String, direction: Int) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        requireActivity().window.decorView.layoutDirection = direction
        requireContext().resources.updateConfiguration(
            config,
            requireContext().resources.displayMetrics
        )
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
                    fusedLocationProviderClient.removeLocationUpdates(this)
                    latitude = location?.latitude ?: 0.0
                    longitude = location?.longitude ?: 0.0

                    val pair = getFromSP()
                    locationViewModelFactory = LocationViewModelFactory(
                        LocationRepositoryImplementation.getInstance(
                            LocationRemoteDataSourceImplementation(),
                            LocationLocalDataSourceImplementation(requireContext())
                        ) ,latitude , longitude, pair.first, pair.second)
                    locationViewModel = ViewModelProvider(this@CurrentLocation, locationViewModelFactory)[LocationViewModel::class.java]

                    Log.i("TAGss", "onLocationResult: $latitude $longitude")
                    Log.i("TAGss", "onLocationResult: ${pair.first} ${pair.second}")

                    /*
                   locationViewModel.location.observe(this@CurrentLocation) { myLocation ->
                       if (locationViewModel.location.value != null) {
                           setUpUI(myLocation.list, myLocation.city.name)
                       }
                   }
                    */
                    lifecycleScope.launch {
                        locationViewModel.location.collectLatest {result ->
                            when(result){
                                is ApiState.Loading -> {
                                    //binding.progressBar.visibility = View.VISIBLE
                                }
                                is ApiState.Success -> {
                                    //binding.progressBar.visibility = View.GONE
                                    setUpUI(result.data?.list ?: listOf(), result.data?.city?.name ?: "")
                                }
                                else -> {
                                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            },
            Looper.myLooper()
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("TAGss", "onDestroy: ")
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

    override fun onDestroyView() {
        super.onDestroyView()
        //locationViewModel.location.removeObservers(this@CurrentLocation)
        //locationViewModel.location.removeObservers(this@CurrentLocation)
        //findNavController().

        //findNavController().navigate(R.id.settingFragment)
        //locationViewModel.location.removeObservers(this)
        viewModelStore.clear()
        Log.i("TAGss", "onDestroyView: ")
    }

    @SuppressLint("SetTextI18n")
    private fun setUpUI(location: List<WeatherItem>, city: String){
        hoursOfDayAdapter.submitList(location.subList(0 ,7))

        val daysList: MutableList<WeatherItem> = mutableListOf()
        var iterator = 8
        for (i in 0 until 4) {
            println("i: $i, iterator: $iterator")
            daysList.add(location[iterator])
            iterator += 8
        }
        println(daysList[3].dt_txt)
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
        val _minute: String
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
        //locationViewModel.location.removeObservers(this)
        latitude = 0.0
        longitude = 0.0
    }
}