package com.example.weatherforecast

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationBuilderWithBuilderAccessor
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.gson.Gson
import java.io.File
import java.util.concurrent.TimeUnit

class AlertFragment : Fragment() {
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_alert, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        scheduleWorkIfPermissionGranted()
       /* workManager.getWorkInfoByIdLiveData(request.id).observe(viewLifecycleOwner) {
            when(it.state){
                WorkInfo.State.SUCCEEDED -> {
                    val weatherAlert = it.outputData.getString(Constants.WEATHER_ALERT)
                    if(weatherAlert.equals("clouds" , ignoreCase = true)){
                        Log.i("TAG", "ef noti: ")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            notificationBuilder = createNotification(requireContext() , "Snowww")
                            notificationManager = NotificationManagerCompat.from(requireContext())
                            if (notificationManager.areNotificationsEnabled()) {
                                Log.i("TAG", "af noti: ")
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
                                Log.i("TAG", "af noti: ")
                            }
                            */
                        }
                    }
                }
                else -> {
                    Log.i("TAG", "onViewCreateddddddddd: "+ it.outputData.getString(Constants.FAILURE_REASON))
                }
            }
        }

    */}


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
                // تحقق من ما إذا كان المستخدم قد وافق على الإذن أم لا
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // تم منح الإذن، قم بعرض الإشعار
                    notificationManager.notify(33, notificationBuilder.build())
                } else {
                    // رفض المستخدم منح الإذن، قم بمعالجة الحالة هنا (مثلاً، إظهار رسالة توضيحية للمستخدم)
                    // يمكنك أيضًا استدعاء الطلب مرة أخرى للإذن، ولكن يجب على المستخدم إعطاء موافقته يدويًا
                Toast.makeText(requireContext() , "Notifyyyyyyyyyyyyy" , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
*/
private fun startWorker(){
    val workManager = WorkManager.getInstance(requireContext())
    val inputData = workDataOf("latitude" to 89.322, "longitude" to -74.12514593)
    val request = OneTimeWorkRequestBuilder<MyWorker>()
        .setInputData(inputData)
        .setInitialDelay(10 , TimeUnit.SECONDS)
        .build()

    workManager.enqueue(request)
}

private fun scheduleWorkIfPermissionGranted() {
    if (isNotificationPermissionGranted()) {
        startWorker()
    } else {
        requestNotificationPermission()
    }
}
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For Android Oreo and above, you can navigate the user to app settings
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().packageName)
            startActivityForResult(intent, NOTIFICATION_PERMISSION_REQUEST_CODE)
        } else {
            // For older versions, simply navigate the user to notification settings
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", requireActivity().packageName, null)
            startActivityForResult(intent, NOTIFICATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun isNotificationPermissionGranted(): Boolean {
        return context?.let { NotificationManagerCompat.from(it).areNotificationsEnabled() } ?: false
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            // Check if notification permission is granted after user interaction
            if (isNotificationPermissionGranted()) {
                // Permission granted, schedule the work
                startWorker()
            } else {
                Toast.makeText(requireContext() , "Permession is denied" , Toast.LENGTH_SHORT).show()
                // Permission denied, show a message or handle accordingly
                // You may want to inform the user that the feature requires notification permission
            }
        }
    }
}