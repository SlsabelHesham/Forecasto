package com.example.weatherforecast.alert.viewModel

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.weatherforecast.Constants
import com.example.weatherforecast.MainActivity
import com.example.weatherforecast.R
import com.example.weatherforecast.dp.LocationLocalDataSourceImplementation
import com.example.weatherforecast.model.LocationRepository
import com.example.weatherforecast.model.LocationRepositoryImplementation
import com.example.weatherforecast.network.LocationRemoteDataSourceImplementation
import com.example.weatherforecast.network.RetrofitHelper
import com.example.weatherforecast.network.WeatherService
import kotlinx.coroutines.*

class MyWorker(private var context: Context , workerParameters: WorkerParameters) : CoroutineWorker(context , workerParameters) {

    val latitude = inputData.getDouble("latitude", 0.0)
    val longitude = inputData.getDouble("longitude", 0.0)
    val units = inputData.getString("units")
    val lang = inputData.getString("lang")
    val id = inputData.getInt("id", 0)
    private val repo: LocationRepository = LocationRepositoryImplementation.getInstance(
        LocationRemoteDataSourceImplementation(),
        LocationLocalDataSourceImplementation(context)
    )

    private var mediaPlayer: MediaPlayer? = null

    override suspend fun doWork(): Result {
        val apiInstance = RetrofitHelper.retrofitInstance.create(WeatherService::class.java)
        val result = apiInstance.getWeatherForecast(latitude, longitude, "f11bd02e0587e48316013cf38a998f56", units.toString(), lang.toString())
        if (result.isSuccessful) {
            Log.i("TAG", "doWork: ")
            mediaPlayer = MediaPlayer.create(context, R.raw.notification)

            mediaPlayer?.start()
            createNotification(context , result.body()?.list?.get(0)?.weather?.get(0)?.main ?: "not found")
            val alert = repo.getAlertById(id)
            if (alert != null) {
                repo.deleteAlert(alert)
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
            context.startActivity(intent)
            Log.i("TAG", "doWork: after")
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

        val notificationLayout = RemoteViews(context.packageName, R.layout.test_notification)

        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.color.transparent)
                //.setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setContentTitle(alert)
                .setContentText("Weather is $alert")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setFullScreenIntent(pendingIntent, true)

        val notification = builder.build()
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)

        return builder
    }
}