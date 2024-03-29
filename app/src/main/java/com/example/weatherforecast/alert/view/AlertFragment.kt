package com.example.weatherforecast.alert.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.weatherforecast.alert.viewModel.MyWorker
import com.example.weatherforecast.R
import com.example.weatherforecast.alert.viewModel.AlertsViewModel
import com.example.weatherforecast.alert.viewModel.AlertsViewModelFactory
import com.example.weatherforecast.databinding.FragmentAlertBinding
import com.example.weatherforecast.dp.LocationLocalDataSourceImplementation
import com.example.weatherforecast.model.Alert
import com.example.weatherforecast.model.LocationRepositoryImplementation
import com.example.weatherforecast.model.AlertState
import com.example.weatherforecast.network.LocationRemoteDataSourceImplementation
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION", "NAME_SHADOWING")
class AlertFragment : Fragment(), OnDeleteAlertClickListener {
    private val notificationPermissionRequestCode = 1001
    private lateinit var binding: FragmentAlertBinding

    private lateinit var alertsViewModelFactory: AlertsViewModelFactory
    private lateinit var alertsViewModel: AlertsViewModel

    private lateinit var alertsAdapter: AlertsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private lateinit var specificDateTime: LocalDateTime

    private val preferences = context?.getSharedPreferences("pref", Context.MODE_PRIVATE)
    private var unit = preferences?.getString("temperature" , "")
    private var language = preferences?.getString("language", "")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertBinding.inflate(inflater, container, false)

        alertsViewModelFactory = AlertsViewModelFactory(
            LocationRepositoryImplementation.getInstance(
                LocationRemoteDataSourceImplementation(),
                LocationLocalDataSourceImplementation(requireContext())
            )
        )
        alertsViewModel =
            ViewModelProvider(this, alertsViewModelFactory)[AlertsViewModel::class.java]

        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        alertsAdapter = AlertsAdapter(this, requireContext())

