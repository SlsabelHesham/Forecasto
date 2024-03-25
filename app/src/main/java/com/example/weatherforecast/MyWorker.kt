package com.example.weatherforecast

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.PopupWindow
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.weatherforecast.network.RetrofitHelper
import com.example.weatherforecast.network.WeatherService
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers

class MyWorker(private var context: Context , private var workerParameters: WorkerParameters) : CoroutineWorker(context , workerParameters) {

    private var mediaPlayer: MediaPlayer? = null

    override suspend fun doWork(): Result {
        val apiInstance = RetrofitHelper.retrofitInstance.create(WeatherService::class.java)
        val result =
            apiInstance.getWeatherForecast(89.322, -74.12514593, "f11bd02e0587e48316013cf38a998f56")
        if (result.isSuccessful) {
            Log.i("TAG", "doWorkkk: ")
            mediaPlayer = MediaPlayer.create(context, R.raw.notification)

            // Start playback
            mediaPlayer?.start()
            createNotification(context , "snoww")
            if (result.body()!!.list[0].weather[0].main.equals("clouds", ignoreCase = true)) {
                Log.i("TAG", "ef noti: ")
                //showFloatingNotification(context , "snoww")
                /*if (isAppInForeground(applicationContext)) {
                    // Display an alert dialog
                    showAlert("Alert from WorkManager!", applicationContext as Application)
                }*/
            }
        }
        return Result.success(workDataOf(Constants.WEATHER_ALERT to result.body()!!.list[0].weather[0].main))
    }

    @SuppressLint("RemoteViewLayout")
    fun createNotification(context: Context, alert: String): NotificationCompat.Builder {
        val name = "CHANNEL Name"
        val description = "Description"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channelId = "3838"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.nav_graph) // Replace with your navigation graph
            .setDestination(R.id.alertFragment) // Replace with the destination Fragment
            .createPendingIntent()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            notificationManager.createNotificationChannel(channel)
        }

        val contentView = RemoteViews(context.packageName, R.layout.location_layout)
        val notificationLayout = RemoteViews(context.packageName, R.layout.test_notification)

        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.color.transparent)
                //.setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setContentTitle(alert)
                .setContentText("Your image is downloading...")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setFullScreenIntent(pendingIntent, true)

        val notification = builder.build()
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)

        return builder
    }
    fun showFloatingNotification(context: Context, message: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.location_layout, null)

            val width = ViewGroup.LayoutParams.WRAP_CONTENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            val focusable = true // يجعل النافذة قابلة للتفعيل باللمس خارجها

            val popupWindow = PopupWindow(view, width, height, focusable)

            // عرض الإشعار العائم في الوسط
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

            // تعيين نص الإشعار
            //val textViewMessage = view.findViewById<TextView>(R.id.textViewMessage)
            //textViewMessage.text = message
        }
    }
/*
    private fun showNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("HIGH PRIORITY")
                .setContentText("Check this dog puppy video NOW!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)

        notificationManager.notify(666, notificationBuilder.build())
    }*/
    private fun showAlert(message: String, application: Application) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            val dialog = Dialog(application.baseContext)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            //dialog.setContentView(R.layout.dialog_layout)
dialog.setTitle("test")
            //val messageTextView = dialog.findViewById<TextView>(R.id.messageTextView)
            //messageTextView.text = message

            //val okButton = dialog.findViewById<Button>(R.id.okButton)
           /* okButton.setOnClickListener {
                dialog.dismiss()
            }
*/
            dialog.show()
        }
    }
    private fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        val appProcesses = activityManager?.runningAppProcesses ?: return false

        for (appProcess in appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcess.processName == context.packageName) {
                    return true
                }
            }
        }
        return false
    }
}