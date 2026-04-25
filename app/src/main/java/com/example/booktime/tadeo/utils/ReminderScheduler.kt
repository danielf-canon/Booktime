package com.example.booktime.tadeo.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.*
import com.example.booktime.tadeo.R

object ReminderScheduler {
    private const val ALARM_REQUEST_CODE = 1002

    fun scheduleReminder(
        context: Context,
        isEnabled: Boolean,
        frequency: String,
        time: String,
        dayOfWeek: String? = null
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (!isEnabled) {
            alarmManager.cancel(pendingIntent)
            Log.d("ReminderScheduler", "Reminder cancelled")
            return
        }

        val calendar = Calendar.getInstance().apply {
            try {
                val timeParts = time.split(":")
                if (timeParts.size == 2) {
                    set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
                    set(Calendar.MINUTE, timeParts[1].toInt())
                } else {
                    set(Calendar.HOUR_OF_DAY, 20)
                    set(Calendar.MINUTE, 0)
                }
            } catch (e: Exception) {
                Log.e("ReminderScheduler", "Error parsing time: $time", e)
                set(Calendar.HOUR_OF_DAY, 20)
                set(Calendar.MINUTE, 0)
            }
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // Si la hora ya pasó hoy, programar para mañana
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        // Ajustar según frecuencia
        val dailyStr = context.getString(R.string.reminder_frequency_daily)
        val weeklyStr = context.getString(R.string.reminder_frequency_weekly)

        when (frequency) {
            weeklyStr -> {
                dayOfWeek?.let {
                    val targetDay = when (it) {
                        context.getString(R.string.day_monday) -> Calendar.MONDAY
                        context.getString(R.string.day_tuesday) -> Calendar.TUESDAY
                        context.getString(R.string.day_wednesday) -> Calendar.WEDNESDAY
                        context.getString(R.string.day_thursday) -> Calendar.THURSDAY
                        context.getString(R.string.day_friday) -> Calendar.FRIDAY
                        context.getString(R.string.day_saturday) -> Calendar.SATURDAY
                        context.getString(R.string.day_sunday) -> Calendar.SUNDAY
                        else -> Calendar.MONDAY
                    }
                    
                    while (calendar.get(Calendar.DAY_OF_WEEK) != targetDay) {
                        calendar.add(Calendar.DAY_OF_YEAR, 1)
                    }
                }
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    // Fallback a inexacto si no tiene permiso
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
            Log.d("ReminderScheduler", "Reminder scheduled for ${calendar.time} (Frequency: $frequency)")
        } catch (e: Exception) {
            Log.e("ReminderScheduler", "Error scheduling alarm", e)
        }
    }
}