        binding.alertsRecyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = alertsAdapter
        }

        binding.floatingActionButton.setOnClickListener {
            val preferences = context?.getSharedPreferences("pref", Context.MODE_PRIVATE)
            val editor = preferences?.edit()
            editor?.putBoolean("isAlert", true)
            editor?.apply()

            val navController = Navigation.findNavController(
                (context as Activity),
                R.id.fragmentNavHost
            )
            navController.navigate(R.id.action_alertFragment_to_mapsFragment)
        }

        if (arguments != null) {
            latitude = arguments?.getDouble("latitude") ?: 0.0
            longitude = arguments?.getDouble("longitude") ?: 0.0
            handleUI(false)
            val currentDate = LocalDateTime.now()
            val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val tomorrowDate = currentDate.plusDays(1).format(dateFormat)
            val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
            val currentTime = LocalDateTime.now().format(timeFormat)
            val hour = LocalDateTime.now().hour
            val amPm: String = if (hour < 12) {
                "AM"
            } else {
                "PM"
            }
            binding.dateET.hint = tomorrowDate
            binding.timeET.hint = "$currentTime $amPm"
        }

        binding.dateET.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    binding.dateET.hint =
                        (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                }, year, month, day
            )
            datePickerDialog.show()
        }

        binding.timeET.setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    var amPm = "AM"
                    if (hourOfDay >= 12) {
                        amPm = "PM"
                    }
                    val hourOf12: Int = if (hourOfDay > 12) {
                        hourOfDay - 12
                    } else if (hourOfDay == 0) {
                        12
                    } else {
                        hourOfDay
                    }
                    binding.timeET.hint =
                        String.format(Locale.getDefault(), "%d:%02d %s", hourOf12, minute, amPm)
                },
                hour,
                minute,
                false
            )

            timePickerDialog.show()
        }

        var typeOfAlarm = "Notification"
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radioButton: RadioButton? = view?.findViewById(checkedId)
            typeOfAlarm = radioButton?.text.toString()
        }
        /*
        alertsViewModel.alerts.observe(this) { alerts ->
            alertsAdapter.submitList(alerts)
        }
        */
        lifecycleScope.launch {
            alertsViewModel.alerts.collectLatest {result ->
                when(result){
                    is AlertState.Loading -> {
                        //binding.progressBar.visibility = View.VISIBLE
                    }
                    is AlertState.Success -> {
                        //binding.progressBar.visibility = View.GONE
                        alertsAdapter.submitList(result.data)
                    }
                    else -> {
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.saveBtn.setOnClickListener {
            val date = binding.dateET.hint
            val dateParts = date.split("-")
            val day = Integer.parseInt(dateParts[0])
            val month = Integer.parseInt(dateParts[1])
            val year = Integer.parseInt(dateParts[2])
            val time = binding.timeET.hint
            val timeParts = time.split(" ")
            val hourMin = timeParts[0]
            val amPm = timeParts[1]

            val hourMinParts = hourMin.split(":")
            val hour = hourMinParts[0].toInt()
            val min = hourMinParts[1].toInt()
            specificDateTime = if (amPm == "AM") {
                LocalDateTime.of(year, month, day, hour, min)
            } else {
                LocalDateTime.of(year, month, day, hour + 12, min)
            }
            val currentDateTime = LocalDateTime.now()

            if (specificDateTime.isBefore(currentDateTime)) {
                Toast.makeText(
                    requireContext(),
                    "Please Select Time In The Future.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                //val countryName = getAddressFromLocation(latitude, longitude)
                val alert = Alert(
                    latitude,
                    longitude,
                    "countryName",
                    binding.dateET.hint.toString(),
                    binding.timeET.hint.toString(),
                    typeOfAlarm
                )
                val duration = calculateSecondsRemaining(specificDateTime)

                alertsViewModel.insertAlert(alert)
                alertsViewModel.lastInsertedId.observe(this) { id ->
                    Log.i("TAG", "onCreateView: $id")
                    if(id != null) {
                        scheduleWorkIfPermissionGranted(latitude, longitude, duration, id.toInt())
                        handleUI(true)
                        alertsViewModel.getStoredAlerts()
                    }
                }
            }
        }

        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateSecondsRemaining(dateTime: LocalDateTime): Long {
        val currentDateTime = LocalDateTime.now()
        val duration = Duration.between(currentDateTime, dateTime)
        return duration.seconds
    }

    private fun getUnits(): String{
        unit = when (unit) {
            "Kelvin" -> {
                ""
            }
            "Celsius" -> {
                "metric"
            }
            "Fahrenheit" -> {
                "imperial"
            }
            else -> {
                ""
            }
        }
        return unit as String
    }
    private fun getLang(): String{
        language = when (language) {
            "Arabic" -> {
                "ar"
            }
            "English" -> {
                "en"
            }
            else -> {
                ""
            }
        }
        return language as String
    }
    /*private fun getAddressFromLocation(latitude: Double, longitude: Double) : String {
        val geocoder = Geocoder(requireContext())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        return addresses?.firstOrNull()?.countryName ?: "Country name not found"
    }
*/
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

       // scheduleWorkIfPermissionGranted()

    }

    private fun handleUI(boolean: Boolean){
        if(boolean){
            binding.alarmSettingLayout.visibility = View.INVISIBLE
        }
        else{
            binding.alarmSettingLayout.visibility = View.VISIBLE
        }
        binding.floatingActionButton.isClickable = boolean
        binding.alertsRecyclerView.isClickable = boolean
        binding.alertsRecyclerView.isFocusable = boolean
        binding.alertsRecyclerView.isFocusableInTouchMode = boolean
    }
    /*
    @RequiresApi(Build.VERSION_CODES.S)
    fun createNotification(context: Context , alert: String): NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "CHANNEL Name"
            val description = "Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("3838", name, importance)
            channel.description = description
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, "3838")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(alert)
                .setContentText("Your image is downloading...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        return builder
    }
*/
    private fun startWorker(latitude: Double, longitude: Double, duration: Long, id: Int) {
        getLang()
        getUnits()
        val workManager = WorkManager.getInstance(requireContext())
    val inputData = workDataOf("latitude" to latitude, "longitude" to longitude, "id" to id, "units" to unit, "lang" to language)
    val request = OneTimeWorkRequestBuilder<MyWorker>()
        .setInputData(inputData)
        .setInitialDelay(duration, TimeUnit.SECONDS)
        .setId(UUID(id.toLong(), 0L))
        .build()

    workManager.enqueue(request)
}
    private fun scheduleWorkIfPermissionGranted(latitude: Double, longitude: Double, duration: Long, id: Int) {
    if (isNotificationPermissionGranted()) {
        startWorker(latitude, longitude, duration, id)
    } else {
        requestNotificationPermission()
    }
}
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().packageName)
            startActivityForResult(intent, notificationPermissionRequestCode)
        } else {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", requireActivity().packageName, null)
            startActivityForResult(intent, notificationPermissionRequestCode)
        }
    }
    private fun isNotificationPermissionGranted(): Boolean {
        return context?.let { NotificationManagerCompat.from(it).areNotificationsEnabled() } ?: false
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == notificationPermissionRequestCode) {
            if (isNotificationPermissionGranted()) {
                //startWorker()
            } else {
                Toast.makeText(requireContext() , "Permission is denied" , Toast.LENGTH_SHORT).show()
             }
        }
    }

    override fun onDeleteAlertClick(alert: Alert) {
        alertsViewModel.deleteAlert(alert)
        WorkManager.getInstance().cancelWorkById(UUID(alert.id.toLong(), 0L))
    }
}



/*
 @SuppressLint("MissingPermission")
 override fun onRequestPermissionsResult(
     requestCode: Int,
     permissions: Array<out String>,
     grantResults: IntArray
 ) {
     super.onRequestPermissionsResult(requestCode, permissions, grantResults)
     when (requestCode) {
         33 -> {
             if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                 notificationManager.notify(33, notificationBuilder.build())
             } else {

             Toast.makeText(requireContext() , "Notify" , Toast.LENGTH_SHORT).show()
             }
         }
     }
 }
*/


/* workManager.getWorkInfoByIdLiveData(request.id).observe(viewLifecycleOwner) {
            when(it.state){
                WorkInfo.State.SUCCEEDED -> {
                    val weatherAlert = it.outputData.getString(Constants.WEATHER_ALERT)
                    if(weatherAlert.equals("clouds" , ignoreCase = true)){
                        Log.i("TAG", "ef Notify: ")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            notificationBuilder = createNotification(requireContext() , "Snow")
                            notificationManager = NotificationManagerCompat.from(requireContext())
                            if (notificationManager.areNotificationsEnabled()) {
                                Log.i("TAG", "af Notify: ")
                                notificationManager.notify(33, notificationBuilder.build())
                            }
                            else {
                                ActivityCompat.requestPermissions(
                                    requireActivity(),
                                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                                    33
                                )
                            }
                        /*if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    ActivityCompat.requestPermissions(
                                        requireActivity(),
                                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                                        33
                                    )
                                }
                            }
                            else{
                                notificationManager.notify(33, notificationBuilder.build())
                                Log.i("TAG", "af Notify: ")
                            }
                            */
                        }
                    }
                }
                else -> {
                    Log.i("TAG", "onViewCreated: "+ it.outputData.getString(Constants.FAILURE_REASON))
                }
            }
        }

    */