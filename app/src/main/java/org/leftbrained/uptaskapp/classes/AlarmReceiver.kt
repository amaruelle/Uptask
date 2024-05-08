package org.leftbrained.uptaskapp.classes

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import org.leftbrained.uptaskapp.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val taskName = intent.getStringExtra("taskName")
        val taskDue = intent.getStringExtra("taskDueDate")
        val taskDesc = intent.getStringExtra("taskDesc")

        val notification = NotificationCompat.Builder(context, "reminders")
            .setContentTitle("$taskName is due on $taskDue!")
            .setContentText("The task description is: $taskDesc")
            .setSmallIcon(R.drawable.icon)
            .build()
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(
            notificationId,
            notification
        )
    }
}