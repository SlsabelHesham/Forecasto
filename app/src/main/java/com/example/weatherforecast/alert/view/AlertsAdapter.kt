package com.example.weatherforecast.alert.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.AlertsLayoutBinding
import com.example.weatherforecast.model.Alert
import java.time.Duration
import java.time.LocalDateTime

class AlertsAdapter(private val listener: OnDeleteAlertClickListener , val context: Context): ListAdapter<Alert, AlertsAdapter.AlertsViewHolder>(AlertsDiffUtil()){

    private lateinit var binding: AlertsLayoutBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertsViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = AlertsLayoutBinding.inflate(inflater, parent, false)
        return AlertsViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AlertsViewHolder, position: Int) {
        val currentAlert = getItem(position)
        holder.binding.countryNameTV.text = currentAlert.cityName

        val specificDateTime: LocalDateTime
        val date = currentAlert.date
        val dateParts = date.split("-")
        val day = Integer.parseInt(dateParts[0])
        val month = Integer.parseInt(dateParts[1])
        val year = Integer.parseInt(dateParts[2])
        val time = currentAlert.time
        val timeParts = time.split(" ")
        val hourMin = timeParts[0]
        val amPm = timeParts[1]

        val hourMinParts = hourMin.split(":")
        val hour = hourMinParts[0].toInt()
        val min = hourMinParts[1].toInt()
        specificDateTime = if (amPm == "AM" && hour == 12) {
            LocalDateTime.of(year, month, day, 0, min)
        } else if (hour == 12) {
            LocalDateTime.of(year, month, day, 12, min)
        } else if (amPm == "AM") {
            LocalDateTime.of(year, month, day, hour, min)
        } else {
            LocalDateTime.of(year, month, day, hour + 12, min)
        }
        val seconds = calculateSecondsRemaining(specificDateTime)
        val timeArray = convertSecondsToDHM(seconds)
        val days = timeArray.first
        val hours = timeArray.second
        val minutes = timeArray.third
        var text = ""
        if(minutes > 0){
            text = if(hours> 0){
                if(days > 0){
                    "Alert in ${if (days == 1L) "1 day" else "$days days"}, ${if (hours == 1L) "1 hour" else "$hours hours"}, and ${if (minutes == 1L) "1 minute" else "$minutes minutes"}"
                }else{
                    "Alert in ${if (hours == 1L) "1 hour" else "$hours hours"}, and ${if (minutes == 1L) "1 minute" else "$minutes minutes"}"
                }
            }else{
                "Alert in ${if (minutes == 1L) "1 minute" else "$minutes minutes"}"
            }
        }
        holder.binding.durationTV.text = text
        holder.binding.typeTV.text = currentAlert.type

        if(currentAlert.type == "Notification"){
            holder.binding.alertIcon.setImageResource(R.drawable.notification)
        }else{
            holder.binding.alertIcon.setImageResource(R.drawable.alarm)
        }

        holder.binding.cancelBtn.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle(R.string.deleteAlert)
                .setMessage(R.string.deleteAlertMSG)
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    listener.onDeleteAlertClick(currentAlert)
                }
                .setNegativeButton(android.R.string.no) { _, _ ->

                }
                .setIcon(R.drawable.alarm)
                .show()
        }
    }

private fun convertSecondsToDHM(seconds: Long): Triple<Long, Long, Long> {
    var remainingSeconds = seconds
    val days = remainingSeconds / (60 * 60 * 24)
    remainingSeconds -= days * (60 * 60 * 24)
    val hours = remainingSeconds / (60 * 60)
    remainingSeconds -= hours * (60 * 60)
    val minutes = remainingSeconds / 60
    return Triple(days, hours, minutes)
}
    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateSecondsRemaining(dateTime: LocalDateTime): Long {
        val currentDateTime = LocalDateTime.now()
        val duration = Duration.between(currentDateTime, dateTime)
        return duration.seconds
    }

    class AlertsViewHolder(var binding: AlertsLayoutBinding) : RecyclerView.ViewHolder(binding.root)

}

class AlertsDiffUtil : DiffUtil.ItemCallback<Alert>(){
    override fun areItemsTheSame(oldItem: Alert, newItem: Alert): Boolean {
        return oldItem.latitude == newItem.latitude
    }
    override fun areContentsTheSame(oldItem: Alert, newItem: Alert): Boolean {
        return oldItem == newItem
    }
}