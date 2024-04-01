package com.example.weatherforecast

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.TextView
import com.example.weatherforecast.databinding.ActivityNotificationBinding

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.rootView.setBackgroundColor(Color.TRANSPARENT)

        mediaPlayer = MediaPlayer.create(this, R.raw.weather_alert)
        mediaPlayer?.setLooping(true);
        mediaPlayer?.start()
        val city = intent.getStringExtra("city")
        val date = intent.getStringExtra("date")
        val time = intent.getStringExtra("time")
        val condition = intent.getStringExtra("condition")
        val description = intent.getStringExtra("description")

        binding.weatherConditionTV.text = "$city\n$date  $time\n$condition!\n$description."

        binding.stopBtn.setOnClickListener {
            mediaPlayer?.stop()
            finish()
        }
    }
}