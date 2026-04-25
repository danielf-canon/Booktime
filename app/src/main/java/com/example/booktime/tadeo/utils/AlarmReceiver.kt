package com.example.booktime.tadeo.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.example.booktime.tadeo.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            // Solo reprogramar al reiniciar si hay un usuario logueado
            if (userId != null) {
                reprogramAlarm(context, userId)
            }
            return
        }

        // Si es una alarma de recordatorio, mostrar notificación y reprogramar
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showReminderNotification()
        
        if (userId != null) {
            reprogramAlarm(context, userId)
        }
    }

    private fun reprogramAlarm(context: Context, userId: String) {
        val prefs = context.getSharedPreferences("booktime_settings_$userId", Context.MODE_PRIVATE)
        val isEnabled = prefs.getBoolean("reading_reminders", true)
        
        val defaultFreq = context.getString(R.string.reminder_frequency_daily)
        val defaultDay = context.getString(R.string.day_monday)
        
        val frequency = prefs.getString("reminder_frequency", defaultFreq) ?: defaultFreq
        val time = prefs.getString("reminder_time", "20:00") ?: "20:00"
        val dayOfWeek = prefs.getString("reminder_day", defaultDay) ?: defaultDay
        
        if (isEnabled) {
            ReminderScheduler.scheduleReminder(context, isEnabled, frequency, time, dayOfWeek)
        }
    }
}
